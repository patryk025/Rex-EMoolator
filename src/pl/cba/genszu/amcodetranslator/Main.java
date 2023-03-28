package pl.cba.genszu.amcodetranslator;

import java.io.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import pl.cba.genszu.amcodetranslator.antlr.*;
import pl.cba.genszu.amcodetranslator.listener.*;
import pl.cba.genszu.amcodetranslator.objects.parser.*;
import pl.cba.genszu.amcodetranslator.utils.*;
import pl.cba.genszu.amcodetranslator.visitors.*;

public class Main {
	
	static List<SyntaxError> syntaxErrors;
	static int fixAttempts = 0;
	
	private static Map<String, String> manualFixes;
	
	private static boolean testCode(String code) {
		fixAttempts++;
		CodeBeautifier.beautify(code);
		//System.out.println(debugInstr);

		AidemMediaLexer lexer = new AidemMediaLexer(CharStreams.fromString(code));

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		AidemMediaParser parser = new AidemMediaParser(tokens);
		SyntaxErrorListener listener = new SyntaxErrorListener();
		lexer.addErrorListener(listener);
		parser.addErrorListener(listener);
		//ParseTree tree = parser.script();
		
		lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
		parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
		parser.script();

		syntaxErrors = listener.getSyntaxErrors();

		if(syntaxErrors.size() > 0) {
			StringBuilder codeFixed = new StringBuilder(code);
			
			System.out.println("Ups, errors: "+syntaxErrors.size());
			//for(SyntaxError error : syntaxErrors) {
			SyntaxError error = syntaxErrors.get(0);
			
				if(error.getException() != null && error.getMessage() == null) {
					String errorName = error.getException().toString();
					System.out.println("Exception: "+errorName);
					String[] parts = errorName.split("\\(");
					try {
						parts[1] = parts[1].substring(0, parts[1].length()-2).replace("'", "");
					}
					catch (Exception e) {
						System.out.println("Wut?");
						return false;
					}
					//System.out.println(error.getMessage());
					switch(parts[0]) {
						case "LexerNoViableAltException":
							switch(parts[1]) {
								case ":":
									System.out.println("Detected typo at "+error.getLine()+":"+error.getCharPositionInLine()+". ':' should be ';'");
									codeFixed.setCharAt(error.getCharPositionInLine(), ';');
									System.out.println("Typo fixed");
									break;
								default:
									System.out.println("Sorry, I don't know how to fix '"+parts[1]+"'");
							}
							break;
						default:
							System.out.println("TODO: unsupported exception for now");
					}
				}
				else {
					System.out.println("Error: "+error.getMessage());
					if(error.getMessage().startsWith("extraneous input")) {
						String[] parts = error.getMessage().split("'", 5);
						String wrongInput = parts[1];
						switch(wrongInput) {
							case ")":
								System.out.println("Misplaced right parenthesis, looking for errors...");
								codeFixed.deleteCharAt(error.getCharPositionInLine());
								boolean fixed = false;
								int parenCounter = 0;
								//System.out.println(codeFixed.length());
								for(int i = error.getCharPositionInLine(); i < codeFixed.length() && !fixed; i++) {
									//System.out.println(i);
									char character = codeFixed.charAt(i);
									if(character == '(') {
										parenCounter++;
									}
									else if(character == ')') {
										if(parenCounter == 0) {
											System.out.println("Too much right parens, removing it (should check it later)...");
											codeFixed.deleteCharAt(i);
											fixed = true;
										}
										else {
											parenCounter--;
										}
									}
									else if((character == ';' || character == '}') && parenCounter > 0) {
										for(int j = 0; j < parenCounter; j++) {
											codeFixed.insert(i, ')');
											fixed = true;
										}
									}
								}
								if(fixed) {
									System.out.println("Code corrected");
								}
								else {
									System.out.println("I couldn't find out how to fix it");
								}
								break;
							case ";":
								//probably problems with quotation marks
								System.out.println("Fixer in progress...");
								
								int lineToFixStart = -1;
								
								//extract line
								for(int i = error.getCharPositionInLine()-1; i >= 0; i--) {
									//System.out.println(i);
									char character = codeFixed.charAt(i);
									if(character == ';' || character == '{') {
										lineToFixStart = i+1;
										break;
									}
								}
								String firstExpected = parts[3].trim();
								//System.out.println("Broken line: "+code.substring(lineToFixStart, error.getCharPositionInLine()));
								int quoteNo = 0;
								parenCounter = 0;
								fixed = false;
								for(int i = lineToFixStart; i <= error.getCharPositionInLine(); i++) {
									//System.out.println(i);
									char character = codeFixed.charAt(i);
									if(firstExpected.equals("(")) {
										if (character == '"')
										{
											if (codeFixed.charAt(i - 1) == '"')
											{
												char nextCharacter = codeFixed.charAt(i + 1);
												if (
													(nextCharacter >= 'a' && nextCharacter <= 'z')
													||  (nextCharacter >= 'A' && nextCharacter <= 'Z')
													||  (nextCharacter >= '0' && nextCharacter <= '9')
													)
												{
													System.out.println("Wrong quotation mark, removing it...");
													codeFixed.deleteCharAt(i);
													System.out.println("Removed character, wait for next checking loop...");
												}
											} //TODO: if ex. (""VARIABLE) or (""VARIABLE")
											else
											{
												if (quoteNo == 1) quoteNo = 0;
												else quoteNo = 1;
											}
										}
										else if (character == '\'')
										{
											System.out.println("Wrong quotation mark, replacing ' with \"");
											codeFixed.setCharAt(i, '"');
											if (quoteNo == 1) quoteNo = 0;
											else quoteNo = 1;
										}
										else if (character == ')')
										{
											if (quoteNo == 1)
											{
												codeFixed.insert(i, '"');
												break;
											}
										}
									}
									else if(firstExpected.equals(")")) { //probably unclosed paren
										if(character == '(') {
											parenCounter++;
										}
										else if(character == ')') {
											if(parenCounter == 0) {
												System.out.println("Too much right parens, removing it (should check it later)...");
												codeFixed.deleteCharAt(i);
												fixed = true;
											}
											else {
												parenCounter--;
											}
										}
										if(character == ';' || character == '}') {
											for(int j = 0; j < parenCounter; j++) {
												codeFixed.insert(i, ')');
												fixed = true;
											}
										}
									}
								}
								if(fixed) {
									System.out.println("Code corrected, wait for another checking loop.");
								}
								else {
									System.out.println("I couldn't find out how to fix it");
								}
								break;
							case "_":
								System.out.println("Probably '_' is outside string. I'm checking now...");
								lineToFixStart = -1;

								//extract line
								for(int i = error.getCharPositionInLine()-1; i >= 0; i--) {
									//System.out.println(i);
									char character = codeFixed.charAt(i);
									if(character == ';' || character == '{') {
										lineToFixStart = i+1;
										break;
									}
								}
								quoteNo = 0;
								fixed = false;
								for(int i = lineToFixStart; i <= error.getCharPositionInLine(); i++) {
									//System.out.println(i);
									char character = codeFixed.charAt(i);
									if(character == '_' && codeFixed.charAt(i-1) == '"') {
										if(codeFixed.charAt(i-2) == '_') {
											System.out.println("Found underline behind string and another in string, removing it...");
											codeFixed.deleteCharAt(i);
										}
										else {
											System.out.println("Found underline behind string, moving it...");
											codeFixed.setCharAt(i-1, '_');
											codeFixed.setCharAt(i, '"');
										}
										fixed = true;
										System.out.println("Done");
									}
								}
								break;
							case "]":
								System.out.println("Probably misplaced ] character");
								codeFixed.deleteCharAt(error.getCharPositionInLine());
								System.out.println("Fixed");
								break;
							case "<EOF>":
								System.out.println("Missing script closing bracket");
								codeFixed.append("}");
								System.out.println("Done");
								break;
							default:
								System.out.println("TODO: fix when '"+wrongInput+"'");
						}
					}
					else if(error.getMessage().startsWith("no viable alternative at input")) {
						String[] parts = error.getMessage().split("'", 3);
						if(parts[1].length() > 1 && parts[1].matches("[+\\-\\*@%].*")) {
							System.out.println("Probably missing [] characters in expression");
							int lineToFixStart = -1;
							for(int i = error.getCharPositionInLine()-1; i >= 0; i--) {
								//System.out.println(i);
								char character = codeFixed.charAt(i);
								if(character == '(') {
									lineToFixStart = i+1;
									break;
								}
							}
							
							int exprChars = 0;
							boolean foundExpr = false;
							for(int i = lineToFixStart; i < codeFixed.length(); i++) {
								//System.out.println(i);
								char character = codeFixed.charAt(i);
								if(character == '[') {
									exprChars++;
									foundExpr = true;
								}
								else if(character == ']') {
									exprChars--;
								}
								else if(character == ')') {
									if(exprChars > 0 && foundExpr) {
										System.out.println("Missing "+exprChars+" ] chars");
										for(int j = 0; j < exprChars; j++) {
											codeFixed.insert(j, ']');
										}
										System.out.println("Fixed");
									}
									else {
										System.out.println("Missed [] characters, inserting it...");
										codeFixed.insert(lineToFixStart, '[');
										codeFixed.insert(i+1, ']');
										System.out.println("Done");
									}
									break;
								}
							}
						}
					}
					else if(error.getMessage().matches("missing '.' at '.'")) {
						String[] parts = error.getMessage().split("'", 3);
						System.out.println("Missing character, inserting it...");
						codeFixed.insert(error.getCharPositionInLine(), parts[1]);
						System.out.println("Done, wait for next checking loop.");
					}
					else if(error.getMessage().startsWith("mismatched input")) {
						int lineToFixStart = -1;
						for(int i = error.getCharPositionInLine()-1; i >= 0; i--) {
							//System.out.println(i);
							char character = codeFixed.charAt(i);
							if(character == ';' || character == '{') {
								lineToFixStart = i+1;
								break;
							}
						}
						//System.out.println("Broken line: "+code.substring(lineToFixStart, error.getCharPositionInLine()));
						int quoteNo = 0;
						for(int i = lineToFixStart; i <= error.getCharPositionInLine(); i++) {
							//System.out.println(i);
							char character = codeFixed.charAt(i);
							if(character == '"') {
								if(codeFixed.charAt(i-1) == '"') {
									char nextCharacter = codeFixed.charAt(i+1);
									if(
										(nextCharacter >= 'a' && nextCharacter <= 'z')
										||  (nextCharacter >= 'A' && nextCharacter <= 'Z')
										||  (nextCharacter >= '0' && nextCharacter <= '9')
										) {
										System.out.println("Wrong quotation mark, removing it...");
										codeFixed.deleteCharAt(i);
										System.out.println("Removed character, wait for next checking loop...");
									}
								} //TODO: if ex. (""VARIABLE) or (""VARIABLE")
								else {
									if(quoteNo == 1) quoteNo = 0;
									else quoteNo = 1;
								}
							}
							else if(character == '\'') {
								System.out.println("Wrong quotation mark, replacing ' with \"");
								codeFixed.setCharAt(i, '"');
								if(quoteNo == 1) quoteNo = 0;
								else quoteNo = 1;
							}
							else if(character == ')') {
								if(quoteNo == 1) {
									codeFixed.insert(i, '"');
									break;
								}
							}
						}
					}
			}
			if(fixAttempts == 3) {
				System.out.println("Halt on 3 tries");
				return false;
			}
			return testCode(codeFixed.toString());
		}
		else {
			AidemMediaParser.ScriptContext scriptContext = parser.script();
			AidemMediaCodeVisitor visitor = new AidemMediaCodeVisitor();
			visitor.visit(scriptContext);
			return true;
		}

		/*List<String> ruleNamesList = Arrays.asList(parser.getRuleNames());
		 String[] tokensList = lexer.getTokenNames();
		 String prettyTree = TreeUtils.toPrettyTree(tree, ruleNamesList);
		 System.out.println(prettyTree);*/
		//ParseTreeAnalyzer.analyzeTree(tree, ruleNamesList, tokens, tokensList);

		/*FireFuncListener listener = new FireFuncListener();
		 ParseTreeWalker.DEFAULT.walk(listener, tree);*/
	}

