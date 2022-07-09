package pl.cba.genszu.amcodetranslator;

import java.io.*;
import java.util.*;
import pl.cba.genszu.amcodetranslator.logger.*;

public class Main {

    public static void search(final String pattern, final File folder, List<String> result, boolean recurse) {
        for (final File f : folder.listFiles()) {

            if (f.isDirectory() && recurse) {
                search(pattern, f, result, true);
            }

            if (f.isFile()) {
                if (f.getName().matches(pattern)) {
                    //System.out.println(f.getAbsolutePath());
                    if(result != null)
                        result.add(f.getAbsolutePath());
					/*try {
						save(f.getAbsolutePath().replace("/storage/sdcard0/Download/Reksio i Czarodzieje/Reksio i Czarodzieje", "/storage/sdcard0/Download/Reksio i Czarodzieje/Reksio i Czarodzieje_decr")+".decr.txt",decryptAMCode(f.getAbsolutePath()));
					}
					catch(StringIndexOutOfBoundsException e) {
						save(f.getAbsolutePath().replace("/storage/sdcard0/Download/Reksio i Czarodzieje/Reksio i Czarodzieje", "/storage/sdcard0/Download/Reksio i Czarodzieje/Reksio i Czarodzieje_decr")+".nochange.txt",wczytaj(f.getAbsolutePath()));
					}*/
                    //System.out.println(f.getAbsolutePath());
                }
            }
        }
    }

