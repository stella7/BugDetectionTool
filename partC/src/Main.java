import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	// Set default values for T_SUPPORT and T_CONFIDENCE
	private static int T_SUPPORT = 3;
	private static double T_CONFIDENCE = (double) 65/100;
	private static int Expand_level = 0;

	
	// <HashMap> graph with callees' names as keys, the set of its callers' names as values
	static HashMap<String, HashSet<String>> graph = new HashMap<String, HashSet<String>>();
	static HashMap<String, HashSet<String>> graphcopy = new HashMap<String, HashSet<String>>();


	/* Main function */
	public static void main(String[] args){
		String fileName = "";

		if (args.length > 0){
			fileName = args[0];
			
			switch(args.length){
			case 1:				// filename with default value of T_SUPPORT, T_CONFIDENCE
				break;
			case 2:
				Expand_level = Integer.parseInt(args[1]);	// filename with expand levels
				break;
			case 3:				// filename with T_SUPPORT, T_CONFIDENCE
				T_SUPPORT = Integer.parseInt(args[1]);
				T_CONFIDENCE = Double.parseDouble(args[2])/100;
				break;	
			case 4:				// filename with T_SUPPORT, T_CONFIDENCE, expand levels
				T_SUPPORT = Integer.parseInt(args[1]);
				T_CONFIDENCE = Double.parseDouble(args[2])/100;
				Expand_level = Integer.parseInt(args[3]);
				break;	
			default:			// Illegal input format
				System.out.println("Error: Wrong Number of Input Arguments");
				System.exit(-1);
			}

		}else {			// without any arguments
			System.out.println("Error: Arument needs to contain at least callgraph file name");
			System.exit(-1);
		}

		//Parse each line of call graph 
		CallGraph callgraph = new CallGraph();
		graph = callgraph.getGraph();
		graphcopy = callgraph.getGraphcopy();
		Parse(fileName,callgraph);
		
		//Calculate confidences and detect bugs
		CalConfidence con = new CalConfidence();
		con.PairConfidence(graph, graphcopy, T_SUPPORT, T_CONFIDENCE);
		
	}//end of main method
	
	/* Parsing each line of the file, identify callers and callees */
	static void Parse(String fileName, CallGraph callgraph){
		// Pattern of caller
		Pattern nodePattern = Pattern.compile("Call graph node for function: '(.*?)'<<.*>>  #uses=(\\d*).*$");
		// Pattern of callee
		Pattern functionPattern = Pattern.compile("CS<.*> calls function '(.*?)'.*$");
		Matcher nodeMacher, functionMatcher;
		
		String callerName = "";
		String calleeName;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String currentLine = null;
			currentLine = br.readLine();
			
			//skip the <<null function>> node
			while(!currentLine.isEmpty()){
				currentLine = br.readLine();
			}
			 
			while((currentLine = br.readLine()) != null){
				//Store caller's name
				nodeMacher = nodePattern.matcher(currentLine);
				if(nodeMacher.find()){
					callerName = nodeMacher.group(1);
				}
				//Store callee's callers
				functionMatcher = functionPattern.matcher(currentLine);
				if(functionMatcher.find()){
					calleeName = functionMatcher.group(1);
					callgraph.createGraph(calleeName, callerName);		//Map callee and caller
				}
			}//end of while
			br.close();
			
			//Get all functions and their callers
			if(Expand_level > 0){
				for(String callee : graphcopy.keySet()){
					HashSet <String> fathercallers = new HashSet <String>();
					callgraph.addfathercallers(callee, fathercallers, Expand_level);

					graphcopy.get(callee).addAll(fathercallers);

					//System.out.printf("\n");
				}
			}		
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
	}//end of parse
	
}//end of class
