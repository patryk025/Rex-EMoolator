package pl.cba.genszu.amcodetranslator.lexer;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import pl.cba.genszu.amcodetranslator.algebra.*;
import pl.cba.genszu.amcodetranslator.encoding.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.interpreter.util.*;
import pl.cba.genszu.amcodetranslator.lexer.fixer.*;
import pl.cba.genszu.amcodetranslator.lexer.tree.*;
import pl.cba.genszu.amcodetranslator.lexer.tree.exception.*;
import pl.cba.genszu.amcodetranslator.utils.*;

public class Parser
{
	private static String[] parseConditional(String condition) {
		final String regex = "(.+?)([<>][']{0,1}|[!]{0,1}['])(.+)";

		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher;

		List<String> parts = new ArrayList<>();
		
		String[] letters = condition.split("");

		String buffer = "";
		
		int counter = 0;
		int len = letters.length;

		for(String letter : letters) {
			counter++;
			if(letter.matches("[\\||&]")) {
				if(!buffer.equals("")) {
					if(!buffer.matches("[\\||&]")) {
						matcher = pattern.matcher(buffer);
						if(matcher.find()) {
							parts.add(matcher.group(1));
							parts.add(matcher.group(2));
							parts.add(matcher.group(3));
							buffer = letter;
						}
					}
					else {
						buffer += letter;
					}
				}
				else {
					System.out.println("WARNING: unexpected logic operator, no condition before");
				}
			}
			else {
				if(buffer.equals("||")) {
					parts.add("OR");
					buffer = "";
				}
				else if(buffer.equals("&&")) {
					parts.add("AND");
					buffer = "";
				}
				buffer += letter;
				if(counter == len) {
					matcher = pattern.matcher(buffer);
					if(matcher.find()) {
						parts.add(matcher.group(1));
						parts.add(matcher.group(2));
						parts.add(matcher.group(3));
						buffer = letter;
					}
				}
			}
		}
		
		/*for(int i = 0; i < parts.size(); i++) {
			parts.set(i, parts.get(i).replace("~ITERATOR~", "_I_"));
		}*/

		return parts.toArray(new String[0]);
	}
	
	private static Node condArrayToTree(String[] cond) throws BinaryTreeInsertException, Exception {
		if(cond.length == 3) { //warunek prosty
			//Token obj = new Token(Constants.IF);
			Token condition;
			switch (cond[1]) {
				case "<": condition = new Token(Constants.LSS); break;
				case "<'":
				case "<_": condition = new Token(Constants.LEQ); break;
				case ">'":
				case ">_": condition = new Token(Constants.GEQ); break;
				case ">": condition = new Token(Constants.GTR); break;
				case "'":
				case "_": condition = new Token(Constants.EQU); break;
				case "!'":
				case "!_": condition = new Token(Constants.NEQ); break;
				default: condition = new Token(Constants.DEBUG, cond[1]);
			}

			if(cond[2].startsWith("$")) {
				Logger.w("Dollar parameter reference detected. Stays untouched for now.");
			}

			Token var;
			if(LexerUtils.isMethodCall(cond[0]) || LexerUtils.isArithmetic(cond[0]))
				var = new Token(Constants.VARVALFUNC, Parser.parseCode(cond[0]));
			else
				var = new Token(Constants.VARNAME, cond[0]);
			//Token val = new Token(Constants.VARVAL, cond[2]);
			Token val;
			
			//if(ExpressionParser.parseExpression(cond[2]).size() > 1)
			if(LexerUtils.isMethodCall(cond[2]) || LexerUtils.isArithmetic(cond[2]))
				val = new Token(Constants.VARVALFUNC, ExpressionParser.expressionToTree(cond[2]));
			else
				val = new Token(Constants.VARVAL, cond[2]);
			
			Node tmp = new Node(condition);
			tmp.add(var);
			tmp.add(val);
			
			return tmp;
		}
		else {
			//konwersja z postaci infixowej do postfixowej
			HashMap<String, Integer> prec = new HashMap<>();
			prec.put("AND", 3);
			prec.put("OR", 2);
			prec.put("(", 1);
			Stack<String> operations = new Stack<>();
			List<String> postfix = new ArrayList<>();
			for(int i = 0; i< cond.length; i++) {
				if (!(cond[i].equals("OR") || cond[i].equals("AND"))) {
					postfix.add(cond[i]);
					postfix.add(cond[i+1]);
					postfix.add(cond[i+2]);
					i+=2;
				} else if (cond[i].equals("(")) {
					operations.push(cond[i]);
				} else if (cond[i].equals(")")) {
					String tmpOp = operations.pop();
					while (!tmpOp.equals('(')) {
						postfix.add(tmpOp);
						tmpOp = operations.pop();
					}
				} else {
					try {
						String tmpElem = operations.peek();
						while (!operations.isEmpty() && (prec.get(tmpElem) >= prec.get(cond[i]))) {
							postfix.add(operations.pop());
							tmpElem = operations.peek();
						}
					}
					catch(EmptyStackException e) {}
					operations.push(cond[i]);
				}
			}
			while (!operations.isEmpty()) {
				postfix.add(operations.pop());
			}
			
			//konwersja postaci postfixowej do drzewa binarnego
			Stack<Node> operands = new Stack<>();
			for(int i = 0; i < postfix.size(); i++) {
				//if not operator
				if (!(postfix.get(i).equals("OR") || postfix.get(i).equals("AND"))) {
					operands.push(condArrayToTree(postfix.subList(i, i+3).toArray(new String[0])));
					i += 2;
				}
				else {
					Node tmp;
					if(postfix.get(i).equals("OR")) {
						tmp = new Node(new Token(Constants.OR));
					}	
					else if(postfix.get(i).equals("AND")) {
						tmp = new Node(new Token(Constants.AND));
					}
					else {
						tmp = new Node(new Token(Constants.DEBUG));
					}
					try
					{
						tmp.right = (LexerUtils.isMethodCall(operands.peek().value.value) ?parseCode(operands.pop().value.value).instr.get(0).root: operands.pop());
						tmp.left = (LexerUtils.isMethodCall(operands.peek().value.value) ?parseCode(operands.pop().value.value).instr.get(0).root: operands.pop());
						operands.push(tmp);
					}
					catch (Exception e)
					{
						//todo
					}
				}
			}
			
			Node tree = null;
			if(!operands.isEmpty())
				tree = operands.pop();

			//BSTPrinter.print(new BinaryTree(tree));
			return tree;
		}
		
		//return null;
	}
	
