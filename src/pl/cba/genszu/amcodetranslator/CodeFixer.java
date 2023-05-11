package pl.cba.genszu.amcodetranslator;

import java.util.*;
import org.antlr.v4.runtime.*;
import pl.cba.genszu.amcodetranslator.antlr.*;
import pl.cba.genszu.amcodetranslator.listener.*;
import pl.cba.genszu.amcodetranslator.objects.parser.*;
import pl.cba.genszu.amcodetranslator.utils.*;
import pl.cba.genszu.amcodetranslator.visitors.*;

public class CodeFixer
{
	//TODO: REFACTORIZE IT PAT
	
	static List<SyntaxError> syntaxErrors;
	static int fixAttempts = 0;
	static int oldErrorAmount = -1;

	private static void showErrorMark(String code, int pos)
	{
		System.out.println(code);
		for (int i = 0; i < code.length(); i++)
		{
			if (i == pos)
				System.out.print("^");
			else
				System.out.print(" ");
		}
		System.out.println();
	}
	
	public static void reset() {
		fixAttempts = 0;
		try {
			syntaxErrors.clear();
		}
		catch(NullPointerException ignored) {} //It's NEP, nothing to see here. Really, you can go further, it's nothing here.
	}
	
	public static boolean fixCode(String code)
	{
		if(code.equals("{}")) {
			System.out.println("It's empty code block. It's nothing to check here ;)");
			return true;
		}

		fixAttempts++;
		try {
			oldErrorAmount = syntaxErrors.size();
		}
		catch(NullPointerException e) {
			oldErrorAmount = -1;
		}

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

		if (syntaxErrors.size() > 0)
		{
			StringBuilder codeFixed = new StringBuilder(code);

			System.out.println("Ups, errors: " + syntaxErrors.size());
			//for(SyntaxError error : syntaxErrors) {
			SyntaxError error = syntaxErrors.get(0);

			showErrorMark(code, error.getCharPositionInLine());

			if (error.getException() != null && error.getMessage() == null)
			{
				String errorName = error.getException().toString();
				System.out.println("Exception: " + errorName);
				String[] parts = errorName.split("\\(");
				try
				{
					parts[1] = parts[1].substring(0, parts[1].length() - 2).replace("'", "");
				}
				catch (Exception e)
				{
					System.out.println("Wut?");
					return false;
				}
				//System.out.println(error.getMessage());
				switch (parts[0])
				{
					case "LexerNoViableAltException":
						switch (parts[1])
						{
							case ":":
								System.out.println("Detected typo at " + error.getLine() + ":" + error.getCharPositionInLine() + ". ':' should be ';'");
								codeFixed.setCharAt(error.getCharPositionInLine(), ';');
								System.out.println("Typo fixed");
								break;
							default:
								System.out.println("Sorry, I don't know how to fix '" + parts[1] + "'");
						}
						break;
					default:
						System.out.println("TODO: unsupported exception for now");
				}
			}
			else
			{
				System.out.println("Error: " + error.getMessage());
				if (error.getMessage().startsWith("extraneous input"))
				{
					String[] parts = error.getMessage().split("'", 5);
					String wrongInput = parts[1];
					switch (wrongInput)
					{
						case ")":
							System.out.println("Misplaced right parenthesis, looking for errors...");
							codeFixed.deleteCharAt(error.getCharPositionInLine());
							boolean fixed = false;
							int parenCounter = 0;
							//System.out.println(codeFixed.length());
							for (int i = error.getCharPositionInLine(); i < codeFixed.length() && !fixed; i++)
							{
								//System.out.println(i);
								char character = codeFixed.charAt(i);
								if (character == '(')
								{
									parenCounter++;
								}
								else if (character == ')')
								{
									if (parenCounter == 0)
									{
										System.out.println("Too much right parens, removing it (should check it later)...");
										codeFixed.deleteCharAt(i);
										fixed = true;
									}
									else
									{
										parenCounter--;
									}
								}
								else if ((character == ';' || character == '}') && parenCounter > 0)
								{
									for (int j = 0; j < parenCounter; j++)
									{
										codeFixed.insert(i, ')');
										fixed = true;
									}
								}
							}
							if (fixed)
							{
								System.out.println("Code corrected");
							}
							else
							{
								System.out.println("I couldn't find out how to fix it");
							}
							break;
						case ";":
							//probably problems with quotation marks
							System.out.println("Fixer in progress...");

							int lineToFixStart = -1;

							//extract line
							for (int i = error.getCharPositionInLine() - 1; i >= 0; i--)
							{
								//System.out.println(i);
								char character = codeFixed.charAt(i);
								if ((character == ';' && codeFixed.charAt(i + 1) != '}') || character == '{')
								{
									lineToFixStart = i + 1;
									break;
								}
							}
							String firstExpected = parts[3].trim();
							//System.out.println("Broken line: "+code.substring(lineToFixStart, error.getCharPositionInLine()));
							int quoteNo = 0;
							parenCounter = 0;
							fixed = false;
							for (int i = lineToFixStart; i <= error.getCharPositionInLine(); i++)
							{
								//System.out.println(i);
								char character = codeFixed.charAt(i);
								if (firstExpected.equals("("))
								{
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
								else if (firstExpected.equals(")"))
								{ //probably unclosed paren
									if (character == '(')
									{
										parenCounter++;
									}
									else if (character == ')')
									{
										if (parenCounter == 0)
										{
											System.out.println("Too much right parens, removing it (should check it later)...");
											codeFixed.deleteCharAt(i);
											fixed = true;
										}
										else
										{
											parenCounter--;
										}
									}
									if (character == ';' || character == '}')
									{
										for (int j = 0; j < parenCounter; j++)
										{
											codeFixed.insert(i, ')');
											fixed = true;
										}
									}
								}
							}
							if (fixed)
							{
								System.out.println("Code corrected, wait for another checking loop.");
							}
							else
							{
								System.out.println("I couldn't find out how to fix it");
							}
							break;
						case "}": //bigger problems with unclosed parens (more than one)
							System.out.println("Fixer in progress...");

							lineToFixStart = 0;

							firstExpected = parts[3].trim();
							//System.out.println("Broken line: "+code.substring(lineToFixStart, error.getCharPositionInLine()));
							parenCounter = 0;
							fixed = false;
							for (int i = lineToFixStart; i <= error.getCharPositionInLine(); i++)
							{
								//System.out.println(i);
								char character = codeFixed.charAt(i);
								if (firstExpected.equals(")"))
								{ //probably unclosed paren
									if (character == '(')
									{
										parenCounter++;
									}
									else if (character == ')')
									{
										if (parenCounter == 0)
										{
											System.out.println("Too much right parens, removing it (should check it later)...");
											codeFixed.deleteCharAt(i);
											fixed = true;
										}
										else
										{
											parenCounter--;
										}
									}
									else if (character == ';' || character == '}')
									{
										for (int j = 0; j < parenCounter; j++)
										{
											codeFixed.insert(i, ')');
											fixed = true;
										}
										parenCounter = 0;
									}
								}
							}
							if (fixed)
							{
								System.out.println("Code corrected, wait for another checking loop.");
							}
							else
							{
								System.out.println("I couldn't find out how to fix it");
							}
							break;
						case "_":
							System.out.println("Probably '_' is outside string. I'm checking now...");
							lineToFixStart = -1;

							//extract line
							for (int i = error.getCharPositionInLine() - 1; i >= 0; i--)
							{
								//System.out.println(i);
								char character = codeFixed.charAt(i);
								if (character == ';' || character == '{')
								{
									lineToFixStart = i + 1;
									break;
								}
							}
							quoteNo = 0;
							fixed = false;
							for (int i = lineToFixStart; i <= error.getCharPositionInLine(); i++)
							{
								//System.out.println(i);
								char character = codeFixed.charAt(i);
								if (character == '_' && codeFixed.charAt(i - 1) == '"')
								{
									if (codeFixed.charAt(i - 2) == '_')
									{
										System.out.println("Found underline behind string and another in string, removing it...");
										codeFixed.deleteCharAt(i);
									}
									else
									{
										System.out.println("Found underline behind string, moving it...");
										codeFixed.setCharAt(i - 1, '_');
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
						case ">":
							firstExpected = parts[3].trim();
							if(firstExpected.equals("\"") && codeFixed.charAt(error.getCharPositionInLine()-1) == '<') {
								System.out.println("Incorrect inequality operator \"<>\", replacing to \"!'\"...");
								codeFixed.setCharAt(error.getCharPositionInLine() - 1, '!');
								codeFixed.setCharAt(error.getCharPositionInLine(), '\'');
								System.out.println("Done");
							}
							else if(firstExpected.equals(")")) {
								System.out.println("Error: got \">\", but expected \"^\", correcting...");
								codeFixed.setCharAt(error.getCharPositionInLine(), '^');
								System.out.println("Done");
							}
							break;
						default:
							System.out.println("TODO: fix when '" + wrongInput + "'");
					}
				}
				else if (error.getMessage().startsWith("no viable alternative at input"))
				{
					String[] parts = error.getMessage().split("'", 3);
					if (parts[1].length() > 1 && parts[1].matches("[+\\-\\*@%].*"))
					{
						System.out.println("Probably missing [] characters in expression");
						int lineToFixStart = -1;
						for (int i = error.getCharPositionInLine() - 1; i >= 0; i--)
						{
							//System.out.println(i);
							char character = codeFixed.charAt(i);
							if (character == '(')
							{
								lineToFixStart = i + 1;
								break;
							}
						}

						int exprChars = 0;
						boolean foundExpr = false;
						for (int i = lineToFixStart; i < codeFixed.length(); i++)
						{
							//System.out.println(i);
							char character = codeFixed.charAt(i);
							if (character == '[')
							{
								exprChars++;
								foundExpr = true;
							}
							else if (character == ']')
							{
								exprChars--;
							}
							else if (character == ')')
							{
								if (exprChars > 0 && foundExpr)
								{
									System.out.println("Missing " + exprChars + " ] chars");
									for (int j = 0; j < exprChars; j++)
									{
										codeFixed.insert(j, ']');
									}
									System.out.println("Fixed");
								}
								else
								{
									System.out.println("Missed [] characters, inserting it...");
									codeFixed.insert(lineToFixStart, '[');
									codeFixed.insert(i + 1, ']');
									System.out.println("Done");
								}
								break;
							}
						}
					}
					else if(codeFixed.charAt(0) == '[') {
						System.out.println("Error: first character must be \"{\". Fixing...");
						codeFixed.setCharAt(0, '{');
						System.out.println("Done");
					}
					else if(parts[1].endsWith(">")) {
						System.out.println("Error: got \">\", but expected \"^\", correcting...");
						codeFixed.setCharAt(error.getCharPositionInLine(), '^');
						System.out.println("Done");
					}
					else if(parts[1].endsWith(",")) {
						System.out.println("Probably missmatched square brackets in parameters, checkimg if there are some arithmetics...");
						boolean isArithmetic = false;
						for (int i = error.getCharPositionInLine() - 1; i >= 0; i--)
						{
							//System.out.println(i);
							char character = codeFixed.charAt(i);
							if (character == '+' || character == '-' || character == '*' || character == '@' || character == '%')
							{
								System.out.println("Found arithmetic operator, adding missing bracket...");
								codeFixed.insert(error.getCharPositionInLine(), ']');
								break;
							}
						}
					}
				}
				else if (error.getMessage().matches("missing '.' at '.'"))
				{
					String[] parts = error.getMessage().split("'", 3);
					System.out.println("Missing character, inserting it...");
					codeFixed.insert(error.getCharPositionInLine(), parts[1]);
					System.out.println("Done, wait for next checking loop.");
				}
				else if (error.getMessage().startsWith("mismatched input"))
				{
					int lineToFixStart = -1;
					for (int i = error.getCharPositionInLine() - 1; i >= 0; i--)
					{
						//System.out.println(i);
						char character = codeFixed.charAt(i);
						if (character == ';' || character == '{')
						{
							lineToFixStart = i + 1;
							break;
						}
					}
					//System.out.println("Broken line: "+code.substring(lineToFixStart, error.getCharPositionInLine()));
					int quoteNo = 0;
					for (int i = lineToFixStart; i <= error.getCharPositionInLine(); i++)
					{
						//System.out.println(i);
						char character = codeFixed.charAt(i);
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
				}
			}
			//if (fixAttempts == 3 || syntaxErrors.size() == oldErrorAmount)
			if(fixAttempts == 10)
			{
				System.out.println("Halt on 10 tries");
				return false;
			}
			return fixCode(codeFixed.toString());
		}
		else
		{
			AidemMediaParser.ScriptContext scriptContext = parser.script();
			AidemMediaCodeVisitor visitor = new AidemMediaCodeVisitor();
			visitor.visit(scriptContext);
			return true;
		}
	}
}
