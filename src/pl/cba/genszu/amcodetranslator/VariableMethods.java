package pl.cba.genszu.amcodetranslator;

import java.io.*;
import java.util.*;
import pl.cba.genszu.amcodetranslator.utils.*;

public class VariableMethods {

    public static void search(final String pattern, final File folder, List<String> result, boolean recurse) {
        for (final File f : folder.listFiles()) {

            if (f.isDirectory() && recurse) {
                search(pattern, f, result, true);
            }

            if (f.isFile()) {
                if (f.getName().matches(pattern)) {
                    
                    if(result != null)
                        result.add(f.getAbsolutePath());
                }
            }
        }
    }

    public static void main(String[] args) {
        List<String> pliki = new ArrayList<>();

        search(".*\\.cnv|.*\\.def|.*\\.seq|.*\\.CNV|.*\\.DEF|.*\\.SEQ", new File("D:\\udostepniane\\skrypty"), pliki, true);
        
		CNVParserDebug cp = new CNVParserDebug();
		String tmp = null;
		
		Logger.setVerbosity(0);
		
        for (String e : pliki) {
            Logger.log(e);
            
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
    }
}
