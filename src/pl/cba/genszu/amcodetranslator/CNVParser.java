package pl.cba.genszu.amcodetranslator;

import java.io.*;
import java.util.*;
import pl.cba.genszu.amcodetranslator.encoding.*;
import pl.cba.genszu.amcodetranslator.interpreter.util.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;

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

	public void parseString(String string)
	{
		String[] lines = string.split("\n");

		String tmp = "";
		String typ = "";
		
		boolean recoveryMode = false;

		for (String line : lines)
		{
			if(recoveryMode) line = line.replace("?", "_");
			if (!line.equals("") && !line.startsWith("#"))
			{
				
				if (line.startsWith("OBJECT="))
				{
					try
					{
						tmp = line.split("OBJECT=")[1];
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
				}
				else if (tmp != null)
				{
					if (line.contains(":TYPE"))
					{
						String varName = line.split(":TYPE=")[0];
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
						typ = line.split(":TYPE=")[1];
						typ = capitalize(typ);

						variables.get(variables.size() - 1).setType(typ);
					}
					else 
					{
						try
						{
							if (!line.endsWith(":="))
							{
								String method = (line.split(":")[1]).split("=")[0];
								String methodParam = "";
								if (method.contains("^"))
								{
									methodParam = method.split("\\^")[1];
									method = method.split("\\^")[0];
								}

								String[] splitTmp = (line.split(tmp + ":")[1]).split("=");
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
										variables.get(variables.size() - 1).setProperty(method, tmpVal);
									}
								}
								else {
									System.out.println("WARNING: no value after equal sign, ignoring...");
									System.out.println("DEBUG: line => "+line);
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
