package pl.cba.genszu.amcodetranslator.lexer.util;

public class LexerToken
{
	public String type;
	public String value;

	public LexerToken(String type) {
		this.type = type;
		this.value = null;
	}

	public LexerToken(String type, String value) {
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "Token(" + type + ", " + value + ")";
	}
}
