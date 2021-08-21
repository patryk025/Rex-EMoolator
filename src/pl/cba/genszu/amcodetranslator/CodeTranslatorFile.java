package pl.cba.genszu.amcodetranslator;

//import com.sun.xml.internal.ws.util.StringUtils;

import java.io.*;
import java.util.*;

public class CodeTranslatorFile
{
	private int intentNumber;
	private int intentNumberDecl;
	private char intentChar = '\t';
	private Map<String, String> behCode;

	public CodeTranslatorFile() {
		this.intentNumber = 0;
	}

	private String addIntend() {
		String tabs = "";
		for(int i = 1; i <= this.intentNumber; i++) {
			tabs += intentChar;
		}
		return tabs;
	}

	private String addIntendDecl() {
		String tabs = "";
		for(int i = 1; i <= this.intentNumberDecl; i++) {
			tabs += intentChar;
		}
		return tabs;
	}

	/*public void insertVar(Map<String, String> zmienne) {
		this.zmienne = zmienne;
	}*/

	private String ifChar(String inCode) {
		/*<, > -> bez zmian
		 _  -> ==
		 !_ -> !=*/
		if(inCode.equals(" < ") || inCode.equals(" > ")) return inCode;
		else if(inCode.equals(" _ ")) return " == ";
		else if(inCode.equals(" !_ ")) return " != ";
		else {
			System.out.println("Nie znam: "+inCode);
			return "";
		}
	}

	private String[] ifRemoveReduntantQuotes(String[] ifParams) {
		//System.out.println(Arrays.asList(ifParams));
		//System.out.println(ifParams.length);
		String[] corrParams = new String[ifParams.length];
		/*for(String params : corrParams) {

		 }*/
		if(ifParams.length == 5) {
			corrParams[0] = ifParams[0].replace("\"", "");
			corrParams[1] = ifChar(ifParams[1].replace("\"", " "));
			if(objTypes.containsKey(ifParams[2].replace("\"", "")) || ifParams[2].contains("TRUE") || ifParams[2].contains("FALSE") || ifParams[2].contains("NULL") || ifParams[2].matches("\"-?\\d*\\.?\\d+\""))
				corrParams[2] = ifParams[2].replace("\"", "").replace("TRUE", "true").replace("FALSE", "false").replace("NULL", "null");
			else {
				//corrParams[1] = ".equals(";
				corrParams[2] = ifParams[2];//+")";
			}
			corrParams[3] = ifParams[3].replace("\"", "");
			corrParams[4] = ifParams[4].replace("\"", "");
		}
		else if(ifParams.length == 4) {
			//for @WHILE instruction
			corrParams[0] = ifParams[0].replace("\"", "");
			corrParams[1] = ifChar(ifParams[1].replace("\"", " "));
			if(objTypes.containsKey(ifParams[2].replace("\"", "")) || ifParams[2].contains("TRUE") || ifParams[2].contains("FALSE") || ifParams[2].contains("NULL") || ifParams[2].matches("\"-?\\d*\\.?\\d+\""))
				corrParams[2] = ifParams[2].replace("\"", "");
			else {
				//corrParams[1] = ".equals(";
				corrParams[2] = ifParams[2];//+")";
			}
			corrParams[3] = (String) ifParams[3].subSequence(1, ifParams[3].length()-1);
		}
		else {
			corrParams[0] = ifParams[0].replace("\"", "").replace("&&", " && ")./*tego nie jesten pewien*/replace("'", " == ").replace("TRUE", "true").replace("FALSE", "false").replace("NULL", "null");
			corrParams[1] = ifParams[1].replace("\"", " ");
			corrParams[2] = ifParams[2].replace("\"", " ");
		}
		return corrParams;
	}

