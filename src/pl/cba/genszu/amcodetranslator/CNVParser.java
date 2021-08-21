package pl.cba.genszu.amcodetranslator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import pl.cba.genszu.amcodetranslator.AMObjects.*;
import pl.cba.genszu.amcodetranslator.lexer.Lexer;
import pl.cba.genszu.amcodetranslator.interpreter.util.*;

public class CNVParser {

    /*List<String> variables = new ArrayList<>();
    List<Object> variablesData = new ArrayList<>();
    List<Object> variablesTypes = new ArrayList<>();*/
	List<Variable> variables = new ArrayList<>();
	int index = -1;

    private String capitalize(String typ)
    {
        String cap = "";
        cap += typ.charAt(0);
        cap += typ.substring(1).toLowerCase();
        return cap;
    }

    public void parseFile(File plik) throws Exception {
        try
        {
            FileReader reader = new FileReader(plik);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            int lineNo = 0;
            String tmp = "";
            String typ = "";
            String lastFun = null;
            boolean genSwitch = false;

            try
            {
                while ((line = bufferedReader.readLine()) != null)
                {
                    lineNo++;
                    //fun.put();
                    if(!line.equals("") && !line.startsWith("#")) {
                        //obsługa zmiennych z dolarami
                        //line = line.replace("$1", "\""+plik.getAbsolutePath()+"\""); //prawdopodobnie
                        line = line.replace("$COMMON", "/storage/sdcard0/ric/common"); //jeszcze
                        line = line.replace("\\", "/"); //bardziej uniwersalne

                        //zmiana kwadratowych nawiasów na okrągłe
                        //line = line.replace("[", "(");
                        //line = line.replace("]", ")");

                        //jak się funkcja zmieni zamknij breaka i funkcje
                        /*if(!line.startsWith(tmp+":"+lastFun) && genSwitch) {

                        }*/

                        if(line.startsWith("OBJECT=")) {
                            tmp = line.split("OBJECT=")[1];
                            //lista.add(new ParseObjectTmp(tmp));
                            variables.add(new Variable(tmp));
                            index++;
                        }
                        else {
                            if(line.startsWith(tmp+":TYPE")) {
                                typ = line.split(tmp+":TYPE=")[1];
                                typ = capitalize(typ);
								
								variables.get(variables.size()-1).setType(typ);
                            }
                            else {
                                try {
                                    String method = (line.split(tmp+":")[1]).split("=")[0];
                                    String methodParam = "";
                                    if(method.contains("^")) {
                                        methodParam = method.split("\\^")[1];
                                        method = method.split("\\^")[0];
                                    }
                                    String[] splitTmp = (line.split(tmp+":")[1]).split("=");
                                    String tmpVal = "\0";
                                    if(splitTmp.length == 2) {
                                        tmpVal = splitTmp[1];
                                    }
									
									if(!methodParam.equals("")) {
										//InstructionsBlock.addListenerParam
										//if(!typ.equals("Animo"))
										//System.out.println("Nie wiem co z tym: "+line);
                                        //System.out.println(variables.get(variables.size()-1).getType()+"."+method);
										variables.get(variables.size()-1).setProperty(method, methodParam+"$$"+tmpVal);
									}
									else {
										variables.get(variables.size()-1).setProperty(method, tmpVal);
									}
                                }
                                catch(ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                    System.out.println("Linia: "+line);
                                }
                            }
                        }
                    }
                }

				//int tmpIndex = 0;
                /*Gson gson = new GsonBuilder().setPrettyPrinting().create();
                for(Object obj : variables) {
					String json = gson.toJson(obj);
					//System.out.println(variables.get(tmpIndex)+" ("+variablesTypes.get(tmpIndex++)+")");
					System.out.println(json);
					System.out.println();
				}*/
                /*Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(variables);
				
				FileWriter fw = new FileWriter("D:\\Re-SP-master\\variableDebug.json");
				fw.write(json);
				fw.close();
				System.out.println("Done");*/
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
