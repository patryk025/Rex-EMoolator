package pl.cba.genszu.amcodetranslator.lexer.fixer;
import java.util.*;

public class CodePatches
{
	//list of located by me errors in codes e.x. missing parenthesis, typos in variable names
	private Map<String, String> patches = new HashMap<String, String>();
	
	public CodePatches() {
		patches.put("ANNOBJECT0^SETFRAME\"STATE0\",1)", "ANNOBJECT0^SETFRAME(\"STATE0\",1)");
		patches.put(")VARNIEREAGUJ^SET(1", "VARNIEREAGUJ^SET(1)");
	}
	
	public boolean hasPatch(String code) {
		return patches.containsKey(code);
	}
	
	public String patch(String code) {
		return patches.get(code);
	}
}
