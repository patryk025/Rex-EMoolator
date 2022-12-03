package pl.cba.genszu.amcodetranslator;

import java.io.*;
import java.util.*;
import pl.cba.genszu.amcodetranslator.encoding.*;
import pl.cba.genszu.amcodetranslator.interpreter.util.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.AMObjects.*;

public class CNVParser
{
	List<Variable> variables = new ArrayList<>();
	int index = -1;

    private String capitalize(String typ)
    {
        String cap = "";
        cap += typ.charAt(0);
        cap += typ.substring(1).toLowerCase();
        return cap;
    }
	
	private Variable getVariable(String name) throws InterpreterException {
		for(Variable var : variables) {
			if(var.getName().equals(name)) return var;
		}
		throw new InterpreterException("Variable "+name+" not found");
	}

    public void parseFile(File plik) throws Exception
	{
        try
        {
            FileReader reader = new FileReader(plik);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
			StringBuilder content = new StringBuilder();
            boolean decypher = false;
			int offset = 0;

            try
            {
                while ((line = bufferedReader.readLine()) != null)
				{
                    if (line.startsWith("{<"))
					{
						String[] tmpParam = line.replace("{<", "").replace(">}", "").split(":");
						offset = Integer.parseInt(tmpParam[1]);
						if (tmpParam[0].toUpperCase().equals("D")) offset *= -1;
						decypher = true;
					}
					else
					{
						content = content.append(line).append("\n");
					}
                }
				if (decypher)
				{
					System.out.println("Decyphering " + plik.getName() + "...");
					parseString(ScriptDecypher.decode(content.toString(), offset));
				}
				else
					parseString(content.toString());
            }
            catch (IOException e)
			{
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e)
		{
            e.printStackTrace();
        }
    }

	public void parseString(String string) throws Exception
	{
		String[] lines = string.split("\n");

		String tmp = "";
		String typ = "";
		
		boolean recoveryMode = false;
		
		String separator = "=";
		
		variables.clear(); // czyść zmienne
		
		Map<String, String> unorderedProperties = new HashMap<>();

		for (String line : lines)
		{
			if(recoveryMode) line = line.replace("?", "_");
			if (!line.equals("") && !line.startsWith("#"))
			{
				if(line.contains(" = ")) separator = " = ";
				else separator = "=";
				if (line.startsWith("OBJECT"+separator))
				{
					try
					{
						tmp = line.split("OBJECT"+separator)[1];
						if(tmp.contains("?") && !recoveryMode) {
							System.out.println("WARNING: probable script errors, entering into recovery mode...");
							tmp = tmp.replace("?", "_");
							System.out.printf("Recovered variable name: %s\n", tmp);
							recoveryMode = true;
						}
						variables.add(new Variable(tmp));
						index++;
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
						tmp = null;
						System.out.println("WARNING: empty variable name, ignoring");
					}
					unorderedProperties.clear();
				}
				else if(line.startsWith("NAME"+separator)) {
					System.out.println("DEBUG: Sequence definition detected");
					tmp = line.split("NAME"+separator)[1];
					variables.add(new Variable(tmp));
					index++;
					unorderedProperties.clear();
				}
				else if (tmp != null)
				{
					
					if (line.contains(":TYPE"))
					{
						String varName = line.split(":TYPE"+separator)[0];
						if(varName.contains("?") && !recoveryMode) {
							System.out.println("WARNING: probable script errors, entering into recovery mode...");
							recoveryMode = true;
						}
						if (!tmp.equals(varName))
						{
							System.out.println("WARNING: variable names doesn't match (" + tmp + " != " + varName + ")!");
							if(recoveryMode) {
								varName = varName.replace("?", "_");
								System.out.printf("Recovered variable name: %s\n", varName);
								if (!tmp.equals(varName))
								{
									System.out.println("WARNING: variable names still doesn't match (" + tmp + " != " + varName + ")!");
								}
							}
						}
						typ = line.split(":TYPE"+separator)[1];
						typ = capitalize(typ);

						
						Variable tmpVar = variables.get(variables.size() - 1);
						tmpVar.setType(typ);
						
						if(unorderedProperties.size() > 0) { //jeśli były jakieś parametry przed TYPE to je wstaw
							for(String key : unorderedProperties.keySet()) {
								tmpVar.setProperty(key, unorderedProperties.get(key));
							}
							unorderedProperties.clear();
						}
					}
					else 
					{
						try
						{
							if (!line.endsWith(":="))
							{
								String[] segments = line.split(":");
								if(segments.length==2) {
									String method = (segments[1]).split(separator)[0];
									String methodParam = "";
									if (method.contains("^"))
									{
										methodParam = method.split("\\^")[1];
										method = method.split("\\^")[0];
									}
									
									String testName = segments[0];
									
									if(!testName.equals(tmp)) {
										System.out.println("WARNING: variable names missmatch ("+testName+" != "+tmp+")!");
										System.out.print("WARNING: line corrected, "+line+" -> ");
										line = line.replace(testName+":", tmp+":");
										System.out.println(line);
									}

									String[] splitTmp = (segments[1]).split(separator);
									/*try {
										splitTmp = (line.split(tmp + ":")[1]).split("=");
									}
									catch(Exception e) {
										System.out.println(Arrays.asList(line.split(tmp + ":")));
										System.out.println(tmp);
										System.out.println(line);
									}*/
									String tmpVal = "";
									if (splitTmp.length == 2)
									{
										tmpVal = splitTmp[1];
										
										if (!methodParam.equals(""))
										{
											//InstructionsBlock.addListenerParam
											//if(!typ.equals("Animo"))
											//System.out.println("Nie wiem co z tym: "+line);
											//System.out.println(variables.get(variables.size()-1).getType()+"."+method);
											variables.get(variables.size() - 1).setProperty(method, methodParam + "$$" + tmpVal);
										}
										else
										{
											Variable tmpVar = variables.get(variables.size() - 1);
											try
											{
												tmpVar.setProperty(method, tmpVal);
											}
											catch(Exception e) {
												if(tmpVar.getClassObj() == null) { //no czasami parametr TYPE jest gdzieś pośrodku zamiast na początku
													System.out.println("Variable defined but not initialised, saving property value for later usage...");
													unorderedProperties.put(method, tmpVal); //cachujemy wartości
												}
												else {
													System.out.println(line);
													e.printStackTrace();
												}
											}
										}
									}
									else if(splitTmp.length == 1 && segments[1].contains("ADD ")) { //speaking:ADD sequence
										String seqName = segments[1].split("ADD ")[1];
										try
										{
											SequenceAM seq = (SequenceAM) getVariable(seqName).getClassObj();
											Variable varTmp = getVariable(tmp);
											if(varTmp.getType().equals("Speaking")) {
												seq.addSpeaking(tmp, (Speaking) varTmp.getClassObj());
												System.out.println("INFO: successfully registered Speaking "+tmp+" in Sequence "+seqName);
											}
											else if(varTmp.getType().equals("Sequence")) {
												seq.addSequence(tmp, (SequenceAM) varTmp.getClassObj());
												System.out.println("INFO: successfully registered Sequence "+tmp+" in Sequence "+seqName);
											}
											else if(varTmp.getType().equals("Simple")) {
												seq.addSimple(tmp, (Simple) varTmp.getClassObj());
												System.out.println("INFO: successfully registered Simple "+tmp+" in Sequence "+seqName);
											}
											else {
												System.out.println("WARNING: incompatible type "+varTmp.getType() + " with Sequence. Ignoring...");
											}
											System.out.println("DEBUG: line => "+line);
										}
										catch (InterpreterException e)
										{
											System.out.println("ERROR: sequence/speaking "+seqName+" does not exists or is declared later");
										}
										catch(ClassCastException e) {
											System.out.println("Variable type casting exception: " + seqName);
											try
											{
												System.out.println("Expected: Sequence, got: " + getVariable(seqName).getType());
												//System.out.println(variables);
												System.out.println(line);
											}
											catch (InterpreterException ignored)
											{}
											throw new Exception("Break");
										}
									}
									else {
										System.out.println("WARNING: no value after equal sign, ignoring...");
										System.out.println("DEBUG: line => "+line);
									}
								}
								else if(segments.length == 3) {
									System.out.println("DEBUG: Sorry but I don't know how to interpret \""+line+"\" for now");
								}
								else {
									System.out.println("WARNING: something is wrong with line " + line);
								}
							}
							else
							{
								System.out.println("WARNING: empty field/signal/method name");
								System.out.println("DEBUG: line => "+line);
							}
						}
						catch (ArrayIndexOutOfBoundsException e)
						{
							e.printStackTrace();
							System.out.println("Linia: " + line);
						}
					}
				}
			}
		}
	}
}
