package pl.cba.genszu.amcodetranslator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import pl.cba.genszu.amcodetranslator.AMObjects.IntegerAM;
import pl.cba.genszu.amcodetranslator.interpreter.InterpreterException;
import pl.cba.genszu.amcodetranslator.interpreter.ScriptInterpreter;
import java.io.FileNotFoundException;
import pl.cba.genszu.amcodetranslator.interpreter.util.Token;
import pl.cba.genszu.amcodetranslator.lexer.tree.BinaryTree;
import pl.cba.genszu.amcodetranslator.lexer.tree.Node;
import pl.cba.genszu.amcodetranslator.lexer.tree.exception.BinaryTreeInsertException;
import pl.cba.genszu.amcodetranslator.lexer.Constants;
import pl.cba.genszu.amcodetranslator.lexer.Lexer;

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

    /*TODO: @GETCURRENTSCENE()*/

    public static void main(String[] args) {
        List<String> pliki = new ArrayList<>();

        search(".*\\.cnv", new File("D:\\Re-SP-master\\Dane"), pliki, true);
        //HashSet<String> fun = new HashSet<>();
        //List<ParseObjectTmp> lista = new ArrayList<>();
        //CodeTranslatorFile ct = new CodeTranslatorFile();
		CNVParser cp = new CNVParser();
		String tmp = null;

        /*try {
            cp.parseFile(new File("D:\\Re-SP-master\\Dane\\Intro\\MainMenu\\13Palisada\\PALISADA13.cnv"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }*/

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
		//System.out.println(tmp); // /sdcard/Re-SP-master/Dane/Intro/MainMenu/13Palisada/PALISADA13.cnv
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
