package pl.genschu.bloomooemulator.loader;

import com.badlogic.gdx.Gdx;
import pl.genschu.bloomooemulator.interpreter.Context;
import pl.genschu.bloomooemulator.interpreter.exceptions.InterpreterException;
import pl.genschu.bloomooemulator.interpreter.factories.VariableFactory;
import pl.genschu.bloomooemulator.interpreter.variable.Variable;
import pl.genschu.bloomooemulator.encoding.ScriptDecypher;
import pl.genschu.bloomooemulator.interpreter.variable.types.SequenceVariable;
import pl.genschu.bloomooemulator.utils.StringUtils;

import java.io.*;
import java.util.*;

public class CNVParser
{
    Map<String, String> tmpVariableInfo = new HashMap<>();
    int index = -1;

    private String capitalize(String typ)
    {
        String cap = "";
        cap += typ.charAt(0);
        cap += typ.substring(1).toLowerCase();
        return cap;
    }

    public void parseFile(File plik, Context context) throws Exception
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
                        if (tmpParam[0].equalsIgnoreCase("D")) offset *= -1;
                        decypher = true;
                    }
                    else
                    {
                        content.append(line).append("\n");
                    }
                }
                if (decypher)
                {
                    System.out.println("Decyphering " + plik.getName() + "...");
                    parseString(ScriptDecypher.decode(content.toString(), offset), context);
                }
                else
                    parseString(content.toString(), context);
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

    public void parseString(String string, Context context) throws Exception
    {
        // TODO: przepisać to inaczej (serio to ja pisałem?)
        /*
        String[] lines = string.split("\n");

        String tmp = "";
        String typ;

        boolean recoveryMode = false;

        String separator;

        Map<String, String> unorderedProperties = new HashMap<>();

        for (String line : lines)
        {
            if(line.contains(":}")) {
                Gdx.app.log("CNVParser", "Corrected typo in line " + line);
                line = line.replace(":}", ";}");
            }
            if(recoveryMode) line = line.replace("?", "_");
            if (!line.isEmpty() && !line.startsWith("#"))
            {
                if(line.contains(" = ")) separator = " = ";
                else separator = "=";
                if (line.startsWith("OBJECT"+separator))
                {
                    try
                    {
                        tmp = line.split("OBJECT"+separator)[1];
                        if(tmp.contains("?") && !recoveryMode) {
                            Gdx.app.log("CNVParser", "probable script errors, entering into recovery mode...");
                            tmp = tmp.replace("?", "_");
                            Gdx.app.log("CNVParser", "Recovered variable name: " + tmp);
                            recoveryMode = true;
                        }
                        tmpVariableInfo.clear();
                        tmpVariableInfo.put("NAME", tmp);
                        index++;
                    }
                    catch (ArrayIndexOutOfBoundsException e)
                    {
                        tmp = null;
                        Gdx.app.log("CNVParser", "empty variable name, ignoring");
                    }
                    unorderedProperties.clear();
                }
                else if(line.startsWith("NAME"+separator)) {
                    Gdx.app.log("CNVParser", "Sequence definition detected");
                    tmp = line.split("NAME"+separator)[1];
                    tmpVariableInfo.clear();
                    tmpVariableInfo.put("NAME", tmp);
                    index++;
                    unorderedProperties.clear();
                }
                else if (tmp != null)
                {

                    if (line.contains(":TYPE"))
                    {
                        String varName = line.split(":TYPE"+separator)[0];
                        if(varName.contains("?") && !recoveryMode) {
                            Gdx.app.log("CNVParser", "probable script errors, entering into recovery mode...");
                            recoveryMode = true;
                        }
                        if (!tmp.equals(varName))
                        {
                            Gdx.app.log("CNVParser", "variable names doesn't match (" + tmp + " != " + varName + ")!");
                            if(recoveryMode) {
                                varName = varName.replace("?", "_");
                                Gdx.app.log("CNVParser", "Recovered variable name: " + varName);
                                if (!tmp.equals(varName))
                                {
                                    Gdx.app.log("CNVParser", "variable names still doesn't match (" + tmp + " != " + varName + ")!");
                                }
                            }
                        }
                        typ = line.split(":TYPE"+separator)[1];
                        typ = capitalize(typ);

                        Variable tmpVar = VariableFactory.createVariable(typ, tmpVariableInfo.get("NAME"), null);

                        if(!unorderedProperties.isEmpty()) { //jeśli były jakieś parametry przed TYPE to je wstaw
                            for(String key : unorderedProperties.keySet()) {
                                tmpVar.setAttribute(key, unorderedProperties.get(key));
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
                                        Gdx.app.log("CNVParser", "variable names missmatch ("+testName+" != "+tmp+")!");
                                        Gdx.app.log("CNVParser", "line corrected, old line: "+line);
                                        line = line.replace(testName+":", tmp+":");
                                        Gdx.app.log("CNVParser", "new line: "+line);
                                    }

                                    String[] splitTmp = (segments[1]).split(separator);

                                    String tmpVal = "";
                                    if (splitTmp.length == 2)
                                    {
                                        tmpVal = splitTmp[1];

                                        if (!methodParam.isEmpty())
                                        {
                                            variables.get(variables.size() - 1).setAttribute(method, methodParam + "$$" + tmpVal);
                                        }
                                        else
                                        {
                                            Variable tmpVar = variables.get(variables.size() - 1);
                                            try
                                            {
                                                tmpVar.setAttribute(method, tmpVal);
                                            }
                                            catch(Exception e) {
                                                if(tmpVar == null) { //no czasami parametr TYPE jest gdzieś pośrodku zamiast na początku
                                                    Gdx.app.log("CNVParser", "Variable defined but not initialised, saving \""+methodParam+"\" value for later use...");
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
                                            SequenceVariable seq = (SequenceVariable) getVariable(seqName);
                                            Variable varTmp = getVariable(tmp);
											/*
											if(varTmp.getType().equals("Speaking")) {
												seq.addSpeaking(tmp, (SpeakingVariable) varTmp);
												Gdx.app.log("CNVParser", "successfully registered Speaking "+tmp+" in Sequence "+seqName);
											}
											else if(varTmp.getType().equals("Sequence")) {
												seq.addSequence(tmp, (SequenceVariable) varTmp);
												Gdx.app.log("CNVParser", "successfully registered Sequence "+tmp+" in Sequence "+seqName);
											}
											else if(varTmp.getType().equals("Simple")) {
												seq.addSimple(tmp, (SimpleVariable) varTmp);
												Gdx.app.log("CNVParser", "successfully registered Simple "+tmp+" in Sequence "+seqName);
											}
											else {
												Gdx.app.log("CNVParser", "incompatible type "+varTmp.getType() + " with Sequence. Ignoring...");
											}

											 /
                                            Gdx.app.log("CNVParser", "line => "+line);
                                        }
                                        catch (InterpreterException e)
                                        {
                                            Gdx.app.log("CNVParser", "sequence/speaking "+seqName+" does not exists or is declared later");
                                        }
                                        catch(ClassCastException e) {
                                            Gdx.app.log("CNVParser", "Variable type casting exception: " + seqName);
                                            try
                                            {
                                                Gdx.app.log("CNVParser", "Expected: Sequence, got: " + getVariable(seqName).getType());
                                                //System.out.println(variables);
                                                Gdx.app.log("CNVParser", line);
                                            }
                                            catch (InterpreterException ignored)
                                            {}
                                            throw new Exception("Break");
                                        }
                                    }
                                    else {
                                        Gdx.app.log("CNVParser", "no value after equal sign, ignoring...");
                                        Gdx.app.log("CNVParser", "line => "+line);
                                    }
                                }
                                else if(segments.length == 3) {
                                    System.out.println(line);
                                    String[] val = segments[2].split(separator);
                                    variables.get(variables.size() - 1).setAttribute(segments[1], val[0] + "$$" + val[1]);
                                }
                                else {
                                    //last chance
                                    boolean giveUp = false;
                                    String[] parts1 = line.split(":", 2);

                                    if(parts1.length == 2) {
                                        String[] parts2 = parts1[1].split(separator, 2);
                                        if(parts2.length == 2) {
                                            variables.get(variables.size() - 1).setAttribute(parts2[0], parts2[1]);
                                        }
                                        else giveUp = true;
                                    }
                                    else giveUp = true;

                                    if(giveUp)
                                        Gdx.app.log("CNVParser", "something is wrong with line " + line);
                                }
                            }
                            else
                            {
                                Gdx.app.log("CNVParser", "empty field/signal/method name");
                                Gdx.app.log("CNVParser", "line => "+line);
                            }
                        }
                        catch (ArrayIndexOutOfBoundsException e)
                        {
                            e.printStackTrace();
                            Gdx.app.log("CNVParser", "Linia: " + line);
                        }
                    }
                }
            }
        }*/
    }
}
