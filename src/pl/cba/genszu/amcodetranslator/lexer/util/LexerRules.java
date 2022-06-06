package pl.cba.genszu.amcodetranslator.lexer.util;
import java.util.*;

public class LexerRules
{
	//takie tam próby, do wywalenia
	
	private HashMap<String, String> rules;
	
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
		rules = new HashMap<String, String>();
		rules.put("INT", "[0-9]*");
		rules.put("DIGIT", "[0-9]{1}");
		rules.put("FLOAT", "[0-9]*\\.[0-9]*");
	}
	
	public LexerRules getInstance() {
		if(instance == null) instance = new LexerRules();
		return instance;
	}
}
