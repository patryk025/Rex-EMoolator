package pl.cba.genszu.amcodetranslator.lexer;

public class LexerUtils
{
	public static boolean isMethodCall(String code) {
		if(code != null) {
			String[] parts = code.split("\\(");
			int lvl = 0;
			String[] letters = parts[0].split("");
			for(String letter : letters) {
				if(letter.equals("\"")) {
					if(lvl == 0) lvl = 1;
					else lvl = 0;
				}
				if(letter.equals("[")) lvl++;
				if(letter.equals("]")) lvl--;
				
				if(letter.equals("^") && lvl == 0) return true;
			}
			return false;
			//return parts[0].contains("^");
		}
		else return false;
	}

	public static boolean isExpression(String code) {
		return (code.startsWith("[") && code.endsWith("]"));
	}

	public static boolean hasSemicolon(String code) {
		return code.endsWith(";");
	}

	public static boolean isInQuote(String code) {
		return code.startsWith("\"") && code.endsWith("\"");
	}

	public static boolean isCode(String code) {
		return (code.startsWith("{") && code.endsWith("}"));
	}

	public static boolean isPointerByString(String code) {
		return code.startsWith("*[") && code.contains("]");
	}

	public static String extractExpression(String code) {
		if(isInQuote(code)) code = StringUtils.cutHeadAndTail(code);
		if(hasSemicolon(code)) code = code.substring(0, code.length()-1);
		if(isExpression(code) || isCode(code))
			return StringUtils.cutHeadAndTail(code);
		return code;
	}

	public static boolean isArithmetic(String code) {
		if(code != null) {
			int lvl = 0;
			String[] letters = code.split("");
			for(String letter : letters) {
				if(letter.equals("\"")) {
					if(lvl == 0) lvl = 1;
					else lvl = 0;
				}
				if(letter.equals("(")) lvl++;
				if(letter.equals(")")) lvl--;

				if(letter.matches("[\\+\\-\\*\\@\\%]") && lvl == 0) return true;
			}
			return false;
		}
		else return false;
		
		
		/*String[] parts = code.split("\\(");
		if(
			parts[0].contains("+") ||
			parts[0].contains("-") ||
			parts[0].contains("*") ||
			(parts[0].contains("@") && !parts[0].startsWith("@")) ||
			parts[0].contains("%")
			) return true;
		else return false;*/
	}

	public static boolean isVarDef(String code) {
		return 
			code.startsWith("@BOOL") 
			|| code.startsWith("@INT")
			|| code.startsWith("@DOUBLE")
			|| code.startsWith("@STRING");
	}
	
	public static boolean isStructFieldExpr(String code) {
		return 
			code.split("\\|").length == 2;
	}
}
