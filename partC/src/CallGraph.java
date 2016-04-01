import java.util.HashMap;
import java.util.HashSet;

/*Class Description: create callgraph by adding nodes and edges*/
public class CallGraph {
	//graphs<callee,Set<callers>>
	private HashMap<String, HashSet<String>> graph = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> graphcopy = new HashMap<String, HashSet<String>>();

	//Add an edge between callee and caller
	public void createGraph(String calleeName, String callerName){
		if (!graph.containsKey(calleeName)){
			graph.put(calleeName, new HashSet<String>());
			graphcopy.put(calleeName, new HashSet<String>());
		}
		graph.get(calleeName).add(callerName);	
		graphcopy.get(calleeName).add(callerName);	
	}
	
	//Add an edge between callee and fathercaller
	public void addfathercallers(String calleeName, HashSet <String> fathercallers, int Expand_level){
		//System.out.printf("ee:%s\n",calleeName);
		//System.out.printf("level:%d\n",Expand_level);

		if(Expand_level > 0){
			for(String callerName : graph.get(calleeName)){
				//System.out.printf("er:%s\n",callerName);
				if(graph.containsKey(callerName)){
					for(String fathercallerName : graph.get(callerName)){
						if (graph.containsKey(fathercallerName)){
							//System.out.printf("fer:%s\n",fathercallerName);
							//graphcopy.get(callerName).remove(fathercallerName);
							fathercallers.add(fathercallerName); 
						}
					}
					addfathercallers(callerName, fathercallers, Expand_level-1);
				}
			}
		}
	}
	
	public HashMap<String, HashSet<String>> getGraph(){
		return graph;
	}
	
	public HashMap<String, HashSet<String>> getGraphcopy(){
		return graphcopy;
	}
}
	
