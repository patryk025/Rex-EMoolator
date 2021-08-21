package pl.cba.genszu.amcodetranslator.interpreter.util;

import pl.cba.genszu.amcodetranslator.AMObjects.*;
import pl.cba.genszu.amcodetranslator.lexer.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;

public class Variable
{
	private String name;
	private String type;
	private Object classObj;

	public Variable() {
		this.name = "";
		this.type = "";
		this.classObj = null;
	}
	
	public Variable(String name) {
		this.name = name;
	}
	
	public Variable(String name, String type, Object classObj) {
		this.name = name;
		this.type = type;
		this.classObj = classObj;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setType(String type)
	{
		this.type = type;
		
		switch (type) {
			case "Animo":
				this.classObj = new Animo();
				break;
			case "Application":
				this.classObj = new Application();
				break;
			case "Array":
				this.classObj = new ArrayAM();
				break;
			case "Behaviour":
				this.classObj = new Behaviour();
				break;
			case "Bool":
				this.classObj = new Bool();
				break;
			case "Button":
				this.classObj = new Button();
				break;
			case "Canvas_observer":
				this.classObj = new CanvasObserver();
				break;
			case "Condition":
				this.classObj = new Condition();
				break;
			case "Complexcondition":
				this.classObj = new Complexcondition();
				break;
			case "Database":
				this.classObj = new Database();
				break;
			case "Episode":
				this.classObj = new Episode();
				break;
			case "Group":
				this.classObj = new Group();
				break;
			case "Image":
				this.classObj = new Image();
				break;
			case "Integer":
				this.classObj = new IntegerAM();
				break;
			case "Keyboard":
				this.classObj = new Keyboard();
				break;
			case "Mouse":
				this.classObj = new Mouse();
				break;
			case "Scene":
				this.classObj = new Scene();
				break;
			case "Sequence":
				this.classObj = new SequenceAM();
				break;
			case "Sound":
				this.classObj = new Sound();
				break;
			case "String":
				this.classObj = new StringAM();
				break;
			case "Struct":
				this.classObj = new Struct();
				break;
			case "Timer":
				this.classObj = new Timer();
				break;
			default:
				System.out.println("Nieznany typ: "+type);
		}
	}

	public String getType()
	{
		return type;
	}

	public void setClassObj(Object classObj)
	{
		this.classObj = classObj;
	}

	public Object getClassObj()
	{
		return classObj;
	}
	
	public void setProperty(String name, String value) {
		switch (this.type) {
			case "Animo":
				Animo objTmp = (Animo) classObj;

				switch (name) {
					case "FILENAME":
						objTmp.setFILENAME(value);
						break;
					case "TOCANVAS":
						objTmp.setTOCANVAS(stringToBool(value));
						break;
					case "VISIBLE":
						objTmp.setVISIBLE(stringToBool(value));
						break;
					case "FPS":
						objTmp.setFPS(Integer.parseInt(value));
						break;
					case "PRIORITY":
						objTmp.SETPRIORITY(Integer.parseInt(value));
						break;
					case "ONFINISHED":
						try
						{
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp.getONFINISHED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp.setONFINISHED(new InstructionsBlock(Lexer.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONSTARTED":
						try
						{
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp.getONSTARTED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp.setONSTARTED(new InstructionsBlock(Lexer.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "PRELOAD":
						objTmp.setPRELOAD(stringToBool(value));
						break;
					case "RELEASE":
						objTmp.setRELEASE(stringToBool(value));
						break;
					case "MONITORCOLLISION":
						objTmp.setMONITORCOLLISION(stringToBool(value));
						break;
					case "MONITORCOLLISIONALPHA":
						objTmp.setMONITORCOLLISIONALPHA(stringToBool(value));
						break;
					case "ONCOLLISION":
						objTmp.setONCOLLISION(value);
						break;
					case "ONINIT":
						try
						{
							objTmp.setONINIT(new InstructionsBlock(Lexer.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONFRAMECHANGED":
						try
						{
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp.getONFRAMECHANGED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp.setONFRAMECHANGED(new InstructionsBlock(Lexer.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Application":
				Application objTmp2 = (Application) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp2.setDESCRIPTION(value);
						break;
					case "CREATIONTIME":
						objTmp2.setCREATIONTIME(value);
						break;
					case "LASTMODIFYTIME":
						objTmp2.setLASTMODIFYTIME(value);
						break;
					case "AUTHOR":
						objTmp2.setAUTHOR(value);
						break;
					case "VERSION":
						objTmp2.setVERSION(value);
						break;
					case "EPISODES":
						objTmp2.setEPISODES(value);
						break;
					case "STARTWITH":
						objTmp2.setSTARTWITH(value);
						break;
					case "PATH":
						objTmp2.setPATH(value);
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Array":
				System.out.println("Nieznane pole: "+type+"."+name);
				System.out.println("DEBUG: wartość -> "+value);
				break;
			case "Behaviour":
				Behaviour objTmp3 = (Behaviour) classObj;

				switch (name) {
					case "CONDITION":
						objTmp3.setCONDITION(value);
						break;
					case "CODE":
						try
						{
							objTmp3.setCODE(new InstructionsBlock(Lexer.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Bool":
				Bool objTmp4 = (Bool) classObj;

				switch (name) {
					case "VALUE":
						objTmp4.SET(stringToBool(value));
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Button":
				Button objTmp5 = (Button) classObj;

				switch (name) {
					case "RECT":
						objTmp5.SETRECT(value);
						break;
					case "ENABLE":
						objTmp5.setENABLE(stringToBool(value));
						break;
					case "VISIBLE":
						objTmp5.setVISIBLE(stringToBool(value));
						break;
					case "DRAGGABLE":
						objTmp5.setDRAGGABLE(stringToBool(value));
						break;
					case "GFXSTANDARD":
						objTmp5.setGFXSTANDARD(value);
						break;
					case "GFXONCLICK":
						objTmp5.setGFXONCLICK(value);
						break;
					case "GFXONMOVE":
						objTmp5.setGFXONMOVE(value);
						break;
					case "ONACTION":
						objTmp5.setONACTION(value);
						break;
					case "ONFOCUSON":
						try
						{
							objTmp5.setONFOCUSON(new InstructionsBlock(Lexer.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONFOCUSOFF":
						try
						{
							objTmp5.setONFOCUSOFF(new InstructionsBlock(Lexer.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Canvas_observer":
				System.out.println("Nieznane pole: "+type+"."+name);
				System.out.println("DEBUG: wartość -> "+value);
				break;
			case "Condition":
				Condition objTmp15 = (Condition) classObj;
				
				switch(name) {
					case "OPERAND1":
						objTmp15.setOPERAND1(value);
						break;
					case "OPERATOR":
						objTmp15.setOPERATOR(value);
						break;
					case "OPERAND2":
						objTmp15.setOPERAND2(value);
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Complexcondition":
				Complexcondition objTmp16 = (Complexcondition) classObj;

				switch(name) {
					case "CONDITION1":
						objTmp16.setCONDITION1(value);
						break;
					case "OPERATOR":
						objTmp16.setOPERATOR(value);
						break;
					case "CONDITION2":
						objTmp16.setCONDITION2(value);
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Database":
				Database objTmp6 = (Database) classObj;

				switch (name) {
					case "MODEL":
						//TODO: pobierz strukturę ze zmiennych
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Episode":
				Episode objTmp7 = (Episode) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp7.setDESCRIPTION(value);
						break;
					case "CREATIONTIME":
						objTmp7.setCREATIONTIME(value);
						break;
					case "LASTMODIFYTIME":
						objTmp7.setLASTMODIFYTIME(value);
						break;
					case "AUTHOR":
						objTmp7.setAUTHOR(value);
						break;
					case "VERSION":
						objTmp7.setVERSION(value);
						break;
					case "SCENES":
						objTmp7.setSCENES(value);
						break;
					case "STARTWITH":
						objTmp7.setSTARTWITH(value);
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Group":
				Group objTmp8 = (Group) classObj;

				switch (name) {
						/*case "":

						 break;*/
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Image":
				Image objTmp9 = (Image) classObj;

				switch (name) {
					case "FILENAME":
						objTmp9.setFILENAME(value);
						break;
					case "TOCANVAS":
						objTmp9.setTOCANVAS(stringToBool(value));
						break;
					case "VISIBLE":
						objTmp9.setVISIBLE(stringToBool(value));
						break;
					case "PRIORITY":
						objTmp9.SETPRIORITY(Integer.parseInt(value));
						break;
					case "PRELOAD":
						objTmp9.setPRELOAD(stringToBool(value));
						break;
					case "RELEASE":
						objTmp9.setRELEASE(stringToBool(value));
						break;
					case "MONITORCOLLISION":
						objTmp9.setMONITORCOLLISION(stringToBool(value));
						break;
					case "MONITORCOLLISIONALPHA":
						objTmp9.setMONITORCOLLISIONALPHA(stringToBool(value));
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Integer":
				IntegerAM objTmp10 = (IntegerAM) classObj;

				switch (name) {
					case "VALUE":
						objTmp10.SET(Integer.parseInt(value));
						break;
					case "TOINI":
						objTmp10.setTOINI(stringToBool(value));
						break;
					case "ONBRUTALCHANGED":
						try
						{
							objTmp10.setONBRUTALCHANGED(new InstructionsBlock(Lexer.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Keyboard":
				System.out.println("Nieznane pole: "+type+"."+name);
				System.out.println("DEBUG: wartość -> "+value);
				break;
			case "Mouse":
				System.out.println("Nieznane pole: "+type+"."+name);
				System.out.println("DEBUG: wartość -> "+value);
				break;
			case "Scene":
				Scene objTmp11 = (Scene) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp11.setDESCRIPTION(value);
						break;
					case "CREATIONTIME":
						objTmp11.setCREATIONTIME(value);
						break;
					case "LASTMODIFYTIME":
						objTmp11.setLASTMODIFYTIME(value);
						break;
					case "AUTHOR":
						objTmp11.setAUTHOR(value);
						break;
					case "VERSION":
						objTmp11.setVERSION(value);
						break;
					case "PATH":
						objTmp11.setPATH(value);
						break;
					case "BACKGROUND":
						objTmp11.setBACKGROUND(value);
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Sequence":
				SequenceAM objTmp17 = (SequenceAM) classObj;
				
				switch(name) {
					case "FILENAME":
						objTmp17.setFILENAME(value);
						break;
					case "ONINIT":
						try
						{
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp17.getONINIT().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp17.setONINIT(new InstructionsBlock(Lexer.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONFINISHED":
						try
						{
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp17.getONFINISHED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp17.setONFINISHED(new InstructionsBlock(Lexer.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Sound":
				Sound objTmp12 = (Sound) classObj;

				switch (name) {
					case "FILENAME":
						objTmp12.setFILENAME(value);
						break;
					case "PRELOAD":
						objTmp12.setPRELOAD(stringToBool(value));
						break;
					case "RELEASE":
						objTmp12.setRELEASE(stringToBool(value));
						break;
					case "FLUSHAFTERPLAYED":
						objTmp12.setFLUSHAFTERPLAYED(stringToBool(value));
						break;
					case "ONSTARTED":
						objTmp12.setONSTARTED(value);
						break;
					case "ONFINISHED":
						objTmp12.setONFINISHED(value);
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "String":
				StringAM objTmp13 = (StringAM) classObj;

				switch (name) {
					case "VALUE":
						objTmp13.SET(value);
						break;
					case "TOINI":
						objTmp13.setTOINI(stringToBool(value));
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			case "Struct":
				Struct objTmp14 = (Struct) classObj;

				switch (name) {
					case "FIELDS":
						objTmp14.addFIELDS(value);
						break;
					default:
						System.out.println("Nieznane pole: "+type+"."+name);
						System.out.println("DEBUG: wartość -> "+value);
						break;
				}
				break;
			default:

				break;
		}
	}
	
	private boolean stringToBool(String val) {
        if(val == "TRUE") return true;
        else return false;
    }
}