	/*private String checkVariableType(String varname) {
		
	}*/
	
	public static InstructionsList parseCode(String code) throws Exception {
		return parseCode(code, true);
	}

	public static InstructionsList parseCode(String code, boolean mainRoutine) throws Exception {

		code = LexerUtils.extractExpression(code);
		
		//code = code.replace(", ", ","); //taka tam kulawa proteza na chwilę (do wywalenia, spowalnia bardzo)
		//TODO: pozbyć się tych protez i zoptymalizować to
		
		//if(!isMethodCall(code) && !isArithmetic(code) && !code.startsWith("@")) System.out.println("Prawdopodobnie nazwa Behaviour -> "+code);
		//code = code.substring(1, code.length()-1);
		//System.out.println((mainRoutine?"INSTRUCTIONS: ":"SUBROUTINE: ")+code);
		
		BinaryTree tree = null;
		InstructionsList instructionsList = new InstructionsList();
		
		//String[] instructions = code.split(";"); //debug

		//String[] instructions = StringUtils.selectiveSplit(code, ';');
		
		String[] instructions = StringUtils.splitInstrToLines(code);
		
		/*System.out.println("Test, zwykły split");
		int testI = 0;
		String[] debug1 = code.split(";");
		String[] debug2 = StringUtils.selectiveSplit(code, ';');
		for(String testInstr : debug1) {
			System.out.println(testI++ + ". " + testInstr);
		}
		System.out.println("Test, selective split");
		testI = 0;
		for(String testInstr : debug2) {
			System.out.println(testI++ + ". " + testInstr);
		}
		System.out.println("Compare lengths");
		System.out.println("debug1.length (" + debug1.length + ") == debug2.length (" + debug2.length + ") => " + (debug1.length == debug2.length));
		System.out.println();
		if(true)
			return null;*/
		
		//System.out.println(Arrays.asList(instructions));
		
		/*pw.println("--------------DEBUG!!!--------------");
		for(String instr : instructions) {
			pw.println(instr);
		}
		pw.flush();*/
		
		CodePatches patcher = new CodePatches();
		
		for(String instr : instructions) {
			tree = new BinaryTree();
			//System.out.println(instr);
			instr = instr.trim(); //remove spaces
			if(!instr.equals("")) {
				//first check if instr doesn't have fix
				if(patcher.hasPatch(instr)) {
					Logger.i("Found fix for " + instr);
					instr = patcher.patch(instr);
				}
				
				if (instr.startsWith("@IF")) {
					instr = instr.replace(", ", ",");
					
					//String[] parts = instr.substring(4, instr.length() - 1).split(",");
					String[] parts = StringUtils.selectiveSplit(instr.substring(4, instr.length() - 1), ',');
					//System.out.println("DEBUG: "+instr);
					
					//System.out.println("DEBUG: " + parts[0]);
					/*for(int i = 0; i < parts.length; i++) {
						System.out.println(i + ". " + parts[i]);
					}*/
					
					//transformacja 5-cioargumentowego ifa do trójargumentowego
					//zmiana _ na ' (kłopoty z parsowaniem)
					if(parts.length == 5) { //warunek prosty (zmienna, komparator, wartość, prawda, fałsz)
						parts[0] = "\""+LexerUtils.extractExpression(parts[0])+LexerUtils.extractExpression(parts[1]).replace("_","'")+LexerUtils.extractExpression(parts[2])+"\"";
						parts[1] = parts[3];
						parts[2] = parts[4];
						parts[3] = null;
						parts[4] = null;
					}

					String[] cond = parseConditional(StringUtils.cutHeadAndTail(parts[0]));

					/*System.out.println("cond:");
					for(int i = 0; i < cond.length; i++) {
						System.out.println(i + ". " + cond[i]);
					}*/
					
					if (cond != null) {
						Token obj = new Token(Constants.BRANCH);
						
						Branch conditionTree = new Branch(obj);
						conditionTree.addCondition(condArrayToTree(cond));
						
						
						String ifTrue = StringUtils.cutHeadAndTail(parts[1]);
						String ifFalse = StringUtils.cutHeadAndTail(parts[2]);

						if (!ifTrue.equals("")) {
							//if(ifTrue.startsWith("{")) ifTrue = cutHeadAndTail(ifTrue);
							conditionTree.left = new Node(Constants.IF);
							conditionTree.left.add(parseCode(ifTrue, false).instr.get(0).root);
						}
						if (!ifFalse.equals("")) {
							//if(ifFalse.startsWith("{")) ifFalse = cutHeadAndTail(ifFalse);
							conditionTree.right = new Node(Constants.ELSE);
							conditionTree.right.add(parseCode(ifFalse, false).instr.get(0).root);
						}
						
						tree.root = conditionTree;

						//BSTPrinter.print(tree);
					} else {
						Logger.e("Błąd parsowania. Linia: " + instr);
					}
				} 
				else if (instr.startsWith("@WHILE")) {
					//String[] parts = instr.substring(4, instr.length() - 1).split(",");
					String[] parts = StringUtils.selectiveSplit(instr.substring(7, instr.length() - 1), ',');
					//System.out.println("DEBUG: "+instr);

					//System.out.println("DEBUG: " + parts[0]);
					/*for(int i = 0; i < parts.length; i++) {
					 System.out.println(i + ". " + parts[i]);
					 }*/

					String[] cond = parseConditional(LexerUtils.extractExpression(parts[0])+LexerUtils.extractExpression(parts[1]).replace("_","'")+LexerUtils.extractExpression(parts[2]));

					/*System.out.println("cond:");
					 for(int i = 0; i < cond.length; i++) {
					 System.out.println(i + ". " + cond[i]);
					 }*/

					if (cond != null) {
						Branch conditionTree = new Branch(Constants.WHILE);
						conditionTree.addCondition(condArrayToTree(cond));

						conditionTree.add(parseCode(parts[3]));
						

						tree.root = conditionTree;

						//BSTPrinter.print(tree);
					} else {
						Logger.e("Błąd parsowania. Linia: " + instr);
					}
				}
				else if (instr.startsWith("@LOOP")) {

					/*
					 @LOOP(treść, wartość_początkowa, różnica_początkowa_maksymalna, wartość_inkrementacji);														
					 gdzie														
					 treść może zawierać nazwę obiektu typu BEHAVIOUR, kod do wykonania lub być pusta							(?)						
					 przy czym w kodzie można używać zadeklarowanego licznika pętli w postaci zmiennej _I_												
					 wartość_początkowa określa początkową wartość licznika													
					 różnica_początkowa_maksymalna określa różnicę między startową a końcową wartość licznika (końcowa wartość nie jest osiągana: oznacza wyjście z pętli)													
					 wartość_inkrementacji określa wartość, o jaką licznik jest podnoszony za każdą iteracją													
					 tak więc wszystko skończyło się dobrze wartość licznika pętli należy do przedziału prawostronnie otwartego <wartość_początkowa; wartość_początkowa+różnica_początkowa_maksymalna)														
					 */

					//System.out.println("DEBUG: "+instr);
					String[] parts = StringUtils.selectiveSplit(instr.substring(6, instr.length() - 1), ',');

					//System.out.println("DEBUG: " + parts[0]);
					/*for(int i = 0; i < parts.length; i++) {
					 System.out.println(i + ". " + parts[i]);
					 }*/

					InstructionsList loopCode = parseCode(parts[0]);
					String startValue = parts[1];
					String stopValue = (startValue.endsWith("]")?startValue.substring(0,startValue.length() - 1):startValue) + "+" + LexerUtils.extractExpression(parts[2]);
					String incrStep = parts[3];

					if(startValue.equals("0")) stopValue = stopValue.replace("0+", ""); //zero plus coś nam tu nie potrzebne

					//System.out.println("_I_>'"+startValue+"&&_I_<"+stopValue);
					String[] cond = parseConditional("_I_>'"+startValue+"&&_I_<"+stopValue);

					//System.out.println(Arrays.toString(cond));
					if (cond != null) {
						Token obj = new Token(Constants.LOOP);

						Branch conditionTree = new Branch(obj);
						conditionTree.addCondition(condArrayToTree(cond));

						conditionTree.add(loopCode);

						Node counterChange = new Node(new Token(Constants.VARSET));
						counterChange.add(new Token(Constants.VARNAME, "_I_"));
						Node tmp = counterChange.add(new Token(Constants.VARVAL));
						tmp.add(ExpressionParser.expressionToTree("_I_+"+incrStep));

						conditionTree.addIncrement(counterChange);

						//BSTPrinter.print(new BinaryTree(conditionTree));
					} else {
						Logger.e("Błąd parsowania. Linia: " + instr);
					}
				}
				else if(LexerUtils.isVarDef(instr)) {
					final String regex = "@(BOOL|INT|DOUBLE|STRING)\\(\"(.*)\",(.*)\\)";

					final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
					final Matcher matcher = pattern.matcher(instr);

					if (matcher.find()) {
						Token create = new Token(Constants.VARCREATE);
						Token type = new Token(Constants.VARTYPE, matcher.group(1));
						Token name = new Token(Constants.VARNAME, matcher.group(2));
						Token value;
						if(LexerUtils.isMethodCall(matcher.group(3))) {
							value = new Token(Constants.VARVALFUNC, parseCode(matcher.group(3)));
							//System.out.println("VARVALFUNC");
							//BSTPrinter.print(value.valueAsFunc);
						}
						else
							value = new Token(Constants.VARVAL, matcher.group(3));

						tree.root = new Node(create);
						Node tmp = tree.root.add(type);
						tmp.add(name);
						tmp.add(value);

						//BSTPrinter.print(tree);
					}
				}
				else if(instr.startsWith("@RETURN")) {
					final String regex = "@RETURN\\((.*)\\)";

					final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
					final Matcher matcher = pattern.matcher(instr);

					if (matcher.find()) {
						Token ret = new Token(Constants.RETURN);
						Token name = new Token(Constants.VARVAL, matcher.group(1));

						tree.root = new Node(ret);
						tree.root.add(name);

						//BSTPrinter.print(tree);
					}
				}
				else if(instr.startsWith("@BREAK")) {
					Token brk = new Token(Constants.BREAK);
					
					tree.root = new Node(brk);

					//BSTPrinter.print(tree);
				}
				else if(instr.startsWith("@MSGBOX")) {
					final String regex = "@MSGBOX\\((.*)\\)";

					final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
					final Matcher matcher = pattern.matcher(instr);

					if (matcher.find()) {
						Token ret = new Token(Constants.MSGBOX);
						Token name = new Token(Constants.VARVAL, matcher.group(1));

						tree.root = new Node(ret);
						tree.root.add(name);

						//BSTPrinter.print(tree);
					}
				}
				else if(instr.startsWith("@")) {
					Logger.w(instr.split("\\(")[0]+" is not implemented yet!!!");
				}
				else if(LexerUtils.isPointerByString(instr)) {
					//System.out.println("INFO: Pointer to variable holding object name detected.");
					
					final String regex = "\\*(\\[.*?\\]).*";

					final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
					final Matcher matcher = pattern.matcher(instr);

					if (matcher.find()) {
						Token finder = new Token(Constants.VARFIND);
						Token name = new Token(Constants.VARNAME, parseCode(matcher.group(1)));
						Node action;
						
						String placeholder = instr.substring(1).replace(matcher.group(1), "a");
						
						InstructionsList test = parseCode(placeholder);
						
						action = test.instr.get(0).root.left;
						
						tree.root = new Node(finder);
						tree.root.add(name);
						tree.root.add(action);
					}
				}
				else if(LexerUtils.isArithmetic(instr)) {

					String expr = LexerUtils.extractExpression(instr);
					//List<String> parts = parseExpression(expr);

					//System.out.println(parts.toString());

					tree.root = ExpressionParser.expressionToTree(expr);
				}
				else if (LexerUtils.isMethodCall(instr)/* && !isArithmetic(instr)*/) { //wywołania metod
					//System.out.println(instr);
					String[] elems = StringUtils.selectiveSplit(instr, '^');
					String objectName = elems[0].trim();
					boolean isStruct = false;
					if(objectName.startsWith("*")) { //przekierowane wyżej
						//objectName = objectName.substring(1);
						//connect with prev else
						//System.out.println("INFO: Pointer to variable holding object name detected. Not implemented yet. Omitting...");
					}
					if(LexerUtils.isExpression(objectName) && objectName.contains("$")) {
						objectName = objectName.substring(1, objectName.length()-1);
						Logger.w("Dollar parameter reference detected. Stays untouched for now.");
					}
					if(LexerUtils.isStructFieldExpr(objectName)){
						isStruct = true;
						//System.out.println("INFO: Function call on struct field");
					}
					elems[1] = elems[1].substring(0, elems[1].length()-1);
					String[] funcParts = StringUtils.singleSplit(elems[1], "(");
					String functionName = funcParts[0].trim();
					funcParts[1] = funcParts[1].replace(", ", ",");
					String[] params = funcParts[1].trim().split(",");
					//System.out.println("obj: " + objectName + ", method: " + functionName + ", paramsNumb: " + params.length + ", params: " + Arrays.toString(params));

					Token obj;
					if(isStruct) {
						obj = new Token(Constants.VARVALFUNC, Parser.parseCode(objectName));
					}
					else obj = new Token(Constants.VARNAME, objectName);
					Token fire = new Token(Constants.FIRE);
					Token func = new Token(Constants.FUNC, functionName);
					Token param = null;
					if(!params[0].equals("")) {
						if(LexerUtils.isMethodCall(funcParts[1])) {
							param = new Token(Constants.FUNC_PARAMS, Parser.parseCode("{"+funcParts[1]+"}"));
						}
						else if(LexerUtils.isExpression(funcParts[1]) || LexerUtils.isStructFieldExpr(funcParts[1])) {
							param = new Token(Constants.FUNC_PARAMS, Parser.parseCode(funcParts[1]));
						}
						else
							param = new Token(Constants.FUNC_PARAMS, funcParts[1]);
					}
					tree.root = new Node(obj);
					Node tmp = tree.root.add(fire);
					tmp.add(func);
					if (!params[0].equals(""))
						tmp.add(param);

					//BSTPrinter.print(tree);
				}
				else if(LexerUtils.isStructFieldExpr(instr)) {
					String[] parts = instr.split("\\|");
					Token obj = new Token(Constants.VARNAME, parts[0]);
					Token fire = new Token(Constants.FIRE);
					Token func = new Token(Constants.FUNC, "GET");
					Token param = new Token(Constants.FUNC_PARAMS, parts[1]);
					tree.root = new Node(obj);
					Node tmp = tree.root.add(fire);
					tmp.add(func);
					tmp.add(param);
				}
				else {
					//System.out.println(instr);
					//TODO: przejrzenie zmiennych i sprawdzenie czy zmienna to Behaviour, jeśli nie wyrzuć LexerException
					//System.out.println("Zakładam, że Behaviour");

					Token obj = new Token(Constants.VARNAME, instr);
					Token fire = new Token(Constants.FIRE);
					Token func = new Token(Constants.FUNC, "CODE");

					tree.root = new Node(obj);
					Node tmp = tree.root.add(fire);
					tmp.add(func);

					//BSTPrinter.print(tree);
				}
				//BSTPrinter.print(tree);
				instructionsList.addInstruction(tree);
			}
		}
		
		return instructionsList;
	}
}
