package pl.cba.genszu.amcodetranslator;

import java.io.*;
import pl.cba.genszu.amcodetranslator.encoding.*;

public class StrangeLineDebug
{
	public static void main() {
		
		//Próba zdebugowania linii z pliku Dane/Intro/MainMenu/atakpiratow/statek1/CUTSTATEK1.cnv
		//Reksio i skarb piratów
		//wynikowo daje YKWKH-@P{MI8YKWKH
		
		try
        {
            FileReader reader = new FileReader("/sdcard/skrypty/risp/Dane/Intro/MainMenu/atakpiratow/statek1/CUTSTATEK1.cnv");
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
				for(int i = -32; i <= 64; i++) {
					String testString = ScriptDecypher.decode(content.toString(), i).trim();
					System.out.println("Offset " + i + ": \"" + testString.substring(testString.length()-17) + "\"");
					//break;
				}
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
}
