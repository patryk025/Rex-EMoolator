package pl.cba.genszu.amcodetranslator;

import java.util.*;

public class CodeTranslator
{
	private int intentNumber;
	private char intentChar = '\t';
	private Map<String, String> zmienne;
	
	public CodeTranslator() {
		this.intentNumber = 0;
	}
	
	private String addIntend() {
		String tabs = "";
		for(int i = 1; i <= this.intentNumber; i++) {
			tabs += intentChar;
		}
		return tabs;
	}
	
	public void insertVar(Map<String, String> zmienne) {
		this.zmienne = zmienne;
	}
	
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
		String[] corrParams = new String[ifParams.length];
		/*for(String params : corrParams) {
			
		}*/
		if(ifParams.length == 5) {
			corrParams[0] = ifParams[0].replace("\"", "");
			corrParams[1] = ifChar(ifParams[1].replace("\"", " "));
			if(zmienne.containsKey(ifParams[2].replace("\"", "")) || ifParams[2].contains("TRUE") || ifParams[2].contains("FALSE") || ifParams[2].contains("NULL") || ifParams[2].matches("-?\\d*\\.{0,1}\\d+"))
				corrParams[2] = ifParams[2].replace("\"", "");
			else {
				//corrParams[1] = ".equals(";
				corrParams[2] = ifParams[2];//+")";
			}
			corrParams[3] = ifParams[3].replace("\"", "");
			corrParams[4] = ifParams[4].replace("\"", "");
		}
		else {
			corrParams[0] = ifParams[0].replace("\"", "").replace("&&", " && ")./*tego nie jesten pewien*/replace("'", " == ");
			corrParams[1] = ifParams[1].replace("\"", " ");
			corrParams[2] = ifParams[2].replace("\"", " ");
		}
		return corrParams;
	}
	
	public String translateCode(String code) {
		reset();
		StringBuilder javaCode = new StringBuilder();
		String[] lines = code.split(";");
		for(String line : lines) {
			line = line.replace("^", "."); //translate to Java method firing
			if(line.startsWith("@IF")) {
				System.out.println("Translating "+line);
				String[] params = ifRemoveReduntantQuotes(((String) line.subSequence(4, line.length()-2)).split(","));
				System.out.println(Arrays.asList(params));
				//TODO: wklejanie właściwości CODE obiektu BEHAVIOUR, albo wywołanie methody*/
				if(params.length<5) {
					javaCode.append(addIntend()).append("if(").append(params[0]).append(") {\n");
					this.intentNumber++;
					javaCode.append(addIntend()).append("/* ").append(params[1]).append(" -> obiekt typu ").append(zmienne.get(params[1])).append("*/\n");
					this.intentNumber--;
					javaCode.append(addIntend()).append("}\nelse {\n");
					this.intentNumber++;
					javaCode.append(addIntend()).append("/* ").append(params[2]).append(" -> obiekt typu ").append(zmienne.get(params[2])).append("*/\n");
					this.intentNumber--;
					javaCode.append(addIntend()).append("}\n");
				}
				else {
					javaCode.append(addIntend()).append("if(").append(params[0]).append(params[1]).append(params[2]).append(") {\n");
					this.intentNumber++;
					javaCode.append(addIntend()).append("/* ").append(params[3]).append(" -> obiekt typu ").append(zmienne.get(params[3])).append("*/\n");
					this.intentNumber--;
					javaCode.append(addIntend()).append("}\nelse {\n");
					this.intentNumber++;
					javaCode.append(addIntend()).append("/* ").append(params[4]).append(" -> obiekt typu ").append(zmienne.get(params[4])).append("*/\n");
					this.intentNumber--;
					javaCode.append(addIntend()).append("}\n");
				}
			}
			else if(line.startsWith("@LOOP")) {
				//TODO parsuj pętle, while lub for
			}
			else if(line.startsWith("@BREAK")) {
				javaCode.append(addIntend()).append("break; /*probably, TODO: investigate*/");
			}
			else if(line.startsWith("*")) {
				javaCode.append(addIntend()).append(line.subSequence(1, line.length())).append(" /* pointer are omitted for now */");
			}
			else {
				javaCode.append(addIntend()).append(line).append(";\n");
			}
		}
		return javaCode.toString();
	}
	
	public void reset() {
		this.intentNumber = 0;
	}
}
