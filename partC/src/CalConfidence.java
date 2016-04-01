import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/* Class Description: Calculating support and confidence of each pair of two functions */
public class CalConfidence {

	public void PairConfidence(
			HashMap<String, HashSet<String>> graph, 	// The hashmap of each callee with caller set
			HashMap<String, HashSet<String>> graphcopy, 	// The hashmap of each callee with fathercaller set
			int T_SUPPORT,	// The T_SUPPORT parameter from the input arguments
			double T_CONFIDENCE){		// The T_CONFIDENCE parameter from the input arguments

		/* For pair(Foo, Bar), each iteration do
		 * calculate support and confidence of Foo
		 * find bug locations of Foo
		 */
		for(String calleeFoo: graph.keySet()){
			Set<String> callersFoo, callersBar, tmp, tmpFoo;
			int supportFoo, supportPair, compare;
			double confidenceFoo;
			
			callersFoo = graph.get(calleeFoo);
			tmp = new HashSet<String>();	
			tmpFoo = new HashSet<String>();			
			supportFoo = callersFoo.size();
			
			for(String calleeBar: graph.keySet()){				
				if(!calleeFoo.equals(calleeBar)){
					/* Compare caller sets of Foo and Bar
					 * The number of common nodes is the pair(Foo,Bar) support
					 */
					callersBar = graph.get(calleeBar);
					tmp.addAll(callersFoo);

					// Set the common nodes
					tmp.retainAll(callersBar);					
					supportPair = tmp.size();
					
					if(supportPair >= T_SUPPORT){
						confidenceFoo = (double)supportPair/supportFoo;// Confidence of Foo on pair(Foo,Bar)
						if(confidenceFoo >= T_CONFIDENCE){
							// The nodes not in common set are labeled as bugs
							tmpFoo.addAll(callersFoo);
							tmpFoo.removeAll(tmp);		//Set of nodes not in common set
							if(!tmpFoo.isEmpty()){
								for(String callerFoo:tmpFoo){
									if(!graphcopy.get(calleeBar).contains(callerFoo)){
										compare = calleeFoo.compareTo(calleeBar);		// Sort the pair functions' names
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
				}
			}//end of inner for
		}//end of outer for
	}
	
}
