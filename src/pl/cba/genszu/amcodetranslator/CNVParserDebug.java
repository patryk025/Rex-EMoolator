package pl.cba.genszu.amcodetranslator;

import java.io.*;
import java.util.*;
import pl.cba.genszu.amcodetranslator.encoding.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.utils.*;
import pl.cba.genszu.amcodetranslator.lexer.*;

public class CNVParserDebug
{
	//List<Variable> variables = new ArrayList<>();
	HashMap<String, HashMap<String, String>> variables = new HashMap<>();
	int index = -1;

    private String capitalize(String typ)
    {
        String cap = "";
        cap += typ.charAt(0);
        cap += typ.substring(1).toLowerCase();
        return cap;
    }

	private HashMap<String, String> getVariable(String name) throws InterpreterException {
		if(variables.containsKey(name))
			return variables.get(name);
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
					Logger.i("Decyphering " + plik.getName() + "...");
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

		for (String line : lines)
		{
			if(line.contains(":}")) {
				Logger.i("Corrected typo in line " + line);
				line = line.replace(":}", ";}");
			}
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
							Logger.w("probable script errors, entering into recovery mode...");
							tmp = tmp.replace("?", "_");
							Logger.i("Recovered variable name: " + tmp);
							recoveryMode = true;
						}
						variables.put(tmp, new HashMap<String, String>());
						index++;
					}
					catch (ArrayIndexOutOfBoundsException e)
					{
						tmp = null;
						Logger.w("empty variable name, ignoring");
					}
				}
				else if(line.startsWith("NAME"+separator)) {
					Logger.d("Sequence definition detected");
					tmp = line.split("NAME"+separator)[1];
					variables.put(tmp, new HashMap<String, String>());
					index++;
				}
				else if (tmp != null)
				{

					if (line.contains(":TYPE"))
					{
						String varName = line.split(":TYPE"+separator)[0];
						if(varName.contains("?") && !recoveryMode) {
							Logger.w("probable script errors, entering into recovery mode...");
							recoveryMode = true;
						}
						if (!tmp.equals(varName))
						{
							Logger.w("variable names doesn't match (" + tmp + " != " + varName + ")!");
							if(recoveryMode) {
								varName = varName.replace("?", "_");
								Logger.i("Recovered variable name: " + varName);
								if (!tmp.equals(varName))
								{
									Logger.w("variable names still doesn't match (" + tmp + " != " + varName + ")!");
								}
							}
						}
						typ = line.split(":TYPE"+separator)[1];
						typ = capitalize(typ);


						HashMap<String, String> tmpVar = getVariable(tmp);
						tmpVar.put("TYPE", typ);
					}
					else 
					{
						try
						{
							if (!line.endsWith(":="))
							{
								//String[] segments = line.split(":");
								String[] segments = StringUtils.selectiveSplit(line, ':', '=', '∆');
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
										Logger.w("variable names missmatch ("+testName+" != "+tmp+")!");
										Logger.w("line corrected, "+line+" -> ", false);
										line = line.replace(testName+":", tmp+":");
										Logger.log(line);
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
											HashMap<String, String> tmpVar = getVariable(tmp);
											tmpVar.put(method, methodParam + "$$" + tmpVal);
										}
										else
										{
											HashMap<String, String> tmpVar = getVariable(tmp);
											tmpVar.put(method, tmpVal);
										}
									}
									else if(splitTmp.length == 1 && segments[1].contains("ADD ")) { //speaking:ADD sequence
										Logger.d("Olej to");
									}
									else {
										Logger.w("no value after equal sign, ignoring...");
										Logger.d("line => "+line);
									}
								}
								else if(segments.length == 3) {
									String[] val = segments[2].split(separator);
									HashMap<String, String> tmpVar = getVariable(tmp);
									tmpVar.put(segments[1], val[0] + "$$" + val[1]);
								}
								else {
									//last chance
									boolean giveUp = false;
									String[] parts1 = line.split(":", 2);

									if(parts1.length == 2) {
										String[] parts2 = parts1[1].split(separator, 2);
										if(parts2.length == 2) {
											HashMap<String, String> tmpVar = getVariable(tmp);
											tmpVar.put(parts2[0], parts2[1]);
										}
										else giveUp = true;
									}
									else giveUp = true;

									if(giveUp)
										Logger.w("something is wrong with line " + line);
								}
							}
							else
							{
								Logger.w("empty field/signal/method name");
								Logger.d("line => "+line);
							}
						}
						catch (ArrayIndexOutOfBoundsException e)
						{
							e.printStackTrace();
							Logger.d("Linia: " + line);
						}
					}
				}
			}
		}
	}
}
