import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/* Class Description: Calculating support and confidence of each pair of two functions */
public class CalConfidence {
	HashMap<Pair, Set<Integer>> orderedBug = new HashMap<Pair, Set<Integer>>();
	HashMap<Integer, String> functions;
	int supportPair;

	public CalConfidence(HashMap<Integer, String> functions) {
		// TODO Auto-generated constructor stub
		this.functions = functions;
	}

	public void orderedPairConfidence(HashMap<Integer,HashMap<Integer, Integer>> orderedGraph, // The hashmap of each callee with caller set
			int support,			// The support parameter from the input arguments
			double confidence){		// The confidence parameter from the input arguments
		
		/* For pair(Foo,Bar), each iteration do
		 * calculate support and confidence of Foo
		 * find bug locations of Foo
		 */
		for(Integer keyFoo: orderedGraph.keySet()){				
			String calleeFoo = functions.get(keyFoo);
			HashMap<Integer, Integer> caller1Map = orderedGraph.get(keyFoo); 
			int supportFoo = caller1Map.size();
			Set<Integer> callersFoo = caller1Map.keySet();
			//System.out.println(calleeFoo+":"+callersFoo);
			for(Integer keyBar: orderedGraph.keySet()){
				String calleeBar = functions.get(keyBar);
				
				if(keyFoo != keyBar){
					/* Compare caller sets of Foo and Bar
					 * The number of common nodes is the pair(Foo,Bar) support
					 */
					HashMap<Integer, Integer> caller2Map = orderedGraph.get(keyBar); //Bar's caller set
					int supportBar = caller2Map.size();
					Set<Integer> tmp = new HashSet<Integer>();
					Set<Integer> callersBar = caller2Map.keySet();
					//System.out.println(calleeBar+":"+callersBar);
					tmp.addAll(callersFoo);
					//System.out.println(calleeFoo+calleeBar);
					// Set the common nodes
					tmp.retainAll(callersBar);
					//System.out.println("tmp:"+tmp);
					supportPair = tmp.size();
					//System.out.println(supportPair);
					double confidenceFoo = (double)supportPair/supportFoo;	// Confidence of Foo on pair(Foo,Bar)
                    double confidenceBar = (double)supportPair/supportBar;// Confidence of Bar on pair(Foo,Bar)
                    double newconfidence = 2/(1/confidenceFoo + 1/confidenceBar);

					if(supportPair >= support){
						//System.out.println(confidenceFoo);
						if(confidenceFoo >= confidence){
                            if(newconfidence >= confidence){
								// The nodes not in common set are labeled as bugs
								Set<Integer> tmpFoo = new HashSet<Integer>();
								tmpFoo.addAll(callersFoo);
								tmpFoo.removeAll(tmp); //Set of nodes not in common set
								if(!tmpFoo.isEmpty()){
									for(Integer id:tmpFoo){
										String callerFoo = functions.get(id);
										int compare = calleeFoo.compareTo(calleeBar);	// Sort the pair functions' names
										if(compare < 0){
											System.out.printf("bug: %s in %s, pair: (%s, %s), support: %d, confidence: %.2f%%\n", 
													calleeFoo, callerFoo, calleeFoo, calleeBar, supportPair, confidenceFoo*100);
										}else{
											System.out.printf("bug: %s in %s, pair: (%s, %s), support: %d, confidence: %.2f%%\n", 
													calleeFoo, callerFoo, calleeBar, calleeFoo, supportPair, confidenceFoo*100);
										}
										
									}
								}
							}
						}
						
					}
					
					// Detect order
					Set<Integer> order1 = new HashSet<Integer>();
					Set<Integer> order2 = new HashSet<Integer>();

					for(Integer pair : tmp){
						if(caller1Map.get(pair) < caller2Map.get(pair)){
							order1.add(pair); 
						}else{
							order2.add(pair);
						}
					}
					double orderedConfidence;
					
					Set<Integer> bugSet = new HashSet<Integer>();
					//int pairSupport = order1.size() + order2.size();
					//if(confidenceFoo >= confidence){
                        //if(newconfidence >= confidence){
							if(order1.size() > order2.size()){
								int trueOrderSupport = order1.size();
								orderedConfidence = (double) order1.size()/supportPair;
								if(orderedConfidence >= confidence){
										bugSet.addAll(order2);
										Pair pair = new Pair(calleeFoo, calleeBar, supportPair, trueOrderSupport);
										if(orderedBug.containsKey(pair)){
											continue;
										}
										orderedBug.put(pair, bugSet);
								}							
							}else{
								int trueOrderSupport = order2.size();
								orderedConfidence = (double) order2.size()/supportPair;
								if(orderedConfidence >= confidence){
									bugSet.addAll(order1);
									Pair pair = new Pair(calleeBar, calleeFoo, supportPair, trueOrderSupport);
									if(orderedBug.containsKey(pair)){
										continue;
									}
									orderedBug.put(pair, bugSet);
								}					
							}
                        //}
					//}			
				}
			}//end of inner for
		}//end of outer for
	}
	
	public void printOrderBugs(){
		
		for(Entry<Pair, Set<Integer>> entry: orderedBug.entrySet()){
			Pair pair = entry.getKey();
			String pair1 = pair.getPair1();
			String pair2 = pair.getPair2();
			int supportPair = pair.getSupport();
			double orderedConfidence = pair.getConfidence();
			Set<Integer> callerId = entry.getValue();
			for(Integer id: callerId){
				String caller = functions.get(id);
				System.out.printf("bug pair: (%s, %s) in %s, pair support: %d, pair confidence: %.2f%%\n", 
					pair2, pair1, caller, supportPair, orderedConfidence*100);
			}			
		}
		
		//System.out.println(orderedBug);
	}
	
	public class Pair{
		private String pair1;
		private String pair2;
		private int support;
		private int trueSupport;
		public Pair(String pair1, String pair2, Integer support, Integer trueSupport){
			this.pair1 = pair1;
			this.pair2 = pair2;
			this.support = support;
			this.trueSupport = trueSupport;
		}
		
		public String getPair1(){
			return this.pair1;
		}
		public String getPair2(){
			return this.pair2;
		}
		public int getSupport(){
			return this.support;
		}
		public double getConfidence(){
			return (double)this.trueSupport/this.support;
		}
		
		@Override
		public int hashCode(){
			final int prime = 31;
	        int result = 1;
	        result = prime * result
	                + ((pair1 == null) ? 0 : pair1.hashCode());
	        result = prime * result
	                + ((pair2 == null) ? 0 : pair2.hashCode());
	        result = prime * result + support;
    		result = prime * result + trueSupport;
	        return result;
		}
		
		@Override
		public boolean equals(Object obj){
			if(this == obj)
	            return true;
	        if((obj == null) || (obj.getClass() != this.getClass()))
	            return false;
	        // object must be Test at this point
	        Pair test = (Pair)obj;
	        return support == test.support && trueSupport == test.trueSupport &&
	        (pair1 == test.pair1 || (pair1 != null && pair1.equals(test.pair1))) &&
	        (pair2 == test.pair2 || (pair2 != null && pair2.equals(test.pair1)));
		}
	}
	
}
