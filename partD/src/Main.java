import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	// Set default value for T_SUPPORT and T_CONFIDENCE
	public static int T_SUPPORT = 3;
	private static double T_CONFIDENCE = 65;
	// <HashMap> functions with functions' IDs(hashcode) as keys, names as values
	static HashMap<Integer, String> functions = new HashMap<Integer, String>();
	// <HashMap> graph with callees' IDs(hashcode) as keys, the set of its callers' IDs as values
	static HashMap<Integer, HashSet<Integer>> graph = new HashMap<Integer, HashSet<Integer>>();
	static HashMap<Integer,HashMap<Integer, Integer>> orderedGraph = new HashMap<Integer,HashMap<Integer, Integer>>();

	/* Main function */
	public static void main(String[] args){
		int support = T_SUPPORT;
		double confidence = T_CONFIDENCE/100;
		String fileName = "";
		
		if (args.length > 0){
			fileName = args[0];
			
			switch(args.length){
			case 1:				// filename with default value of T_SUPPORT, T_CONFIDENCE
				break;
			case 3:				// filename with T_SUPPORT, T_CONFIDENCE
				support = Integer.parseInt(args[1]);
				confidence = Double.parseDouble(args[2])/100;
				break;	
			default:			// Illegal input format
				System.out.println("Error: Wrong Number of Input Arguments");
				System.exit(-1);
			}

		}else {			// without any arguments
			System.out.println("Error: Arument needs to contain at least callgraph file name");
			System.exit(-1);
		}

		CallGraph callGraph = new CallGraph();
		Parse(fileName,callGraph);
		CalConfidence con = new CalConfidence(functions);
		con.orderedPairConfidence(orderedGraph, support, confidence);
		con.printOrderBugs();
	}//end of main method
	
	/* Parsing each line of the file, identify callers and callees */
	static void Parse(String fileName, CallGraph callgraph){
		// Pattern of caller
		Pattern nodePattern = Pattern.compile("Call graph node for function: '(.*?)'<<.*>>  #uses=(\\d*).*$");
		// Pattern of callee
		Pattern functionPattern = Pattern.compile("CS<(.*)> calls function '(.*?)'.*$");

		String callerName = "";
		String calleeName = "";
		String currentLine = null;
		Integer index = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
			currentLine = br.readLine();
			
			//skip the <<null function>> node
			while((currentLine != null) && (!currentLine.isEmpty())){
				currentLine = br.readLine();
			}
			
			while((currentLine = br.readLine()) != null){
				Matcher nodeMacher = nodePattern.matcher(currentLine);
				if(nodeMacher.find()){
					index = 0;
					callerName = nodeMacher.group(1);
					callgraph.addToFunctionSet(callerName);		//Add caller's ID and name into functions
				}
				Matcher functionMatcher = functionPattern.matcher(currentLine);
				if(functionMatcher.find()){
					calleeName = functionMatcher.group(2);
					callgraph.addToFunctionSet(calleeName);		//Add callee's ID and name into functions
					callgraph.createGraph(calleeName, callerName);	//Map callee and caller
					callgraph.createOrderedGraph(calleeName, callerName, index);
					index = index + 1;
				}
			}//end of while
			
			br.close();
			
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
		
		functions = callgraph.getFunctions();		
		graph = callgraph.getGraph();
		orderedGraph = callgraph.getOrderedGraph();
		
		//System.out.println(orderedGraph);
		/***
		for (Entry<Integer, HashMap<Integer, Integer>> entry : orderedGraph.entrySet()){
			HashMap<Integer, Integer> value = entry.getValue();
			System.out.println(value);
		}
		***/
		
		
	}//end of parse
	
	
}//end of class
