package pl.cba.genszu.amcodetranslator.utils;

import java.io.*;
import java.util.*;
import pl.cba.genszu.amcodetranslator.encoding.*;

public class DebugStringFinder
{
	private static void search(final String pattern, final File folder, List<String> result, boolean recurse)
	{
        for (final File f : folder.listFiles())
		{

            if (f.isDirectory() && recurse)
			{
                search(pattern, f, result, true);
            }

            if (f.isFile())
			{
                if (f.getName().matches(pattern))
				{
                    if (result != null)
                        result.add(f.getAbsolutePath());
                }
            }
        }
    }

	public static String find(String toFind)
	{
		List<String> pliki = new ArrayList<>();

        search(".*\\.cnv|.*\\.def|.*\\.seq|.*\\.CNV|.*\\.DEF|.*\\.SEQ", new File("/sdcard/skrypty"), pliki, true);

		for (String plik : pliki)
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
							if (tmpParam[0].toUpperCase().equals("D")) offset *= -1;
							decypher = true;
						}
						else
						{
							content = content.append(line).append("\n");
						}
					}
					if (decypher)
					{
						System.out.println("Decyphering " + new File(plik).getName() + "...");
						if (ScriptDecypher.decode(content.toString(), offset).contains(toFind))
						{
							return plik;
						}
					}
					else
					{
						if (content.toString().contains(toFind))
						{
							return plik;
						}
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
		return null;
	}
}
