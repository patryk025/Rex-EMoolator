package pl.cba.genszu.amcodetranslator.interpreter.util;

import pl.cba.genszu.amcodetranslator.AMObjects.*;
import pl.cba.genszu.amcodetranslator.lexer.*;
import pl.cba.genszu.amcodetranslator.interpreter.*;
import pl.cba.genszu.amcodetranslator.logger.*;

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
			case "Canvasobserver":
			case "Canvas_observer":
				this.classObj = new CanvasObserver();
				break;
			case "Class":
				this.classObj = new ClassAM();
				break;
			case "Cnvloader":
				this.classObj = new CNVLoader();
				break;
			case "Condition":
				this.classObj = new Condition();
				break;
			case "Complexcondition":
				this.classObj = new ComplexCondition();
				break;
			case "Database":
				this.classObj = new Database();
				break;
			case "Double":
				this.classObj = new DoubleAM();
				break;
			case "Episode":
				this.classObj = new Episode();
				break;
			case "Expression":
				this.classObj = new Expression();
				break;
			case "Filter":
				this.classObj = new Filter();
				break;
			case "Font":
				this.classObj = new Font();
				break;
			case "Group":
				this.classObj = new Group();
				break;
			case "Image":
				this.classObj = new Image();
				break;
			case "Inertia":
				this.classObj = new Inertia();
				break;
			case "Integer":
				this.classObj = new IntegerAM();
				break;
			case "Keyboard":
				this.classObj = new Keyboard();
				break;
			case "Matrix":
				this.classObj = new Matrix();
				break;
			case "Mouse":
				this.classObj = new Mouse();
				break;
			case "Multiarray":
				this.classObj = new MultiArray();
				break;
			case "Music":
				this.classObj = new Music();
				break;
			case "Pattern":
				this.classObj = new Pattern();
				break;
			case "Rand":
				this.classObj = new RandAM();
				break;
			case "Scene":
				this.classObj = new Scene();
				break;
			case "Sequence":
				this.classObj = new SequenceAM();
				break;
			case "Simple":
				this.classObj = new Simple();
				break;
			case "Sound":
				this.classObj = new Sound();
				break;
			case "Speaking":
				this.classObj = new Speaking();
				break;
			case "Staticfilter":
				this.classObj = new StaticFilter();
				break;
			case "String":
				this.classObj = new StringAM();
				break;
			case "Struct":
				this.classObj = new Struct();
				break;
			case "Text":
				this.classObj = new Text();
				break;
			case "Timer":
				this.classObj = new Timer();
				break;
			case "Vector":
				this.classObj = new Vector();
				break;
			case "World":
				this.classObj = new World();
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
	
	public void debugPrint(String type, String name, String value) {
		System.out.println("DEBUG: " + type + "." + name + " => " + value);
		Logger logger = Logger.getInstance();
		logger.log(type + "." + name + " => " + value);
	}
	
	public void setProperty(String name, String value) {
		switch (this.type) {
			case "Animo":
				Animo objTmp1 = (Animo) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp1.setDESCRIPTION(value);
						break;
					case "FILENAME":
						objTmp1.setFILENAME(value);
						break;
					case "FPS":
						objTmp1.setFPS(Integer.parseInt(value));
						break;
					case "MONITORCOLLISION":
						objTmp1.setMONITORCOLLISION(stringToBool(value));
						break;
					case "MONITORCOLLISIONALPHA":
						objTmp1.setMONITORCOLLISIONALPHA(stringToBool(value));
						break;
					case "ONCLICK":
						try
						{
							objTmp1.setONCLICK(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONCOLLISION":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp1.getONCOLLISION().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp1.setONCOLLISION(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONFINISHED":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp1.getONFINISHED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp1.setONFINISHED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONFOCUSOFF":
						try
						{
							objTmp1.setONFOCUSOFF(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONFOCUSON":
						try
						{
							objTmp1.setONFOCUSON(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONFRAMECHANGED":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp1.getONFRAMECHANGED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp1.setONFRAMECHANGED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONINIT":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp1.getONINIT().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp1.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONRELEASE":
						try
						{
							objTmp1.setONRELEASE(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONSIGNAL":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp1.getONSIGNAL().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp1.setONSIGNAL(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONSTARTED":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp1.getONSTARTED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp1.setONSTARTED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "PRELOAD":
						objTmp1.setPRELOAD(stringToBool(value));
						break;
					case "PRIORITY":
						objTmp1.setPRIORITY(Integer.parseInt(value));
						break;
					case "RELEASE":
						objTmp1.setRELEASE(stringToBool(value));
						break;
					case "TOCANVAS":
						objTmp1.setTOCANVAS(stringToBool(value));
						break;
					case "VISIBLE":
						objTmp1.setVISIBLE(stringToBool(value));
						break;
				}
				break;
			case "Application":
				Application objTmp2 = (Application) classObj;

				switch (name) {
					case "AUTHOR":
						objTmp2.setAUTHOR(value);
						break;
					case "BLOOMOO_VERSION":
						objTmp2.setBLOOMOO_VERSION(value);
						break;
					case "CREATIONTIME":
						objTmp2.setCREATIONTIME(value);
						break;
					case "DESCRIPTION":
						objTmp2.setDESCRIPTION(value);
						break;
					case "EPISODES":
						objTmp2.setEPISODES(value);
						break;
					case "LASTMODIFYTIME":
						objTmp2.setLASTMODIFYTIME(value);
						break;
					case "PATH":
						objTmp2.setPATH(value);
						break;
					case "STARTWITH":
						objTmp2.setSTARTWITH(value);
						break;
					case "VERSION":
						objTmp2.setVERSION(value);
						break;
				}
				break;
			case "Array":
				ArrayAM objTmp3 = (ArrayAM) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp3.setDESCRIPTION(value);
						break;
					case "ONINIT":
						try
						{
							objTmp3.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
				}
				break;
			case "Behaviour":
				Behaviour objTmp4 = (Behaviour) classObj;

				switch (name) {
					case "CODE":
						try
						{
							objTmp4.setCODE(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "CONDITION":
						objTmp4.setCONDITION(value);
						break;
					case "DESCRIPTION":
						objTmp4.setDESCRIPTION(value);
						break;
				}
				break;
			case "Bool":
				Bool objTmp5 = (Bool) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp5.setDESCRIPTION(value);
						break;
					case "ONBRUTALCHANGED":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp5.getONBRUTALCHANGED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp5.setONBRUTALCHANGED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONCHANGED":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp5.getONCHANGED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp5.setONCHANGED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONINIT":
						try
						{
							objTmp5.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "TOINI":
						objTmp5.setTOINI(stringToBool(value));
						break;
					case "VALUE":
						objTmp5.SET(stringToBool(value));
						break;
				}
				break;
			case "Button":
				Button objTmp6 = (Button) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp6.setDESCRIPTION(value);
						break;
					case "DRAGGABLE":
						objTmp6.setDRAGGABLE(stringToBool(value));
						break;
					case "ENABLE":
						objTmp6.setENABLE(stringToBool(value));
						break;
					case "GFXONCLICK":
						objTmp6.setGFXONCLICK(value);
						break;
					case "GFXONMOVE":
						objTmp6.setGFXONMOVE(value);
						break;
					case "GFXSTANDARD":
						objTmp6.setGFXSTANDARD(value);
						break;
					case "ONACTION":
						try
						{
							objTmp6.setONACTION(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONCLICKED":
						try
						{
							objTmp6.setONCLICKED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONENDDRAGGING":
						try
						{
							objTmp6.setONENDDRAGGING(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONFOCUSOFF":
						try
						{
							objTmp6.setONFOCUSOFF(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONFOCUSON":
						try
						{
							objTmp6.setONFOCUSON(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONINIT":
						try
						{
							objTmp6.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONRELEASED":
						try
						{
							objTmp6.setONRELEASED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONSTARTDRAGGING":
						try
						{
							objTmp6.setONSTARTDRAGGING(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "PRIORITY":
						objTmp6.setPRIORITY(Integer.parseInt(value));
						break;
					case "RECT":
						objTmp6.setRECT(value);
						break;
					case "SNDONMOVE":
						objTmp6.setSNDONMOVE(value);
						break;
					case "VISIBLE":
						objTmp6.setVISIBLE(stringToBool(value));
						break;
				}
				break;
			case "Cnvloader": //są wywołania ale nie wiem po co
				CNVLoader objTmp7 = (CNVLoader) classObj;

				debugPrint(type, name, value);
				/*switch (name) {
				}*/
				break;
			case "Canvas": //nieużywane
				/*Canvas objTmp8 = (Canvas) classObj;

				switch (name) {
				}*/
				break;
			case "Canvas_observer":
			case "Canvasobserver":
				/*Canvas_Observer objTmp9 = (Canvas_Observer) classObj;

				switch (name) {
					case "ONWINDOWFOCUSOFF":
						objTmp9.setONWINDOWFOCUSOFF(value);
						break;
					case "ONWINDOWFOCUSON":
						objTmp9.setONWINDOWFOCUSON(value);
						break;
				}*/
				break;
			case "Class":
				ClassAM objTmp11 = (ClassAM) classObj;

				switch (name) {
					case "BASE":
						objTmp11.setBASE(value);
						break;
					case "DEF":
						objTmp11.setDEF(value);
						break;
				}
				break;
			case "Complexcondition":
				ComplexCondition objTmp12 = (ComplexCondition) classObj;

				switch (name) {
					case "CONDITION1":
						objTmp12.setCONDITION1(value);
						break;
					case "CONDITION2":
						objTmp12.setCONDITION2(value);
						break;
					case "DESCRIPTION":
						objTmp12.setDESCRIPTION(value);
						break;
					case "ONRUNTIMEFAILED":
						try
						{
							objTmp12.setONRUNTIMEFAILED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONRUNTIMESUCCESS":
						try
						{
							objTmp12.setONRUNTIMESUCCESS(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "OPERATOR":
						objTmp12.setOPERATOR(value);
						break;
				}
				break;
			case "Condition":
				Condition objTmp13 = (Condition) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp13.setDESCRIPTION(value);
						break;
					case "ONRUNTIMEFAILED":
						try
						{
							objTmp13.setONRUNTIMEFAILED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONRUNTIMESUCCESS":
						try
						{
							objTmp13.setONRUNTIMESUCCESS(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "OPERAND1":
						objTmp13.setOPERAND1(value);
						break;
					case "OPERAND2":
						objTmp13.setOPERAND2(value);
						break;
					case "OPERATOR":
						objTmp13.setOPERATOR(value);
						break;
				}
				break;
			case "Database":
				Database objTmp14 = (Database) classObj;

				switch (name) {
					case "MODEL":
						objTmp14.setMODEL(value);
						break;
					case "ONINIT":
						try
						{
							objTmp14.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
				}
				break;
			case "Dialog": //nieużywane
				/*Dialog objTmp15 = (Dialog) classObj;

				switch (name) {
				}*/
				break;
			case "Double":
				DoubleAM objTmp16 = (DoubleAM) classObj;

				switch (name) {
					case "TOINI":
						objTmp16.setTOINI(stringToBool(value));
						break;
					case "VALUE":
						value = value.replace("\"", ""); //ee nie wolno
						try {
							objTmp16.SET(Double.parseDouble(value));
						}
						catch(NumberFormatException e) {
							System.out.println("ERROR: błąd rzutowania wartości, otrzymano " + value);
							throw e;
						}
						break;
				}
				break;
			case "Editbox": //nieużywane
				/*Editbox objTmp17 = (Editbox) classObj;

				switch (name) {
				}*/
				break;
			case "Episode":
				Episode objTmp18 = (Episode) classObj;

				switch (name) {
					case "AUTHOR":
						objTmp18.setAUTHOR(value);
						break;
					case "CREATIONTIME":
						objTmp18.setCREATIONTIME(value);
						break;
					case "DESCRIPTION":
						objTmp18.setDESCRIPTION(value);
						break;
					case "LASTMODIFYTIME":
						objTmp18.setLASTMODIFYTIME(value);
						break;
					case "PATH":
						objTmp18.setPATH(value);
						break;
					case "SCENES":
						objTmp18.setSCENES(value);
						break;
					case "STARTWITH":
						objTmp18.setSTARTWITH(value);
						break;
					case "VERSION":
						objTmp18.setVERSION(value);
						break;
				}
				break;
			case "Expression":
				Expression objTmp19 = (Expression) classObj;

				switch (name) {
					case "OPERAND1":
						objTmp19.setOPERAND1(value);
						break;
					case "OPERAND2":
						objTmp19.setOPERAND2(value);
						break;
					case "OPERATOR":
						objTmp19.setOPERATOR(value);
						break;
				}
				break;
			case "Filter":
				Filter objTmp20 = (Filter) classObj;

				switch (name) {
					case "ACTION":
						objTmp20.setACTION(value);
						break;
				}
				break;
			case "Font":
				Font objTmp21 = (Font) classObj;

				objTmp21.DEF(name.replace("DEF_", "").split("_"));
				break;
			case "Grbuffer": //nieużywane
				/*GRBuffer objTmp22 = (GRBuffer) classObj;

				switch (name) {
				}*/
				break;
			case "Group":
				Group objTmp23 = (Group) classObj;

				switch (name) {
					case "ONINIT":
						try
						{
							objTmp23.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
				}
				break;
			case "Image":
				Image objTmp24 = (Image) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp24.setDESCRIPTION(value);
						break;
					case "FILENAME":
						objTmp24.setFILENAME(value);
						break;
					case "MONITORCOLLISION":
						objTmp24.setMONITORCOLLISION(stringToBool(value));
						break;
					case "MONITORCOLLISIONALPHA":
						objTmp24.setMONITORCOLLISIONALPHA(stringToBool(value));
						break;
					case "ONCLICK":
						try
						{
							objTmp24.setONCLICK(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONFOCUSOFF":
						try
						{
							objTmp24.setONFOCUSOFF(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONFOCUSON":
						try
						{
							objTmp24.setONFOCUSON(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONINIT":
						try
						{
							objTmp24.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "PRELOAD":
						objTmp24.setPRELOAD(stringToBool(value));
						break;
					case "PRIORITY":
						objTmp24.SETPRIORITY(Integer.parseInt(value));
						break;
					case "RELEASE":
						objTmp24.setRELEASE(stringToBool(value));
						break;
					case "TOCANVAS":
						objTmp24.setTOCANVAS(stringToBool(value));
						break;
					case "VISIBLE":
						objTmp24.setVISIBLE(stringToBool(value));
						break;
				}
				break;
			case "Inertia":
				debugPrint(type, name, value);
				/*Inertia objTmp25 = (Inertia) classObj;

				switch (name) {
				}*/
				break;
			case "Integer":
				IntegerAM objTmp26 = (IntegerAM) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp26.setDESCRIPTION(value);
						break;
					case "ONBRUTALCHANGED":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp26.getONBRUTALCHANGED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp26.setONBRUTALCHANGED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONCHANGED":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp26.getONCHANGED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp26.setONCHANGED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONINIT":
						try
						{
							objTmp26.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONSIGNAL":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp26.getONSIGNAL().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp26.setONSIGNAL(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "TOINI":
						objTmp26.setTOINI(stringToBool(value));
						break;
					case "VALUE":
						try {
							objTmp26.SET(Integer.parseInt(value));
						}
						catch(Exception e) {
							System.out.println("WARNING: wrong value type assigned, interpreting as Bool, converting to Integer instead...");
							objTmp26.SET(stringBoolToInt(value));
						}
						break;
					case "VARTYPE":
						objTmp26.setVARTYPE(value);
						break;
				}
				break;
			case "Internet": //nieużywane
				debugPrint(type, name, value);
				/*Internet objTmp27 = (Internet) classObj;

				switch (name) {
				}*/
				break;
			case "Joystick": //nieużywane
				debugPrint(type, name, value);
				/*Joystick objTmp28 = (Joystick) classObj;

				switch (name) {
				}*/
				break;
			case "Keyboard":
				Keyboard objTmp29 = (Keyboard) classObj;

				switch (name) {
					case "ONCHAR":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp29.getONCHAR().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp29.setONCHAR(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONKEYDOWN":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp29.getONKEYDOWN().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp29.setONKEYDOWN(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONKEYUP":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp29.getONKEYUP().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp29.setONKEYUP(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
				}
				break;
			case "Matrix":
				Matrix objTmp30 = (Matrix) classObj;
				//debugPrint(type, name, value);

				//do odtworzenia operacje na macierzach
				switch (name) {
					case "BASEPOS":
						objTmp30.setBASEPOS(value);
						break;
					case "CELLHEIGHT":
						objTmp30.setCELLHEIGHT(Integer.parseInt(value));
						break;
					case "CELLWIDTH":
						objTmp30.setCELLWIDTH(Integer.parseInt(value));
						break;
					case "ONLATEST":
						try
						{
							objTmp30.setONLATEST(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONNEXT":
						try
						{
							objTmp30.setONNEXT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "SIZE":
						objTmp30.setSIZE(value);
						break;
				}
				break;
			case "Mouse":
				//debugPrint(type, name, value);
				Mouse objTmp31 = (Mouse) classObj;

				switch (name) {
					case "ONCLICK":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp31.getONCLICK().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp31.setONCLICK(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONDBLCLICK":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp31.getONDBLCLICK().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp31.setONDBLCLICK(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONINIT":
						try
						{
							objTmp31.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONMOVE":
						try
						{
							objTmp31.setONMOVE(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONRELEASE":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp31.getONRELEASE().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp31.setONRELEASE(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "RAW":
						objTmp31.setRAW(Integer.parseInt(value));
						break;
				}
				break;
			case "Multiarray":
				MultiArray objTmp32 = (MultiArray) classObj;

				switch (name) {
					case "DIMENSIONS":
						objTmp32.setDIMENSIONS(Integer.parseInt(value));
						break;
				}
				break;
			case "Music":
				Music objTmp33 = (Music) classObj;

				switch (name) {
					case "FILENAME":
						objTmp33.setFILENAME(value);
						break;
				}
				break;
			case "Netserver": //nieużywane
				debugPrint(type, name, value);
				/*NetServer objTmp34 = (NetServer) classObj;

				switch (name) {
				}*/
				break;
			case "Netclient": //nieużywane
				debugPrint(type, name, value);
				/*NetClient objTmp35 = (NetClient) classObj;

				switch (name) {
				}*/
				break;
			case "Netpeer": //nieużywane
				debugPrint(type, name, value);
				/*NetPeer objTmp36 = (NetPeer) classObj;

				switch (name) {
				}*/
				break;
			case "Pattern":
				Pattern objTmp37 = (Pattern) classObj;
				//debugPrint(type, name, value);
				
				switch (name) {
					case "GRIDX":
						objTmp37.setGRIDX(Integer.parseInt(value));
						break;
					case "GRIDY":
						objTmp37.setGRIDY(Integer.parseInt(value));
						break;
					case "HEIGHT":
						objTmp37.setHEIGHT(Integer.parseInt(value));
						break;
					case "LAYERS":
						objTmp37.setLAYERS(Integer.parseInt(value));
						break;
					case "PRIORITY":
						objTmp37.setPRIORITY(Integer.parseInt(value));
						break;
					case "TOCANVAS":
						objTmp37.setTOCANVAS(stringToBool(value));
						break;
					case "VISIBLE":
						objTmp37.setVISIBLE(stringToBool(value));
						break;
					case "WIDTH":
						objTmp37.setWIDTH(Integer.parseInt(value));
						break;
				}
				break;
			case "Rand":
				//TODO: obiekt typu Rand zrobić wrapper do metod z Javy
				RandAM objTmp38 = (RandAM) classObj;
				debugPrint(type, name, value);

				switch (name) {
				}
				break;
			case "Scene":
				Scene objTmp39 = (Scene) classObj;

				switch (name) {
					case "AUTHOR":
						objTmp39.setAUTHOR(value);
						break;
					case "BACKGROUND":
						objTmp39.setBACKGROUND(value);
						break;
					case "CREATIONTIME":
						objTmp39.setCREATIONTIME(value);
						break;
					case "DESCRIPTION":
						objTmp39.setDESCRIPTION(value);
						break;
					case "DLLS":
						objTmp39.setDLLS(value);
						break;
					case "LASTMODIFYTIME":
						objTmp39.setLASTMODIFYTIME(value);
						break;
					case "MUSIC":
						objTmp39.setMUSIC(value);
						break;
					case "PATH":
						objTmp39.setPATH(value);
						break;
					case "VERSION":
						objTmp39.setVERSION(value);
						break;
				}
				break;
			case "Scroll": //nieużywane
				debugPrint(type, name, value);
				/*Scroll objTmp40 = (Scroll) classObj;

				switch (name) {
				}*/
				break;
			case "Sequence":
				SequenceAM objTmp41 = (SequenceAM) classObj;

				switch (name) {
					case "FILENAME":
						objTmp41.setFILENAME(value);
						break;
					case "ONFINISHED":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp41.getONFINISHED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp41.setONFINISHED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONINIT":
						try
						{
							objTmp41.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONSTARTED":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp41.getONSTARTED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp41.setONSTARTED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "SEQEVENT":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp41.addSEQEVENT(parts[0], Integer.parseInt(parts[1]));
							}
							else
								System.out.println("Error: expected key:value pair, but got only value");
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "VISIBLE":
						objTmp41.setVISIBLE(stringToBool(value));
						break;
				}
				break;
			case "Simple":
				Simple objTmp54 = (Simple) classObj;
				
				switch (name) {
					case "FILENAME":
						objTmp54.setFILENAME(value);
						break;
					case "EVENT":
						objTmp54.setEVENT(value);
						break;
					default:
						debugPrint(type, name, value);
				}
				break;
			case "Sound":
				Sound objTmp42 = (Sound) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp42.setDESCRIPTION(value);
						break;
					case "FILENAME":
						objTmp42.setFILENAME(value);
						break;
					case "FLUSHAFTERPLAYED":
						objTmp42.setFLUSHAFTERPLAYED(stringToBool(value));
						break;
					case "ONFINISHED":
						try
						{
							objTmp42.setONFINISHED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONINIT":
						try
						{
							objTmp42.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONSTARTED":
						try
						{
							objTmp42.setONSTARTED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "PRELOAD":
						objTmp42.setPRELOAD(stringToBool(value));
						break;
					case "RELEASE":
						objTmp42.setRELEASE(stringToBool(value));
						break;
				}
				break;
			case "Speaking":
				Speaking objTmp52 = (Speaking) classObj;
				
				switch (name) {
					case "ANIMOFN":
						//debugPrint(type, name, value);
						objTmp52.setANIMOFN(value);
						break;
					case "PREFIX":
						objTmp52.setPREFIX(value);
						break;
					case "WAVFN":
						objTmp52.setWAVFN(value);
						break;
					case "STARTING":
						objTmp52.setSTARTING(stringToBool(value));
						break;
					case "ENDING":
						objTmp52.setENDING(stringToBool(value));
						break;
				}
				
				break;
			case "Staticfilter":
				StaticFilter objTmp43 = (StaticFilter) classObj;

				switch (name) {
					case "ACTION":
						//debugPrint(type, name, value);
						objTmp43.setACTION(value);
						break;
				}
				break;
			case "String":
				StringAM objTmp44 = (StringAM) classObj;

				switch (name) {
					case "DESCRIPTION":
						objTmp44.setDESCRIPTION(value);
						break;
					case "ONBRUTALCHANGED":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp44.getONBRUTALCHANGED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp44.setONBRUTALCHANGED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONCHANGED":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp44.getONCHANGED().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp44.setONCHANGED(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "ONINIT":
						try
						{
							objTmp44.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "TOINI":
						objTmp44.setTOINI(stringToBool(value));
						break;
					case "VALUE":
						objTmp44.SET(value);
						break;
				}
				break;
			case "Struct":
				Struct objTmp45 = (Struct) classObj;

				switch (name) {
					case "FIELDS":
						objTmp45.addFIELDS(value);
						break;
				}
				break;
			case "System": //nieużywane
				debugPrint(type, name, value);
				/*System objTmp46 = (System) classObj;

				switch (name) {
				}*/
				break;
			case "Text":
				Text objTmp47 = (Text) classObj;

				switch (name) {
					case "FONT":
						objTmp47.setFONT(value);
						break;
					case "HJUSTIFY":
						objTmp47.setHJUSTIFY(value);
						break;
					case "MONITORCOLLISION":
						objTmp47.setMONITORCOLLISION(stringToBool(value));
						break;
					case "MONITORCOLLISIONALPHA":
						objTmp47.setMONITORCOLLISIONALPHA(stringToBool(value));
						break;
					case "ONINIT":
						try
						{
							objTmp47.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "PRIORITY":
						objTmp47.setPRIORITY(Integer.parseInt(value));
						break;
					case "RECT":
						objTmp47.setRECT(value);
						break;
					case "TEXT":
						objTmp47.setTEXT(value);
						break;
					case "TOCANVAS":
						objTmp47.setTOCANVAS(stringToBool(value));
						break;
					case "VISIBLE":
						objTmp47.setVISIBLE(stringToBool(value));
						break;
					case "VJUSTIFY":
						objTmp47.setVJUSTIFY(stringToBool(value));
						break;
				}
				break;
			case "Timer":
				Timer objTmp48 = (Timer) classObj;

				switch (name) {
					case "ELAPSE":
						objTmp48.setELAPSE(Integer.parseInt(value));
						break;
					case "ENABLED":
						objTmp48.setENABLED(stringToBool(value));
						break;
					case "ONINIT":
						try
						{
							objTmp48.setONINIT(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					case "ONTICK":
						try {
							if(value.contains("$$")) {
								String[] parts = value.split("\\$\\$");
								objTmp48.getONTICK().addListenerParam(parts[0], parts[1]);
							}
							else
								objTmp48.setONTICK(new InstructionsBlock(Parser.parseCode(value)));
						}
						catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case "TICKS":
						objTmp48.setTICKS(Integer.parseInt(value));
						break;
				}
				break;
			case "Vector":
				//debugPrint(type, name, value);
				Vector objTmp49 = (Vector) classObj;
				
				switch (name) {
					case "SIZE":
						objTmp49.setSIZE(Integer.parseInt(value));
						break;
					case "VALUE":
						objTmp49.setVALUE(value);
						break;
				}
				break;
			case "Virtualgraphicsobject": //nieużywane
				debugPrint(type, name, value);
				/*VirtualGraphicsObject objTmp50 = (VirtualGraphicsObject) classObj;

				switch (name) {
				}*/
				break;
			case "World":
				World objTmp51 = (World) classObj;

				switch (name) {
					case "FILENAME":
						objTmp51.setFILENAME(value);
						break;
				}
				break;
			default:
				debugPrint(type, name, value);
				break;
		}
	}
	
	private boolean stringToBool(String val) {
        if(val == "TRUE") return true;
        else return false;
    }
	
	private int stringBoolToInt(String val) {
        if(val == "TRUE") return 1;
        else return 0;
    }
}
