package pl.cba.genszu.amcodetranslator.lexer;

import java.util.*;

public class StringUtils
{
	public static String[] selectiveSplit(String text, char splitChar) {
		return selectiveSplit(text, splitChar, '(', ')');
	}

	public static String[] selectiveSplit(String text, char splitChar, char intendCharMarker, char intendCharMarker2) {
		List<String> linesArr = new ArrayList<>();
		linesArr.add("");
		int stringNo = 0;
		int intentNo = 0;
		for(int i = 0; i < text.length(); i++) {
			if(text.charAt(i) == intendCharMarker)
				intentNo++;
			if (text.charAt(i) == intendCharMarker2) {
				intentNo--;
			}
			if(text.charAt(i) == splitChar && intentNo == 0) {
				stringNo++;
				if(i < text.length() - 1)
					linesArr.add("");
			}
			else {
				linesArr.set(stringNo, linesArr.get(stringNo)+text.charAt(i));
			}
		}
		return linesArr.toArray(new String[0]);
	}
	
	public static String[] splitInstrToLines(String code) {
		return selectiveSplit(code, ';', '{', '}');
	}

	public static String[] singleSplit(String text, String splitChar) {
		String[] parts = new String[2];
		for(int i = 0; i < text.length(); i++) {
			if(text.charAt(i) == splitChar.charAt(0)) {
				if(i != 0)
					parts[0] = text.substring(0, i);

				parts[1] = text.substring(i+1);
				return parts;
			}
		}
		return null;
	}
	
	public static String cutHeadAndTail(String string) {
		if(string.equals("\"\"")) { //no jak puste to puste
			return "";
		}
		else {
			return string.substring(1, string.length()-1);
		}
		
	}
}
