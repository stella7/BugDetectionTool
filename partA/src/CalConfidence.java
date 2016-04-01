import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/* Class Description: Calculating support and confidence of each pair of two functions */
public class CalConfidence {

	public void PairConfidence(HashMap<Integer, HashSet<Integer>>graph, // The hashmap of each callee with caller set
			HashMap<Integer, String> functions, // Hashmap functions used for finding function name by its ID
			int support,			// The support parameter from the input arguments
			double confidence){		// The confidence parameter from the input arguments
		
		/* For pair(Foo,Bar), each iteration do
		 * calculate support and confidence of Foo
		 * find bug locations of Foo
		 */
		for(Integer keyFoo: graph.keySet()){				
			String calleeFoo = functions.get(keyFoo);
			Set<Integer> callersFoo = graph.get(keyFoo); //Foo's caller set
			int supportFoo = callersFoo.size();
			
			for(Integer keyBar: graph.keySet()){
				String calleeBar = functions.get(keyBar);
				
				if(keyFoo != keyBar){
					/* Compare caller sets of Foo and Bar
					 * The number of common nodes is the pair(Foo,Bar) support
					 */
					Set<Integer> callersBar = graph.get(keyBar); //Bar's caller set
					Set<Integer> tmp = new HashSet<Integer>();
					tmp.addAll(callersFoo);
					
					// Set the common nodes
					tmp.retainAll(callersBar);					
					int supportPair = tmp.size();
					
					if(supportPair >= support){
						
						double confidenceFoo = (double)supportPair/supportFoo;	// Confidence of Foo on pair(Foo,Bar)

						if(confidenceFoo >= confidence){
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
			}//end of inner for
		}//end of outer for
	}
	
}