    public static void main(String[] args) {
		manualFixes = new HashMap<>();
		manualFixes.put("{||SOBJ2^FIND(\"IMGM\")>-1){SOBJT^SET(SOBJ2);}", "@IF(\"SOBJ2^FIND(\"IMGM\")>-1\", \"{SOBJT^SET(SOBJ2);}\", \"\");");
		
		//String debugInstr = "{@IF(\"ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAL\")'TRUE||ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAP\")'TRUE||ANNREKSIO^ISPLAYING(\"ZPLANSZY22\")'TRUE\",\"{@BREAK();}\",\"\"):IREXPOSX^SET(-1);IREXPOSY^SET(-1);@IF(\"ANNREKSIO^GETEVENTNAME()'\"L\"\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAL\");}\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAP\");}\");BEHPLAYHENSFX^RUN();};";
    	//String debugInstr = "{)VARNIEREAGUJ^SET(1}";
		//String debugInstr = "{BEHSELECTOBJS^RUN(\"POMOST_BOBROR\");SOBJECT|IPARAM0^SET(0);BEHUPDATEOBJECTS^RUN();VARSTEMP0^SET(BEHGETOBJECT^RUN(\"\"DZIELNICA3\"));*VARSTEMP0^HIDE();}";
		//String debugInstr = "{BEHPLAYSOUND^RUN(5;);ANNHEAD1^STOP(FALSE);ANNHEAD1^PLAY(\"SPI\");ANNWAND1^STOP(FALSE);ANNWAND1^PLAY(\"ZWISA\");STLMAGIC^STOP(FALSE);ARRTIMEELAPSED^CHANGEAT(1,0);}";
		/*String debugInstr = "{)VARNIEREAGUJ^SET(1}";
		
		fixAttempts = 0;
		if(testCode(debugInstr)) {
			System.out.println("Yey");
		}
		else {
			System.out.println("Not yey");
		}*/
		
		try {
            Scanner scanner = new Scanner(new File("/storage/emulated/0/AppProjects/AidemMediaInterpreterAntlr/src/pl/cba/genszu/amcodetranslator/errors.txt"));
			
			while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
				
				if(manualFixes.containsKey(line)) {
					System.out.println("Line "+line+" has manual fix, because cannot be fixed automatically");
					line = manualFixes.get(line);
				}
				
				fixAttempts = 0;
				if(testCode(line)) {
					System.out.println("Yey");
				}
				else {
					System.out.println("Not yey");
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
