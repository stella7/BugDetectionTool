import java.util.HashMap;
import java.util.HashSet;

/*Class Description: create callgraph by adding nodes and edges*/
public class CallGraph {
	//functions<ID, name>
	private HashMap<Integer, String> functions = new HashMap<Integer, String>();
	//graphs<calleeID,Set<callerIDs>>
	private HashMap<Integer, HashSet<Integer>> graph = new HashMap<Integer, HashSet<Integer>>();
	private HashMap<Integer,HashMap<Integer, Integer>> orderedGraph = new HashMap<Integer,HashMap<Integer, Integer>>();

	//Add a new node to the callgraph
	public void addToFunctionSet(String newFunction){
		int newId = newFunction.hashCode();
		functions.put(newId, newFunction);
	}
	//Add an edge between callee and caller
	public void createGraph(String calleeName, String callerName){
		int calleeId = calleeName.hashCode();
		int callerId = callerName.hashCode();
		if (!graph.containsKey(calleeId)){
			graph.put(calleeId, new HashSet<Integer>());
		}
		graph.get(calleeId).add(callerId);		
	}
	
	// Store caller with the position that callee appears{A:{scope1:1},{scope2:1},{scope5:3}}
	public void createOrderedGraph(String calleeName, String callerName, Integer index){
		int calleeId = calleeName.hashCode();
		int callerId = callerName.hashCode();
		if (!orderedGraph.containsKey(calleeId)){
			HashMap<Integer, Integer> callerMap = new HashMap<Integer, Integer>();
			callerMap.put(callerId, index);
			orderedGraph.put(calleeId, callerMap);
		} else{
			orderedGraph.get(calleeId).put(callerId, index);
		}
		
	}
	
	public HashMap<Integer, String> getFunctions(){
		return functions;
	}
	
	public HashMap<Integer, HashSet<Integer>> getGraph(){
		return graph;
	}
	
	public HashMap<Integer, HashMap<Integer, Integer>> getOrderedGraph(){
		return orderedGraph;
	}
}