	private String[] selectiveSplit(String text, char splitChar, char intendCharMarker, char intendCharMarker2) {
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
	
	private String[] selectiveSplit(String text, char splitChar) {
		List<String> linesArr = new ArrayList<>();
		linesArr.add("");
		int stringNo = 0;
		int intentNo = 0;
		for(int i = 0; i < text.length(); i++) {
			if(text.charAt(i) == '{' || text.charAt(i) == '(')
				intentNo++;
			if (text.charAt(i) == '}' || text.charAt(i) == ')') {
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

	StringBuilder javaCode = new StringBuilder();
	StringBuilder declaration = new StringBuilder();
	static List<String> trCodes = new ArrayList<>();
	private List<String> functiomsTmp = new ArrayList<>();

	public void translateCode(String code, boolean fix) {
		//String[] lines = code.split(";"); //trzeba to inaczej pociachać
		String[] lines = selectiveSplit(code, ';'/*, '{', '}'*/);
		for(String line : lines) {
			line = line.replace("^", "."); //translate to Java method firing
			//System.out.println("Translating "+line);
			if(line.startsWith("@IF")) {
				String[] params = ifRemoveReduntantQuotes(selectiveSplit(((String) line.subSequence(4, line.length()-1)), ',', '(', ')'));
				//System.out.println(Arrays.asList(params));
				//TODO: wklejanie właściwości CODE obiektu BEHAVIOUR, albo wywołanie methody*/
				if(params.length<5) {
					declaration.append(addIntendDecl()).append("if(").append(params[0]).append(") {\n");
					this.intentNumberDecl++;
					declaration.append(addIntendDecl()).append(params[1]).append(".CODE();\n");// /* ").append(params[1]).append(" -> obiekt typu ").append(objTypes.get(params[1])).append("*/
					this.intentNumberDecl--;
					declaration.append(addIntendDecl()).append("}\n");
					if(!params[2].equals("")) {
						declaration.append(addIntendDecl()).append("else {\n");
						this.intentNumberDecl++;
						declaration.append(addIntendDecl()).append(params[2]+".CODE(); ").append("\n");//.append("/* ").append(params[2]).append(" -> obiekt typu ").append(objTypes.get(params[2])).append("*/
						this.intentNumberDecl--;
						declaration.append(addIntendDecl()).append("}\n");
					}
				}
				else {
					declaration.append(addIntendDecl()).append("if(").append(params[0]).append(params[1]).append(params[2]).append(") {\n");
					this.intentNumberDecl++;
					declaration.append(addIntendDecl()).append(params[3]).append(".CODE();\n");// /* ").append(params[3]).append(" -> obiekt typu ").append(objTypes.get(params[3])).append("*/
					this.intentNumberDecl--;
					declaration.append(addIntendDecl()).append("}\n");
					if(!params[4].equals("")) {
						declaration.append(addIntendDecl()).append("else {\n");
						this.intentNumberDecl++;
						declaration.append(addIntendDecl()).append(params[4]+".CODE(); ").append("\n");//.append("/* ").append(params[4]).append(" -> obiekt typu ").append(objTypes.get(params[4])).append("*/
						this.intentNumberDecl--;
						declaration.append(addIntendDecl()).append("}\n");
					}
				}
			}
			else if(line.startsWith("@WHILE")) {
				//@WHILE(VARSTEMP0^GETLENGTH(),">","0","{VARITEMP0^SET(VARSTEMP0^FIND(");");@IF("VARITEMP0","_","-1","BFITMP4","BFITMP5");}");
				String[] params = ifRemoveReduntantQuotes(selectiveSplit(((String) line.subSequence(7, line.length()-1)), ',', '{', '}'));
				declaration.append(addIntendDecl()).append("while(").append(params[0]).append(params[1]).append(params[2]).append(") {\n");
				this.intentNumberDecl++;
				translateCode((String) params[3].subSequence(1, params[3].length()-1), fix);
			}
			else if(line.startsWith("@LOOP")) {
				//TODO parsuj pętle, for
				System.out.println("hehe");
			}
			else if(line.startsWith("@BREAK")) {
				declaration.append(addIntendDecl()).append("break; /*do ustalenia/\n");
			}
			else if(line.contains("*")) {
				declaration.append(addIntendDecl()).append(line.replace("*", "")).append("; /* pointers are omitted for now */\n");
			}
			else if(!line.equals("")){
				declaration.append(addIntendDecl()).append(line).append(";\n");
			}
		}
		if(fix) {
			this.intentNumberDecl--;
			declaration.append(addIntendDecl()).append("}\n");
		}
		//return javaCode.toString();
	}

	static int indeks = -1;

	HashMap<String, String> objTypes = new HashMap<>();
	HashMap<String, String> objParents = new HashMap<>();
	int objNumber = 0;

	public void parseFile(File plik) {
		reset();
		try
		{
			FileReader reader = new FileReader(plik);
			BufferedReader bufferedReader = new BufferedReader(reader);

			//TODO: predict imports
			javaCode.append("package pl.cba.genszu.reksioPiklibToGDX.genClasses;\n\npublic class ").append(plik.getName()).append(" {\n");
			this.intentNumber++;
			/*declaration.append("package pl.cba.genszu.reksioPiklibToGDX.genClasses;\n\npublic class ").append(plik.getName()).append(" {\n");
			this.intentNumberDecl++;*/
			
			String line;
			int lineNo = 0;
			String tmp = "";
			String typ;
			String lastFun = null;
			boolean genSwitch = false;
			String switchParamName = "";

			try
			{
				while ((line = bufferedReader.readLine()) != null)
				{
					lineNo++;
					//fun.put();
					if(!line.equals("") && !line.startsWith("#")) {
						//obsługa zmiennych z dolarami
						line = line.replace("$1", "\""+plik.getAbsolutePath()+"\""); //prawdopodobnie
						line = line.replace("$COMMON", "/storage/sdcard0/ric/common"); //jeszcze
						line = line.replace("\\", "/");
						
						//zmiana kwadratowych nawiasów na okrągłe
						line = line.replace("[", "(");
						line = line.replace("]", ")");
						
						//jak się funkcja zmieni zamknij breaka i funkcje
						if(!line.startsWith(tmp+":"+lastFun) && genSwitch) {
							declaration.append(addIntendDecl()).append("}\n");
							this.intentNumberDecl--;
							declaration.append(addIntendDecl()).append("}\n");
							genSwitch = false;
						}
						
						if(line.startsWith("OBJECT=")) {
							tmp = line.split("OBJECT=")[1];
							//lista.add(new ParseObjectTmp(tmp));
							if(declaration.length() > 0) {
								this.intentNumberDecl--;
								declaration.append(addIntendDecl()).append("}\n\n");
							}
						}
						else {
							if(line.startsWith(tmp+":TYPE")) {
								typ = line.split(tmp+":TYPE=")[1];

								objTypes.put(tmp, "obj"+objNumber);
								objParents.put(tmp, capitalize(typ));

								declaration.append(addIntendDecl()).append("class obj").append(objNumber).append(" extends ").append(capitalize(typ)).append(" {\n");
								this.intentNumberDecl++;
								javaCode.append(addIntend()).append("obj").append(objNumber).append(" ").append(tmp).append(" = new obj").append(objNumber++).append("();\n");
								/*if(fun.add(typ)) {
									lista.add(new ParseObjectTmp(typ));
									//ilosc++;
									indeks = lista.size()-1;
								}
								else {
									/*Iterator<String> it = fun.iterator();
									int tmpIt = lista.size()-1;

									while(it.hasNext()) {
										if(it.next().equals(typ)) {
											indeks = tmpIt;
										}
										tmpIt--;
									}*

									int tmpIt = 0;
									Iterator<ParseObjectTmp> it = lista.iterator();
									while(it.hasNext()) {
										if(it.next().typeName.equals(typ)) {
											indeks = tmpIt;
										}
										tmpIt++;
									}
								}*/
							}
							else {
								/*try {
									lista.get(indeks).fields.add(((line.split(tmp+":")[1]).split("=")[0]).split("\\^")[0]);
								}
								catch(ArrayIndexOutOfBoundsException e) {
									try {
										System.out.println("Plik: "+plik.getName()+" Linia nr "+lineNo);
										System.out.println("Kroki:");
										System.out.println("1. "+line);
										System.out.println("2. line.split(\""+tmp+":\" -> "+Arrays.toString(line.split(tmp+":")));
										System.out.println("3. ("+line.split(tmp+":")[1]+").split(\"=\") -> "+Arrays.toString((line.split(tmp+":")[1]).split("=")));
										System.out.println("4. ("+(line.split(tmp+":")[1]).split("=")[0]+").split(\"\\^)\" -> " + Arrays.toString(((line.split(tmp+":")[1]).split("=")[0]).split("\\^")));
										System.out.println("5. Wynik -> "+((line.split(tmp+":")[1]).split("=")[0]).split("\\^")[0]);
									}
									catch(ArrayIndexOutOfBoundsException e2) {
										System.out.println("Wyłapano wyjątek");
									}
								}*/
								
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
									
									//funkcja zamykająca breaka przeniesiona wyżej
									
									if(method.equals("ONFINISHED")) {
										switchParamName="str1";
										if(!genSwitch) {
											declaration.append(addIntendDecl()).append("void ").append(method).append("(").append("String ").append(switchParamName).append(") {\n");
											this.intentNumberDecl++;
											declaration.append(addIntendDecl()).append("switch(").append(switchParamName).append(") {\n");
										}
										this.intentNumberDecl++;
										genSwitch=true;
										lastFun = method;
									}
									if(tmpVal.startsWith("{")) {
										//todo: dopisz typ do parametru, może być, a właściwie powinien być dziedziczący, przy eventach do ustalenia
										//todo: grupuj funkcje o tej samej nazwie, funkcje eventowe popinane do pętli zdarzeń
										if(!genSwitch) {
											declaration.append(addIntendDecl()).append("void ").append(method).append("(").append(methodParam).append(") {\n");
											this.intentNumberDecl++;
										}
										if(!tmpVal.equals("{}")/* && !tmpVal.equals("{￺")*/) {
											if(genSwitch) {
												declaration.append(addIntendDecl()).append("case \"").append(methodParam).append("\":\n");
												this.intentNumberDecl++;
											}
											
											tmpVal = tmpVal.substring(1, tmpVal.length()-2);
											translateCode(tmpVal, !genSwitch);
											if(genSwitch) {
												declaration.append(addIntendDecl()).append("break;\n");
												this.intentNumberDecl-=2;
											}
											//this.intentNumberDecl--;
											//declaration.append(addIntendDecl()).append("}\n\n");
										}
									}
									else {
										//Detection variables with other type than String
										String type = "String";
										if(tmpVal.equals("TRUE") || tmpVal.equals("FALSE")) {
											type = "boolean";
											tmpVal = tmpVal.toLowerCase();
										}
										else if(tmpVal.matches("^[0-9]*$")) //no chyba daruję sobie sprawdzanie czy int czy long
											type = "int";
										else if(tmpVal.matches("^[0-9]*[.,]?[0-9]*")) //bez różnicy czy float czy double
											type = "float";
										String str = (!type.equals("String")) ? "" : "\"";
										declaration.append(addIntendDecl()).append(type).append(" ").append(method).append(" = ").append(str).append(tmpVal).append(str).append(";\n");
									}
									lastFun = method;
								}
								catch(ArrayIndexOutOfBoundsException e) {
									e.printStackTrace();
									System.out.println("Linia: "+line);
									/*this.intentNumberDecl--;
									declaration.append(addIntendDecl()).append("}");*/
								}
							}
						}
					}
				}
				//System.out.println(lista);
				javaCode.append("}");
				this.intentNumberDecl--;
				declaration.append(addIntendDecl()).append("}");
				System.out.println("Deklaracje: ");
				System.out.println(declaration.toString());
				System.out.println("\nKod: ");
				System.out.println(javaCode.toString());
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String capitalize(String typ)
	{
		String cap = "";
		cap += typ.charAt(0);
		cap += typ.substring(1).toLowerCase();
		return cap;
	}

	public void reset() {
		this.intentNumber = 0;
		this.intentNumberDecl = 0;
		this.javaCode.setLength(0);
		this.declaration.setLength(0);
		this.objTypes.clear();
		this.objParents.clear();
	}
}