    public static void main(String[] args) {
		
		/* copy System.out to file (for now, Logger in building) */
		/*try {
			FileOutputStream file = new FileOutputStream("/sdcard/skrypty/logi.txt");
			TeePrintStream tee = new TeePrintStream(file, System.out);
			System.setOut(tee);
			System.setErr(tee);
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}*/
		
		//"BEHCLICK_"+SOBJECT|IDNAME
		//value = "{ANNOBJECT0^STOP(FALSE);ANNOBJECT0^SETFRAME"STATE0",1);}"
		
		/* decypher - debug helper */
		/*try
        {
            FileReader reader = new FileReader("/sdcard/skrypty/risp/Dane/Intro/MainMenu/04plaza/4PLAZA.cnv");
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
				PrintWriter pw = new PrintWriter("/sdcard/skrypty/4PLAZA.cnv.dek");
				
				if (decypher)
				{
					pw.write(ScriptDecypher.decode(content.toString(), offset));
				}
				else pw.write(content.toString());
				pw.close();
            }
            catch (IOException e)
			{
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e)
		{
            e.printStackTrace();
        }*/
		
        List<String> pliki = new ArrayList<>();

        search(".*\\.cnv", new File("/sdcard/skrypty"), pliki, true);
        //HashSet<String> fun = new HashSet<>();
        //List<ParseObjectTmp> lista = new ArrayList<>();
        //CodeTranslatorFile ct = new CodeTranslatorFile();
		CNVParser cp = new CNVParser();
		String tmp = null;
		
		//pliki.clear();
		//pliki.add("/sdcard/skrypty/risp/Dane/Intro/MainMenu/04plaza/4PLAZA.cnv");

        for (String e : pliki) {
            System.out.println(e);
            //ct.parseFile(new File(e));
			try {
				cp.parseFile(new File(e));
			}
			catch (Exception ex) {
				ex.printStackTrace();
				break;
			}
			tmp = e;
            //break;
        }
		
		
        try {
            //Lexer.parseCode("@IF(IREXPOSX,\"!_\",$1,\"{L_BRET^SET(FALSE);}\",\"\")");
            //Lexer.parseCode("@IF(\"VARMELODIA'3\",\"\",\"{BRETURN^SET(FALSE);}\")");
            /*InstructionsList test = Lexer.parseCode("{@IF(\"ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAL\")'TRUE||ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAP\")'TRUE||ANNREKSIO^ISPLAYING(\"ZPLANSZY22\")'TRUE\",\"{@BREAK();}\",\"\");IREXPOSX^SET(-1);IREXPOSY^SET(-1);@IF(\"ANNREKSIO^GETEVENTNAME()'\"L\"\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAL\");}\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAP\");}\");BEHPLAYHENSFX^RUN();};");
			test = Lexer.parseCode("{@IF(\"ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAL\")'TRUE&&ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAP\")'TRUE||ANNREKSIO^ISPLAYING(\"ZPLANSZY22\")'TRUE\",\"{@BREAK();}\",\"\");IREXPOSX^SET(-1);IREXPOSY^SET(-1);@IF(\"ANNREKSIO^GETEVENTNAME()'\"L\"\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAL\");}\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAP\");}\");BEHPLAYHENSFX^RUN();};");
			test = Lexer.parseCode("{@IF(\"ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAL\")'TRUE||ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAP\")'TRUE&&ANNREKSIO^ISPLAYING(\"ZPLANSZY22\")'TRUE\",\"{@BREAK();}\",\"\");IREXPOSX^SET(-1);IREXPOSY^SET(-1);@IF(\"ANNREKSIO^GETEVENTNAME()'\"L\"\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAL\");}\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAP\");}\");BEHPLAYHENSFX^RUN();};");
			*/
			
			//InstructionsList test = Lexer.parseCode("@LOOP(\"{@INT(\"L_IDIGIT\",SPASSWORD^GET([_I_-5+SPASSWORD^LENGTH()]));*[\"ZNACZKI\"+_I_]^SHOW();*[\"ZNACZKI\"+_I_]^SETFRAME([L_IDIGIT-1]);}\",[5-SPASSWORD^LENGTH()],SPASSWORD^LENGTH(),1)");
			//Lexer.parseCode("@LOOP(\"{*[\"GES\"+_I_+\"_BUTTON\"]^ENABLE();*[\"GES\"+_I_]^PLAY(\"RANDOM\");}\",1,6,1)");
			//System.out.println("@INT(\"L_IDIGIT\",\n\tSPASSWORD^GET([_I_-5+SPASSWORD^LENGTH()])\n);\n*[\"ZNACZKI\"+_I_]^SHOW();\n*[\"ZNACZKI\"+_I_]^SETFRAME([L_IDIGIT-1]);");
			//Lexer.parseCode("@LOOP(\"{@INT(\"L_IDIGIT\",SPASSWORD^GET([_I_-5+SPASSWORD^LENGTH()]));*[\"ZNACZKI\"+_I_]^SHOW();*[\"ZNACZKI\"+_I_]^SETFRAME([L_IDIGIT-1]);}\",[5-SPASSWORD^LENGTH()],SPASSWORD^LENGTH(),1)");
			//Lexer.expressionToTree("2+2*2-2--2");
			//ExpressionParser.expressionToTree("a*(b+c)");
			//ExpressionParser.expressionToTree("*L_SARRNAME^GET(L_INEXTINDEX)");
			//ExpressionSolver.optimaliseExpression("2+2*2-2--2"); //oczekiwane 6
			//ExpressionSolver.optimaliseExpression("3x-2+x"); //oczekiwane 4x-2
			//ExpressionSolver.optimaliseExpression("2(3x-4)+4(2-x)"); //oczekiwane 2x
			//Lexer.expressionToTree("TESTVAL^LENGTH()+15+TMP");
			//Lexer.parseCode("@LOOP(\"{@IF(\"ARRFIELDSSTATE^GET(_I_)'5\",\"{L_BFAILURE^SET(TRUE);}\",\"\");@IF(\"ARRFIELDSSTATE^GET(_I_)!'3&&ARRFIELDSSTATE^GET(_I_)!'4\",\"{L_BSUCCESS^SET(FALSE);}\",\"\");}\",0,16,1)");
			//Lexer.parseCode("{@INT(\"L_ICLONEINDEX\",THIS^GETCLONEINDEX());@INT(\"L_IFRAMEINDEX\",THIS^GETCFRAMEINEVENT());@IF(\"ARRFALLING^GET(L_ICLONEINDEX,1)'L_IFRAMEINDEX\",\"{*[\"ANNFALLINGROCK_\"+L_ICLONEINDEX]^STOP(TRUE);}\",\"\");}");
			//CodeBeautifier.beautify("{VARSTEMP0^SET([\"BEHCLICK_\"+SOBJECT|IDNAME]);VARSLASTFOCUS^SET(SOBJECT|IDNAME);@IF(\"VARSCURRENTITEM\",\"!_\",\"\"NULL\"\",\"BFITMP2\",\"BFITMP3\");}");
			//Lexer.parseCode("{VARSTEMP0^SET([\"BEHCLICK_\"+SOBJECT|IDNAME]);VARSLASTFOCUS^SET(SOBJECT|IDNAME);@IF(\"VARSCURRENTITEM\",\"!_\",\"\"NULL\"\",\"BFITMP2\",\"BFITMP3\");}");
			//Lexer.parseCode("SOBJECT|IPARAM0^SET(1)");
			//CodeBeautifier.beautify("{@LOOP(\"{S1^SET([\"ENEMY\"+_I_]);SPLAYERGOON^REMOVEBEHAVIOUR([\"ONBRUTALCHANGED^\"+S1]);SPLAYERGOON^ADDBEHAVIOUR([\"ONBRUTALCHANGED^\"+S1],\"BEHGOONENEMYNOCOLL\");}\",0,IENEMYNO,1);}");
			//Lexer.parseCode("{@LOOP(\"{S1^SET([\"ENEMY\"+_I_]);SPLAYERGOON^REMOVEBEHAVIOUR([\"ONBRUTALCHANGED^\"+S1]);SPLAYERGOON^ADDBEHAVIOUR([\"ONBRUTALCHANGED^\"+S1],\"BEHGOONENEMYNOCOLL\");}\",0,IENEMYNO,1);}");
			//Lexer.parseCode("\"ONBRUTALCHANGED^\"+S1");
			//Lexer.parseCode("{S1^SET([\"ENEMY\"+IENEMYNO]);CLSCHASZCZEENEMYOBJ^NEW(S1,0,IX,IY);GENEMIES^ADD(S1);GENEMYNAMES^ADD(S1);SPLAYERGOON^ADDBEHAVIOUR([\"ONBRUTALCHANGED^\"+S1],\"BEHGOONENEMY\");IENEMYNO^INC();ARRMAPA^SET(IX,IY,S1);SPOLE^SET(\"\");}");
			//Lexer.parseCode("ART0^GET([ART0^GETSIZE()-1])");
			//Lexer.parseCode("{@IF(\"ICIK\",\"_\",ART0^GET([ART0^GETSIZE()-1]),\"BFITMP35\",\"\");}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //try {
			/*Lexer.parseCode("{ARRTRAFIONY^ADD(FALSE,FALSE,FALSE);ANIMOSZCZUR^HIDE();ANIMOSZCZUR1^HIDE();ANIMOSZCZUR2^HIDE();ANIMOREKSIO^PLAY(\"BEZRUCH\");VARKOKOS0^SET(0);VARKOKOS1^SET(0);VARKOKOS2^SET(0);VARKOKOS3^SET(0);VARKOKOS4^SET(0);VARKOKOS5^SET(0);VARKOKOS6^SET(0);VARKOKOS7^SET(0);VARKOKOS8^SET(0);VARKOKOS9^SET(0);VARKOKOS10^SET(0);VARKOKOS11^SET(0);VARKOKOS12^SET(0);VARKOKOS13^SET(0);VARKOKOS14^SET(0);VARKOKOS15^SET(0);VARPIRAT1DX^SET(0);VARPIRAT1DY^SET(0);VARPIRAT2DX^SET(0);VARPIRAT2DY^SET(0);VARPIRAT3DX^SET(0);VARPIRAT3DY^SET(0);VARSTANPIRATA1^SET(2);VARSTANPIRATA2^SET(0);VARSTANPIRATA3^SET(0);VARPIRAT1LEVEL^SET(0);VARPIRAT2LEVEL^SET(0);VARPIRAT3LEVEL^SET(0);VARSCORE^SET(0);VARLICZBAPITATOW^SET(15);SNDENTRE^PLAY();BEHENABLEBUTTONS^RUN();}");
            System.out.println();
            System.out.println();
			Lexer.parseCode("{BEHINITSCENEMUSIC^RUN(); !BUTTONS_INIT; GRPBUTTONS^ADD(\"DO5_BUT\");GRPBUTTONS^ADD(\"DRZWI_BUT\");@IF(\"G_IEP2MAPREVEALINGSTAGE'0\",\"{BUTELKA_BUT^ENABLE();GRPBUTTONS^ADD(\"BUTELKA_BUT\");BUTELKA^PLAY(\"BUTELKA\");}\",\"\");@IF(\"G_IEP2MAPREVEALINGSTAGE'1||G_IEP2MAPREVEALINGSTAGE'2\",\"{BBUTELKA_BUTTON^ENABLE();GRPBUTTONS^ADD(\"BBUTELKA_BUTTON\");}\",\"\");@IF(\"G_IEP2MAPREVEALINGSTAGE>'1&&G_IEP2MAPREVEALINGSTAGE<'7\",\"{BMAPA0_BUTTON^ENABLE();GRPBUTTONS^ADD(\"BMAPA0_BUTTON\");}\",\"\");@IF(\"G_IEP2MAPREVEALINGSTAGE'9\",\"{BMAPA_BUTTON^ENABLE();GRPBUTTONS^ADD(\"BMAPA_BUTTON\");}\",\"\");@IF(\"G_IEP2DOOROPENINGSTAGE'2\",\"{BKLUCZ_BUTTON^ENABLE();GRPBUTTONS^ADD(\"BKLUCZ_BUTTON\");}\",\"\");@IF(\"G_IEP2DOOROPENINGSTAGE'3\",\"{DRZWI^SETFRAME(1);KLUCZ^SHOW();}\",\"{DRZWI^SETFRAME(0);}\"); !ANIMATIONS_INIT; FALA1^PLAY(\"PLAY\");@IF(\"G_IEP2GENERALSTAGE'0\",\"BEHFIRSTENTRY\",\"BEHENTRY\");POZYCJAREKSIA^SET(4);}");
            System.out.println();
            System.out.println();
			Lexer.parseCode("{@BOOL(\"BRETURN\",TRUE);@IF(\"VARMELODIA'3\",\"\",\"{BRETURN^SET(FALSE);}\");@IF(\"VARKROLEWNAETAPMUZYKA'0\",\"\",\"{BRETURN^SET(FALSE);}\");@IF(\"VARZLECONAMUZYKA'1\",\"\",\"{BRETURN^SET(FALSE);}\");@RETURN(BRETURN);}");
			System.out.println();
			System.out.println();
			Lexer.parseCode("@BOOL(\"BRETURN\",FALSE);@IF(\"ANIMOSZCZUR2^GETPOSITIONX()<'650\",\"{BRETURN^SET(TRUE);}\",\"{ANIMOSZCZUR2^SETPOSITION(0,0);BEHPIRAT3GORAP^RUN();}\");@RETURN(BRETURN);");
			System.out.println();
			System.out.println();*/
			/*Lexer.parseCode("@BOOL(\"BRETURN\",TRUE);@INT(\"ILEFT1\",*[$1]^GETPOSITIONX());@INT(\"ITOP1\",*[$1]^GETPOSITIONY());@INT(\"IRIGHT1\",*[$1]^GETWIDTH());IRIGHT1^SET([ILEFT1+IRIGHT1]);@INT(\"IBOTTOM1\",*[$1]^GETHEIGHT());IBOTTOM1^SET([ITOP1+IBOTTOM1]);@INT(\"ILEFT2\",*[$2]^GETPOSITIONX());@INT(\"ITOP2\",*[$2]^GETPOSITIONY());@INT(\"IRIGHT2\",*[$2]^GETWIDTH());IRIGHT2^SET([ILEFT2+IRIGHT2]);@INT(\"IBOTTOM2\",*[$2]^GETHEIGHT());IBOTTOM2^SET([ITOP2+IBOTTOM2]);@IF(\"ILEFT1>IRIGHT2\",\"{BRETURN^SET(FALSE);}\",\"\");@IF(\"IRIGHT1<ILEFT2\",\"{BRETURN^SET(FALSE);}\",\"\");@IF(\"ITOP1>IBOTTOM2\",\"{BRETURN^SET(FALSE);}\",\"\");@IF(\"IBOTTOM1<ITOP2\",\"{BRETURN^SET(FALSE);}\",\"\");@RETURN(BRETURN);");
		}
		catch (Exception e) {
			e.printStackTrace();
		}*/
		//CodeBuilder cb = new CodeBuilder("twojaStara");
		
		//System.out.println(cb.generateJavaCode());
		
		/*File file = new File("/sdcard/err.txt");
		try
		{
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setErr(ps);
			
		}
		catch (FileNotFoundException e)
		{}*/
		
		/*int test = 0;
		System.out.println(test);
        System.out.print("Test kodu skompilowanego: ");
        long start = System.nanoTime();
        while(test <= 100000) {
            test++;
        }
        long stop = System.nanoTime();
		
		float diff = (stop-start)/1000000f;
		
        System.out.println(diff+"ms");
		System.out.println(test);

        CodeBuilder cb = new CodeBuilder("twojaStara");
        cb.fireMethod("test", "SET", new String[]{"METHOD", "test", "GET"});

        CodeBuilder cb2 = new CodeBuilder("twojaStara");
        cb2.addWhile("test", "<=", "100000", cb.getInstructions());*/

		/*Node node = new Node(new Token(Contants.DIV, null));
		BinaryTree test = new BinaryTree(node);
		
		try
		{
			Node plus = node.add(new Token(Contants.PLUS, null));
			Node minus = node.add(new Token(Contants.MINUS, null));
			plus.add(new Token(2));
			plus.add(new Token(3));
			
			minus.add(new Token(10));
			minus.add(new Token(5));
		}
		catch (BinaryTreeInsertException e)
		{}
		
		test.traversePreOrder();
		
		ScriptInterpreter interpreter = new ScriptInterpreter();
		System.out.println(interpreter.evaluateCode(test));
		System.out.println((2+3)/(10-5));*/
		//System.out.println(cb2.generateJavaCode());
        /*ScriptInterpreter si = new ScriptInterpreter();
        si.addVariable("test", "Integer", "0");
		int test2 = ((IntegerAM) (si.getVariable("test")[1])).GET();
		
		System.out.println(test2);
        System.out.print("Test kodu interpretowanego: ");
        start = System.nanoTime();
        try {
            si.evaluateCode(cb2.getInstructions());
        } catch (InterpreterException e) {
            e.printStackTrace();
        }
        stop = System.nanoTime();
		
		float diff2 = (stop-start)/1000000f;
		
        System.out.println(diff2+"ms");
		test2 = ((IntegerAM) (si.getVariable("test")[1])).GET();
		System.out.println(test2);
		
		System.out.println("Różnica: "+(diff2-diff)+" ms ("+Math.round(diff2/diff*100)/100f+"x)");*/
    }
}
