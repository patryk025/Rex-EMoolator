package pl.cba.genszu.amcodetranslator.lexer.util;
import java.util.*;

public class LexerRules
{
	//TODO: co ja robię ze swoim życiem?
	private HashMap<String, String> rules;
	private HashMap<String, String> tokens;
	
	private LexerRules instance;
	
/*
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        )
    ;

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    ;

CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    ;

LPAREN	:	'('
	;

RPAREN	:	')'
	;

STARTCODE
	:	'{'
	;

STOPCODE:	'}'
	;

STARTEXPR
	:	'['
	;

STOPEXPR:	']'
	;

INSTRNAME
	:	'@' (  ~('\\'|'"') )*
	;

ITERATOR:	'_I_'
	;

LSS	:	'<'
	;

LEQ	:	'<' ( '\'' | '_' )
	;

GEQ	:	'>' ( '\'' | '_' )
	;

GTR	:	'>'
	;

EQU	:	'\'' | '_'
	;

NEQ	:	'!' ( '\'' | '_' )
	;

ENDINSTR:	';'
	;

FIREFUNC:	'^'
	;*/
	
	private LexerRules() {
		tokens = new HashMap<String, String>();
		tokens.put("DIGIT", "[0-9]");
		tokens.put("CHAR", "[a-zA-Z]");
		tokens.put("LPAREN", "\\(");
		tokens.put("RPAREN", "\\)");
		tokens.put("STARTCODE", "\\{");
		tokens.put("STOPCODE", "\\}");
		tokens.put("STARTEXPR", "\\[");
		tokens.put("STOPEXPR", "\\]");
		tokens.put("WS", "[ \\t\\r\\n]");
		
		rules = new HashMap<String, String>();
		rules.put("FLOAT", "~DIGIT~*\\.~DIGIT~*");
		rules.put("INT", "~DIGIT~*");
		rules.put("STRING", "\"~CHAR~*\"");
		rules.put("INSTRNAME", "@~CHAR~");
		rules.put("CODE", "~STARTCODE~~CHAR~*~STOPCODE~");
	}
	
	public LexerRules getInstance() {
		if(instance == null) instance = new LexerRules();
		return instance;
	}
}
