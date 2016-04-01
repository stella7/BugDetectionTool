import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/*Class Description: create callgraph by adding nodes and edges*/
public class CallGraph {
	//functions<ID, name>
	private HashMap<Integer, String> functions = new HashMap<Integer, String>();
	//graphs<calleeID,Set<callerIDs>>
	private HashMap<Integer, HashSet<Integer>> graph = new HashMap<Integer, HashSet<Integer>>();
	
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
	
	public HashMap<Integer, String> getFunctions(){
		return functions;
	}
	
	public HashMap<Integer, HashSet<Integer>> getGraph(){
		return graph;
	}
	
}
