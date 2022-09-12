package pl.cba.genszu.amcodetranslator.lexer;
import java.util.*;
import pl.cba.genszu.amcodetranslator.lexer.util.*;

public class Lexer
{
	//TODO: przerobić parsowanie kodu na lekser i parser
	//TODO: opracować reguły
	
	public List<LexerToken> analyze(String code) {
		List<LexerToken> tokens = new ArrayList<>();
		
		String[] characters = code.split("");
		
		boolean isFunc = false;
		boolean isStruct = false;
		boolean isString = false;
		boolean isExpression = false;
		boolean isParam = false;
		boolean isFloat = false;
		
		StringBuilder buffer = new StringBuilder();
		
		for(String character : characters) {
			
		}
		
		return tokens;
	}
}
