package pl.cba.genszu.amcodetranslator;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SimpleCodeAnalyzer {
    public static HashMap<String, Set<String>> analyzeCode(String code, HashMap<String, HashMap<String, String>> variables) {
        code = code.substring(1, code.length() - 1);

        String[] instructions = code.split(";");
        for(String instr : instructions) {
            if(instr.contains("^")) {
                instr = instr.substring(0, instr.length() - 1);
                //funkcja
                String[] parts = instr.split("\\^");
                String[] params = parts[1].split("\\(");

            }
        }
        return null;
    }
}
