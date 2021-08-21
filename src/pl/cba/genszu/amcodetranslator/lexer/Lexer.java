package pl.cba.genszu.amcodetranslator.lexer;
import pl.cba.genszu.amcodetranslator.lexer.tree.BinaryTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.cba.genszu.amcodetranslator.interpreter.util.Token;
import pl.cba.genszu.amcodetranslator.lexer.tree.Node;
import pl.cba.genszu.amcodetranslator.lexer.tree.BSTPrinter;
import pl.cba.genszu.amcodetranslator.interpreter.*;

/*TODO:
	refaktoryzacja
	sprawdzenie czy to wyrażenie czy instrukcje ([] czy {})
	
*/

public class Lexer
{
	/*public Lexer(String code) {
		
	}*/

	public static String[] selectiveSplit(String text, char splitChar) {
		return selectiveSplit(text, splitChar, '(', ')');
	}

	public static String[] selectiveSplit(String text, char splitChar, char intendCharMarker, char intendCharMarker2) {
		List<String> linesArr = new ArrayList<>();
		linesArr.add("");
		int stringNo = 0;
		int intentNo = 0;
		for(int i = 0; i < text.length(); i++) {
			if(text.charAt(i) == intendCharMarker)
				intentNo++;
			if (text.charAt(i) == intendCharMarker2) {
				intentNo--;
			}
			if(text.charAt(i) == splitChar && intentNo == 0) {
				stringNo++;
				linesArr.add("");
			}
			else {
				linesArr.set(stringNo, linesArr.get(stringNo)+text.charAt(i));
			}
		}
		return linesArr.toArray(new String[0]);
	}
	
	public static String[] singleSplit(String text, String splitChar) {
		String[] parts = new String[2];
		for(int i = 0; i < text.length(); i++) {
			if(text.charAt(i) == splitChar.charAt(0)) {
				if(i != 0)
					parts[0] = text.substring(0, i);
					
				parts[1] = text.substring(i+1);
				return parts;
			}
		}
		return null;
	}

	private static String[] parseConditional(String condition) {
		final String regex = "(.*?)([<>!_']{1,2})(.*)";

		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(condition);

		if(matcher.find()) {
			return new String[] {matcher.group(1), matcher.group(2), matcher.group(3)};
		}
		else {
			return null;
		}
	}
	
	private static boolean isMethodCall(String code) {
		return code.contains("^");
	}
	
	private static boolean isExpression(String code) {
		return (code.startsWith("[") && code.endsWith("]"));
	}
	
	private static boolean isCode(String code) {
		return (code.startsWith("{") && code.endsWith("}"));
	}
	
	private static String extractExpression(String code) {
		if(isExpression(code) || isCode(code))
			return cutHeadAndTail(code);
		return code;
	}
	
	private static boolean isArithmetic(String code) {
		if(
			code.contains("+") ||
			code.contains("-") ||
			code.contains("*") ||
			(code.contains("@") && !code.startsWith("@")) ||
			code.contains("%")
		) return true;
		else return false;
	}
	
	private static boolean isVarDef(String code) {
		return 
			   code.startsWith("@BOOL") 
			|| code.startsWith("@INT")
			|| code.startsWith("@DOUBLE")
			|| code.startsWith("@STRING");
	}

	private static String cutHeadAndTail(String string) {
		return string.substring(1, string.length()-1);
	}
	
	public static InstructionsList parseCode(String code) throws Exception {
		return parseCode(code, true);
	}

