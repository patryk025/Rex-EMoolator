package pl.cba.genszu.amcodetranslator;

import java.io.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import pl.cba.genszu.amcodetranslator.antlr.*;
import pl.cba.genszu.amcodetranslator.listener.*;
import pl.cba.genszu.amcodetranslator.objects.parser.*;
import pl.cba.genszu.amcodetranslator.utils.*;
import pl.cba.genszu.amcodetranslator.visitors.*;

public class Main
{
	private static Map<String, String> manualFixes;

    public static void main(String[] args)
	{
		manualFixes = new HashMap<>();
		manualFixes.put("{||SOBJ2^FIND(\"IMGM\")>-1){SOBJT^SET(SOBJ2);}", "@IF(\"SOBJ2^FIND(\"IMGM\")>-1\", \"{SOBJT^SET(SOBJ2);}\", \"\");");
		manualFixes.put("{]", "{}"); // nie chciało mi się pisać do tego logiki :D

		boolean debug = false;

		if (debug)
		{
			//String debugInstr = "{@IF(\"ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAL\")'TRUE||ANNREKSIO^ISPLAYING(\"DEZINTEGRACJAP\")'TRUE||ANNREKSIO^ISPLAYING(\"ZPLANSZY22\")'TRUE\",\"{@BREAK();}\",\"\"):IREXPOSX^SET(-1);IREXPOSY^SET(-1);@IF(\"ANNREKSIO^GETEVENTNAME()'\"L\"\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAL\");}\",\"{ANNREKSIO^PLAY(\"DEZINTEGRACJAP\");}\");BEHPLAYHENSFX^RUN();};";
			//String debugInstr = "{)VARNIEREAGUJ^SET(1}";
			//String debugInstr = "{BEHSELECTOBJS^RUN(\"POMOST_BOBROR\");SOBJECT|IPARAM0^SET(0);BEHUPDATEOBJECTS^RUN();VARSTEMP0^SET(BEHGETOBJECT^RUN(\"\"DZIELNICA3\"));*VARSTEMP0^HIDE();}";
			//String debugInstr = "{BEHPLAYSOUND^RUN(5;);ANNHEAD1^STOP(FALSE);ANNHEAD1^PLAY(\"SPI\");ANNWAND1^STOP(FALSE);ANNWAND1^PLAY(\"ZWISA\");STLMAGIC^STOP(FALSE);ARRTIMEELAPSED^CHANGEAT(1,0);}";
			//String debugInstr = "{)VARNIEREAGUJ^SET(1}";
			//String debugInstr = "{VARINTTEMP^ADD( UFO^RUN( [\"ANIMOLEBIODKA\" + $1 ], \"GETEVENTNAME\" );}";
			String debugInstr = "{VARSTEMP1^SET(THIS^GETNAME());VARITEMP1^SET(UFO^RUN(VARSTEMP1, \"GETPOSITIONX\");VARITEMP2^SET(UFO^RUN(VARSTEMP1, \"GETPOSITIONY\");VARSTEMP1^SET(THIS^GETNAME());CONDDETECT^CHECK( TRUE );}";

			CodeFixer.reset();
			if (CodeFixer.fixCode(debugInstr))
			{
				System.out.println("Yey");
			}
			else
			{
				System.out.println("Not yey");
			}
		}
		else
		{
			try
			{
				Scanner scanner = new Scanner(new File("/storage/emulated/0/AppProjects/AidemMediaInterpreterAntlr/src/pl/cba/genszu/amcodetranslator/errors.txt"));

				int codes = 0;
				int manualFixed = 0;
				int fixed = 0;
				
				while (scanner.hasNextLine())
				{
					String line = scanner.nextLine();

					codes++;
					
					if (manualFixes.containsKey(line))
					{
						System.out.println("Line " + line + " has manual fix, because cannot be fixed automatically");
						line = manualFixes.get(line);
						manualFixed++;
						continue;
					}

					CodeFixer.reset();
					if (CodeFixer.fixCode(line))
					{
						System.out.println("Yey");
						fixed++;
					}
					else
					{
						System.out.println("Not yey");
						break;
					}
				}
				
				System.out.println("Checked lines: " + codes);
				System.out.println("Manual fixed: " + manualFixed);
				System.out.println("Fixed: " + fixed);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		//System.out.println(DebugStringFinder.find("!!!24LASER^RUN(\"KAMIENBADANYCZYPOW\",\"SET\",KAMIENBADANYSKLAD);"));
	}
}
