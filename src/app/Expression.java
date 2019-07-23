package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	
    	expr = expr.replaceAll(" ", ""); 										// deletes spaces
    	expr = expr.replaceAll("\t", ""); 										// deletes tabs
    	
    	StringTokenizer toke = new StringTokenizer(expr, delims, true); 		// master Tokenizer
    	
    	StringTokenizer toke1 = new StringTokenizer(expr, delims, true); 		// "*" current token checker
    	StringTokenizer toke2 = new StringTokenizer(expr, delims, true); 		// "+" current token checker
    	StringTokenizer toke3 = new StringTokenizer(expr, delims, true); 		// "-" current token checker
    	StringTokenizer toke4 = new StringTokenizer(expr, delims, true); 		// "/" current token checker
    	StringTokenizer toke5 = new StringTokenizer(expr, delims, true); 		// "(" current token checker
    	StringTokenizer toke6 = new StringTokenizer(expr, delims, true); 		// ")" current token checker
    	StringTokenizer toke7 = new StringTokenizer(expr, delims, true); 		// "[" current token checker
    	StringTokenizer toke8 = new StringTokenizer(expr, delims, true); 		// "]" current token checker
    	StringTokenizer toke9 = new StringTokenizer(expr, delims, true); 		// "[" next token checker
    	String stringToke9 = toke9.nextToken();
    	
    	for (int numTokes = toke.countTokens(); numTokes != 0; numTokes--){		// loop that populates ArrayLists
    		
    		if (toke1.nextToken() != "*" ||
    				toke2.nextToken() != "+" ||
    				toke3.nextToken() != "-" ||
    				toke4.nextToken() != "/" || 								// then array or variable
    				toke5.nextToken() != "(" ||
    				toke6.nextToken() != ")" ||
    				toke7.nextToken() != "[" ||
    				toke8.nextToken() != "]"){
    			
    			if (toke9.hasMoreTokens()){
    				
    				stringToke9 = toke9.nextToken();
    			}
    			
    			if (stringToke9.equals("[")){ 									// then array
    				
    				String arrName = toke.nextToken(); 							// create name string
    				Array arr = new Array(arrName); 							// create Array object with correct name
    				
    				if (arrays.contains(arr)){ 									// check "arrays" ArrayList for new object
    					
    					continue; 												// already exists - skip
    				}
    				
    				else {
    					
    					arrays.add(arr); 										// doesn't exist yet - generate
    				}
    			}
    			
    			else { 															// then variable
    				
    				String varName = toke.nextToken(); 							// create name string
    				Variable var = new Variable(varName); 						// create Variable object with correct name
    				
    				if (vars.contains(var)){ 									// check "vars" ArrayList for new object
    					
    					continue; 												// already exists - skip
    				}
    				
    				else {
    					
    					vars.add(var); 											// doesn't exist yet - generate    				
    				}
    			}
    		}
    	}
    	
		//temporary code-testing lines
    	//System.out.println(arrays.toString());
    	//System.out.println(vars.toString());
    	
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	
    	expr = expr.replaceAll(" ", "");		// delete spaces
    	expr = expr.replaceAll("\t", "");		// delete tabs	
    	
    	return rEvaluate(expr, vars, arrays, 0, expr.length() - 1);
    }
    
    private static float
    rEvaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays, int begex, int endex){
    	
    	String currExpr = expr.substring(begex, endex + 1);
    	String solvedExpr = null;
    	String nextExpr = null;
    	
    	int[] levelsData = getLevelsData(expr, begex, endex);
    	
    	int nextLevelBegex = levelsData[0];		
    	int nextLevelEndex = levelsData[1];		
    	int nextLevelCount = levelsData[2];		

    	if (nextLevelBegex == -1){ 	
    		
    		float answer = crunchSimpleExpr(currExpr, vars);
			
			if (answer == -0.0){
				
				return (float)0.0;
			}
    		
    		return crunchSimpleExpr(currExpr, vars);	
    	}
    	
    	else if (nextLevelCount == 1 && expr.charAt(nextLevelBegex) == '('){
    		
    		nextExpr = expr.substring(nextLevelBegex + 1, nextLevelEndex);   		
    		
    		if (nextExpr.contains("*") == false && 
					nextExpr.contains("/") == false && 
    				nextExpr.contains("+") == false && 
    				nextExpr.contains("-") == false &&
	    			nextExpr.contains("[") == false){
    			
    			nextExpr = nextExpr.replaceAll("\\)", "");
    			nextExpr = nextExpr.replaceAll("\\(", "");
    			
    			if (expr.length() -1 >= nextLevelEndex + 2){
    			
    				solvedExpr = String.valueOf(crunchSimpleExpr(nextExpr, vars)) +
    						expr.substring(nextLevelEndex + 1, nextLevelEndex + 2);
    				
    				nextExpr = expr.substring(nextLevelEndex + 2, endex + 1);
    				
    				if (nextExpr.contains("*") == false && 
    						nextExpr.contains("/") == false && 
    	    				nextExpr.contains("+") == false && 
    	    				nextExpr.contains("-") == false &&
	    	    			nextExpr.contains("[") == false){
    	    			
    	    			nextExpr = nextExpr.replaceAll("\\)", "");
    	    			nextExpr = nextExpr.replaceAll("\\(", "");
    	    			
    	    			currExpr = expr.substring(begex, nextLevelBegex) + solvedExpr +
    	    					String.valueOf(crunchSimpleExpr(nextExpr, vars));
    				}
    				
    				else{
    					
    					currExpr = expr.substring(begex, nextLevelBegex) + solvedExpr + 
        					String.valueOf(rEvaluate(expr, vars, arrays, nextLevelEndex + 2, endex + 1)); 
    				}
    			}
    			
    			else{
    				
    				currExpr = expr.substring(begex, nextLevelBegex) + 
    						String.valueOf(crunchSimpleExpr(nextExpr, vars));
    			}  			   			   			
    			
    			float answer = crunchSimpleExpr(currExpr, vars);
    			
    			if (answer == -0.0){
    				
    				return (float)0.0;
    			}
    			
    			return crunchSimpleExpr(currExpr, vars);
    			
    		}
    		   		
    		currExpr = (expr.substring(begex, nextLevelBegex) + 
    				String.valueOf(rEvaluate(expr, vars, arrays, nextLevelBegex + 1, nextLevelEndex - 1)) + 
    				expr.substring(nextLevelEndex + 1, endex + 1));
    		
    		float answer = crunchSimpleExpr(currExpr, vars);
			
			if (answer == -0.0){
				
				return (float)0.0;
			}
    		
    		return crunchSimpleExpr(currExpr, vars);
    	}
    	
    	else if (nextLevelCount == 1 && expr.charAt(nextLevelBegex) == '['){
    		
    		int inArray = (int)rEvaluate(expr, vars, arrays, nextLevelBegex + 1, nextLevelEndex - 1);
    		
    		int arrNameBegex = getLeftTermBegex(expr.substring(0, nextLevelBegex));
    		
			String arrName = expr.substring(arrNameBegex, nextLevelBegex);
			
			int arrDex;
			
			for (arrDex = 0; arrDex < arrays.size(); arrDex++){					
    					
				if ((arrays.get(arrDex).name).equals(arrName)){
    						
					break;
				}
			}
	    		
			float arrVal = (float)arrays.get(arrDex).values[inArray];																		
			
			currExpr = expr.substring(begex, arrNameBegex) + String.valueOf(arrVal) + 
					expr.substring(nextLevelEndex + 1, endex + 1);
			
			return crunchSimpleExpr(currExpr, vars);
    	}
    	
    	else if (nextLevelCount > 1 && (expr.charAt(nextLevelBegex) == '(' || 
    				expr.charAt(nextLevelBegex) == '[')){
    		
    		while (nextLevelCount != 0 && (expr.charAt(nextLevelBegex) == '(' ||
    				expr.charAt(nextLevelBegex) == '[')){
    			
    			int[] currLevelsData = getLevelsData(currExpr, 0, currExpr.length() - 1);
    	    	
    	    	int currNextLevelBegex = currLevelsData[0];		
    	    	
    	    	nextExpr = expr.substring(nextLevelBegex + 1, nextLevelEndex); 
    	    	
    	    	float arrVal = 0;
    	    	boolean arrSolved = false;
    	    	String arrName = null;
    	    	
    	    	if (expr.charAt(nextLevelBegex) == '(' &&
    	    			nextExpr.contains("*") == false && 
    	    			nextExpr.contains("/") == false && 
    	    			nextExpr.contains("+") == false &&
    	    			nextExpr.contains("-") == false &&
    	    			nextExpr.contains("[") == false){
        			
    	    		nextExpr = nextExpr.replaceAll("\\)", "");
    	    		nextExpr = nextExpr.replaceAll("\\(", "");
        			
        			if (expr.length() -1 >= nextLevelEndex + 2){
        			
        				solvedExpr = String.valueOf(crunchSimpleExpr(nextExpr, vars)) +
        						expr.substring(nextLevelEndex + 1, nextLevelEndex + 2);
        				
        				currExpr = currExpr.substring(0, currNextLevelBegex) + solvedExpr + 
            					String.valueOf(rEvaluate(expr, vars, arrays, nextLevelEndex + 2, expr.length() - 1)); 
        			}
        			
        			else{
        				
        				solvedExpr = String.valueOf(crunchSimpleExpr(nextExpr, vars));
        				
        				currExpr = currExpr.substring(0, currNextLevelBegex) + nextExpr;
        			}  	
        			
        			float answer = crunchSimpleExpr(currExpr, vars);
        			
        			if (answer == -0.0){
        				
        				return (float)0.0;
        			}
        			
        			return crunchSimpleExpr(currExpr, vars);
        			
        		}
    	    	    	    	
    	    	else if (expr.charAt(nextLevelBegex) == '['){
    	    	
    	    		int inArray = (int)rEvaluate(expr, vars, arrays, nextLevelBegex + 1, nextLevelEndex - 1);
        		
    	    		int arrNameBegex = getLeftTermBegex(expr.substring(0, nextLevelBegex));
        		
    	    		arrName = expr.substring(arrNameBegex, nextLevelBegex);
    			
    	    		int arrDex;
    			
    	    		for (arrDex = 0; arrDex < arrays.size(); arrDex++){					
        					
    	    			if ((arrays.get(arrDex).name).equals(arrName)){
        						
    	    				break;
    	    			}
    	    		}
    	    	    	    		
    	    		arrVal = (float)arrays.get(arrDex).values[inArray];		
    	    		
    	    		arrSolved = true;    			
    	    	}
    	    	
    	    	if (arrSolved == true){
    	    		
    	    		currExpr = currExpr.substring(0, currNextLevelBegex - arrName.length()) + String.valueOf(arrVal) + 
    	    				expr.substring(nextLevelEndex + 1, endex + 1);
    	    		
    	    		arrSolved = false;
    	    	}
    	    	
    	    	else{
    	    	
    	    		currExpr = currExpr.substring(0, currNextLevelBegex) + 
    	    				String.valueOf(rEvaluate(expr, vars, arrays, nextLevelBegex + 1, nextLevelEndex - 1)) +
    	    				expr.substring(nextLevelEndex + 1, endex + 1);
    	    	}
    			
    			nextLevelCount--;
    			
    			if (nextLevelCount == 0){
    				
    				break;
    			}
    			
    			levelsData = getLevelsData(expr, nextLevelEndex + 1, expr.length() - 1);
        	
    			nextLevelBegex = levelsData[0];		
    			nextLevelEndex = levelsData[1];		
    			nextLevelCount = levelsData[2];										
    		}
    		
    		float answer = crunchSimpleExpr(currExpr, vars);
			
			if (answer == -0.0){
				
				return (float)0.0;
			}
    		
    		return crunchSimpleExpr(currExpr, vars);
    	}
    		
    	return 0;		
    }
    
    private static int[] getLevelsData (String expr, int begex, int endex){
    	
    	int nextLevelBegex = -1;													// Next sub-expression's beginning index
    	int nextLevelEndex = -1;													// Next sub-expression's ending index
    	int nextLevelCount = 0;														// Number of sub-expressions one level lower the current
    	int deepestLevelBegex = -1;													// Deepest sub-expression's beginning index
    	int deepestLevelEndex = -1;													// Deepest sub-expression's ending index
    	int currLevel = 0;															// Depth-counter
    	int deepestLevel = 0;														// Deepest-depth tracker
    	
    	for (int i = begex; i <= endex; i++){
    		
    		if (expr.charAt(i) == '['){
    					
    			currLevel++;
    			
    			if (nextLevelBegex == -1){
    				
    				nextLevelBegex = i;
    			}
    			
    			if (currLevel > deepestLevel){
    				
    				deepestLevel++;
    				deepestLevelBegex = i;
    			}
    		}
    		
    		else if (expr.charAt(i) == '('){
    			
    			currLevel++;
    			
    			if (nextLevelBegex == -1){
    				
    				nextLevelBegex = i;
    			}
    			
    			if (currLevel > deepestLevel){
    				
    				deepestLevel++;
    				deepestLevelBegex = i;
    			}
    		}
    		
    		else if (expr.charAt(i) == ']'){
    			
    			if (currLevel == deepestLevel && deepestLevelEndex == -1){
    				
    				deepestLevelEndex = i;
    			}
    			
    			currLevel--;
    			
    			if (currLevel == 0){
    				
    				nextLevelCount++;
    				
    				if (nextLevelEndex == -1){
    				
    					nextLevelEndex = i;
    				}    				
    			}
    		}
    		
    		else if (expr.charAt(i) == ')'){
    			
    			if (currLevel == deepestLevel && deepestLevelEndex == -1){
    				
    				deepestLevelEndex = i;
    			}
    			
    			currLevel--;
    			
    			if (currLevel == 0){
    				
    				nextLevelCount++;
    				
    				if (nextLevelEndex == -1){
    					
    					nextLevelEndex = i;
    				}
    			}
    		}
    	}
    	
    	int[] levelsData = new int[5];
    	
    	levelsData[0] = nextLevelBegex;
    	levelsData[1] = nextLevelEndex;
    	levelsData[2] = nextLevelCount;
    	levelsData[3] = deepestLevelBegex;
    	levelsData[4] = deepestLevelEndex;
    	
    	return levelsData;
    }   
       
    private static float crunchSimpleExpr (String expr, ArrayList<Variable> vars){
    	
    	boolean multiply = false;
    	boolean divide = false;
		boolean add = false;
		
		int opDex = -1;
		
		if (expr.contains("*") == false && 
				expr.contains("/") == false && 
				expr.contains("+") == false && 
				expr.substring(1).contains("-") == false){							// subExpr is just a float or a variable
			
			if (expr.contains("0") || 
					expr.contains("1") || 
					expr.contains("2") || 
					expr.contains("3") || 
					expr.contains("4") || 
					expr.contains("5") ||											// subExpr is just a float
					expr.contains("6") || 
					expr.contains("7") || 
					expr.contains("8") || 
					expr.contains("9")){
				
				return Float.parseFloat(expr);										// return
			}
			
			else {																	// subExpr is just a variable		
    				
				int varDex;
	    				
				for (varDex = 0; varDex < vars.size(); varDex++){					// find variable index
	    					
					if ((vars.get(varDex).name).equals(expr)){
	    						
						break;
					}
				}
	    		
				return (float)vars.get(varDex).value;								// return variable value as float
			}
		}
		
		else if (expr.contains("*") || expr.contains("/")){							// subExpr is an expression of just integers, variables, and  		
																					// operators. Executes if one of these operators is a '*' or '/'
			for (int i = 0; i < expr.length(); i++){

				if (expr.charAt(i) == '*' || expr.charAt(i) == '/'){				// find first '*' or '/'

					if (expr.charAt(i) == '*'){										

						multiply = true;
						opDex = i;
						break;
					}

					else if (expr.charAt(i) == '/'){								

						divide = true;
						opDex = i;
						break;
					}
				}
			}
		}
		
		else {																		// subExpr is an expression of just integers, variables, and  	
																					// '+' or '-' operators.
			for (int i = 0; i < expr.length(); i++){
				
				if (i == 0 && expr.charAt(i) == '-'){
					
					i++;
				}

				else if (expr.charAt(i) == '+' || expr.charAt(i) == '-'){			// find first '+' or '-'

					if (expr.charAt(i) == '+'){

						add = true;
						opDex = i;
						break;
					}

					else if (expr.charAt(i) == '-'){

						opDex = i;
						break;
					}
				}
			}
		}
		
		String lHalf = expr.substring(0, opDex);									// split expression at operator
		String rHalf = expr.substring(opDex + 1);
				
		int lTermBegex = getLeftTermBegex(lHalf);							
		lHalf = lHalf.substring(lTermBegex);										// load left term into lHalf
		
		float lTerm = 0;
		
		while (lHalf.contains("*") || 
				lHalf.contains("/") || 
				lHalf.contains("+") || 												
				lHalf.substring(1).contains("-")){
			
			lHalf = String.valueOf(crunchSimpleExpr(lHalf, vars));					// recursively deal with multiple sub-operations
		}
		
		if (lHalf.contains("0") || 
				lHalf.contains("1") ||
				lHalf.contains("2") || 
				lHalf.contains("3") || 
				lHalf.contains("4") || 
				lHalf.contains("5") ||												// lHalf is just a float
				lHalf.contains("6") || 
				lHalf.contains("7") || 
				lHalf.contains("8") || 
				lHalf.contains("9")){
			
			lTerm = Float.parseFloat(lHalf);										// load float into lTerm
		}
		
		else{																		// lHalf is just a variable
			
			int varDex;
			
			for (varDex = 0; varDex < vars.size(); varDex++){						// find variable index
    					
				if ((vars.get(varDex).name).equals(lHalf)){
    						
					break;
				}
			}
    		
			lTerm = (float)vars.get(varDex).value;									// load lTerm with variable value as float										
		}
		
		int rTermEndex = getRightTermEndex(rHalf);							
		rHalf = rHalf.substring(0, rTermEndex + 1);									// load right term into rHalf
		
		float rTerm = 0;
		
		while (rHalf.contains("*") || 
				rHalf.contains("/") || 
				rHalf.contains("+") || 												
				rHalf.substring(1).contains("-")){
			
			rHalf = String.valueOf(crunchSimpleExpr(rHalf, vars));					// recursively deal with multiple sub-operations
		}
		
		if (rHalf.contains("0") || 
				rHalf.contains("1") || 
				rHalf.contains("2") || 
				rHalf.contains("3") || 
				rHalf.contains("4") || 
				rHalf.contains("5") ||												// rHalf is just a float
				rHalf.contains("6") || 
				rHalf.contains("7") || 
				rHalf.contains("8") || 
				rHalf.contains("9")){
			
			rTerm = Float.parseFloat(rHalf);										// load float into rTerm
		}
		
		else{																		// rHalf is just a variable
			
			int varDex;
			
			for (varDex = 0; varDex < vars.size(); varDex++){						// find variable index
    					
				if ((vars.get(varDex).name).equals(rHalf)){
    						
					break;
				}
			}
    		
			rTerm = (float)vars.get(varDex).value;									// load rTerm with variable value as float	
		}
								
		Float partAnsF = null;														// prepare partial-answer float
									
	    if (multiply == true){	  
    								
	    	partAnsF = lTerm * rTerm;												// multiply	    				
	   	}
	    			
	   	else if (divide == true){	  															
	    					    		
	   		partAnsF = lTerm / rTerm;												// divide
		}
	    							
	   	else if (add == true){	  
	    					    		
	    	partAnsF = lTerm + rTerm;												// add
	    }
	    			
	    else{	  															
	    					    		
	    	partAnsF = lTerm - rTerm;												// subtract	 
		}
		
		String partAnsS = String.valueOf(partAnsF);									// convert float back to string
		
		expr = expr.substring(0, lTermBegex) + partAnsS +							// update subExpr   
				expr.substring(opDex + rHalf.length() + 1, expr.length()); 
				
		return crunchSimpleExpr (expr, vars);										// recurse
    }
    
    private static int getLeftTermBegex (String lHalf){
    	
    	int lTermBegex = lHalf.length() - 1;
    	
    	if (Character.isDigit(lHalf.charAt(lHalf.length() - 1))){
			
			while (lTermBegex > 0){
    			
				if (Character.isDigit(lHalf.charAt(lTermBegex - 1)) || 
						lHalf.charAt(lTermBegex - 1) == '.'){
    			
					lTermBegex--;
				}
				
				else if (lTermBegex == 1 && 
						lHalf.charAt(lTermBegex - 1) == '-'){
					
					lTermBegex--;
				}
				
				else{
					
					break;
				}
    		}
		}
			
		else {
				
			while (lTermBegex > 0){
				
				if (Character.isDigit(lHalf.charAt(lTermBegex - 1)) == false && 
						lHalf.charAt(lTermBegex - 1) != '*' &&
						lHalf.charAt(lTermBegex - 1) != '/' && 
						lHalf.charAt(lTermBegex - 1) != '+' &&
	    				lHalf.charAt(lTermBegex - 1) != '-' &&
	    				lHalf.charAt(lTermBegex - 1) != '['){
    				
					lTermBegex--;
				}
				
				else{
					
					break;
				}
    		}
		}
    	
    	return lTermBegex;
    }
    
    private static int getRightTermEndex (String rHalf){
    	
    	int rTermEndex = 0;
    	
    	if (Character.isDigit(rHalf.charAt(0)) || rHalf.charAt(0) == '-'){
			
			while (rTermEndex < rHalf.length() - 1){
    				
				if (Character.isDigit(rHalf.charAt(rTermEndex + 1)) || 
						rHalf.charAt(rTermEndex + 1) == '.'){ 
				
					rTermEndex++;
				}
				
				else{
					
					break;
				}
    		}
		}
			
		else {
				
			while (rTermEndex < rHalf.length() - 1){
    			
				if (Character.isDigit(rHalf.charAt(rTermEndex + 1)) == false && 
						rHalf.charAt(rTermEndex + 1) != '*' &&
						rHalf.charAt(rTermEndex + 1) != '/' && 
						rHalf.charAt(rTermEndex + 1) != '+' &&
	    				rHalf.charAt(rTermEndex + 1) != '-'){
				
					rTermEndex++;
				}
				
				else{
					
					break;
				}
    		}
		}
    	
    	return rTermEndex;
    }
}