	public static InstructionsList parseCode(String code, boolean mainRoutine) throws Exception {

		code = extractExpression(code);
		if(!isMethodCall(code) && !isArithmetic(code)) System.out.println("Prawdopodobnie nazwa Behaviour -> "+code);
		//code = code.substring(1, code.length()-1);
		//System.out.println((mainRoutine?"INSTRUCTIONS: ":"SUBROUTINE: ")+code);
		
		BinaryTree tree = new BinaryTree();
		InstructionsList instructionsList = new InstructionsList();
		
		//String[] instructions = code.split(";"); //debug

		String[] instructions = selectiveSplit(code, ';');
		
		for(String instr : instructions) {
			//System.out.println(instr);
			instr = instr.trim(); //remove spaces
			if(!instr.equals("")) {
				if (instr.startsWith("@IF")) {
					//String[] parts = instr.substring(4, instr.length() - 1).split(",");
					String[] parts = selectiveSplit(instr.substring(4, instr.length() - 1), ',');

					String[] cond = parseConditional(cutHeadAndTail(parts[0]));

					if (cond != null) {
						Token obj = new Token(Constants.IF);
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

						Token var = new Token(Constants.VARNAME, cond[0]);
						Token val = new Token(Constants.VARVAL, cond[2]);

						tree.root = new Node(obj);
						Node tmp = tree.root.add(condition);
						Node tmp2 = tmp.add(var); //dla true
						tmp = tmp.add(val); //dla false

						String ifTrue = cutHeadAndTail(parts[1]);
						String ifFalse = cutHeadAndTail(parts[2]);

						if (!ifTrue.equals("")) {
							//if(ifTrue.startsWith("{")) ifTrue = cutHeadAndTail(ifTrue);
							tmp2.add(parseCode(ifTrue, false));
						}
						if (!ifFalse.equals("")) {
							//if(ifFalse.startsWith("{")) ifFalse = cutHeadAndTail(ifFalse);
							tmp.add(parseCode(ifFalse, false));
						}

						//BSTPrinter.print(tree);
					} else {
						System.out.println("Błąd parsowania. Linia: " + instr);
					}
				} 
				else if(isVarDef(instr)) {
					final String regex = "@(BOOL|INT|DOUBLE|STRING)\\(\"(.*)\",(.*)\\)";

					final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
					final Matcher matcher = pattern.matcher(instr);

					if (matcher.find()) {
						Token create = new Token(Constants.VARCREATE);
						Token type = new Token(Constants.VARTYPE, matcher.group(1));
						Token name = new Token(Constants.VARNAME, matcher.group(2));
						Token value;
						if(isMethodCall(matcher.group(3))) {
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
						Token name = new Token(Constants.VARNAME, matcher.group(1));

						tree.root = new Node(ret);
						tree.root.add(name);

						//BSTPrinter.print(tree);
					}
				}
				else if(instr.startsWith("@")) {
					System.out.println("WARNING: " + instr.split("\\(")[0] + " is not implemented yet!!!");
				}
				else if (isMethodCall(instr)) { //wywołania metod
					String[] elems = selectiveSplit(instr, '^');
					String objectName = elems[0].trim();
					if(objectName.startsWith("*")) {
						objectName = objectName.substring(1);
						System.out.println("INFO: Pointer to variable holding object name detected. Not implemented yet. Omitting...");
					}
					if(isExpression(objectName) && objectName.contains("$")) {
						objectName = objectName.substring(1, objectName.length()-1);
						System.out.println("WARNING: Dollar object reference detected. Stays untouched for now.");
					}
					elems[1] = elems[1].substring(0, elems[1].length()-1);
					String[] funcParts = singleSplit(elems[1], "(");
					String functionName = funcParts[0].trim();
					String[] params = funcParts[1].trim().split(",");
					//System.out.println("obj: " + objectName + ", method: " + functionName + ", paramsNumb: " + params.length + ", params: " + Arrays.toString(params));

					Token obj = new Token(Constants.VARNAME, objectName);
					Token fire = new Token(Constants.FIRE);
					Token func = new Token(Constants.FUNC, functionName);
					Token param = null;
					if(!params[0].equals("")) {
						if(isMethodCall(funcParts[1])) {
							param = new Token(Constants.FUNC_PARAMS, Lexer.parseCode("{"+funcParts[1]+"}"));
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
				else if(isArithmetic(instr)) {
					final String regex = "(.*?)([+\\-*@%])(.*)";

					final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
					final Matcher matcher = pattern.matcher(instr);

					if (matcher.find()) {
						Token op;
						switch(matcher.group(2)) {
							case "+":
								op = new Token(Constants.PLUS);
								break;
							case "-":
								op = new Token(Constants.MINUS);
								break;
							case "*":
								op = new Token(Constants.MUL);
								break;
							case "@":
								op = new Token(Constants.DIV);
								break;
							case "%":
								op = new Token(Constants.MOD);
								break;
							default:
								op = new Token(Constants.DEBUG); //just for supress uninitialised error
						}
						//determine if elements are numbers, variables, method calls or strings
						//if string replace Constants.PLUS by Constants.CONCAT
						Token left = new Token(Constants.VARNAME, matcher.group(1));
						Token right = new Token(Constants.VARNAME, matcher.group(3));
						
						tree.root = new Node(op);
						tree.root.add(left);
						tree.root.add(right);

						//BSTPrinter.print(tree);
					}
				}
				//To akurat komentarz jednoliniowy wewnątrz kodu, do wywalenia
				/*else if(instr.startsWith("!")) {
					String functionName = instr.replace(" !", "");
					System.out.println("engine: fire " + functionName);

					Token engine = new Token(Constants.ENGINE);
					Token fire = new Token(Constants.FIRE);
					Token func = new Token(Constants.FUNC, functionName);

					tree.root = new Node(engine);
					Node tmp = tree.root.add(fire);
					tmp.add(func);

					//BSTPrinter.print(tree);
				}*/
				else {
					//System.out.println(instr);
					//TODO: przejrzenie zmiennych i sprawdzenie czy zmienna to Behaviour, jeśli nie wyrzuć LexerException
					System.out.println("Zakładam, że Behaviour");

					Token obj = new Token(Constants.VARNAME, instr);
					Token fire = new Token(Constants.FIRE);
					Token func = new Token(Constants.FUNC, "CODE");

					tree.root = new Node(obj);
					Node tmp = tree.root.add(fire);
					tmp.add(func);

					//BSTPrinter.print(tree);
				}
				instructionsList.addInstruction(tree);
			}
		}
		
		return instructionsList;
	}
}
