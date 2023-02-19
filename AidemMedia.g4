grammar AidemMedia;

ifInstr	
	: '@IF' LPAREN (
			QUOTEMARK condition QUOTEMARK SEPARATOR
		|	param SEPARATOR QUOTEMARK (LSS | LEQ | GEQ | GTR| EQU | NEQ) QUOTEMARK SEPARATOR param SEPARATOR
	)	(codeBlock | string) SEPARATOR (codeBlock | string) RPAREN ENDINSTR*
	;

loopInstr
	:	'@LOOP' LPAREN codeBlock SEPARATOR (string | LITERAL | expression | ARITHMETIC? NUMBER | functionFire | struct) SEPARATOR (string | LITERAL | expression | ARITHMETIC? NUMBER | functionFire | struct) SEPARATOR (string | LITERAL | expression | ARITHMETIC? NUMBER | functionFire | struct) RPAREN ENDINSTR*
	;

whileInstr
	:	'@WHILE' LPAREN param SEPARATOR QUOTEMARK (LSS | LEQ | GEQ | GTR| EQU | NEQ) QUOTEMARK SEPARATOR param SEPARATOR (codeBlock | string) RPAREN ENDINSTR*
	;

functionFire
	:	(LITERAL | ITERATOR | stringRef | struct | variable) FIREFUNC LITERAL LPAREN (SEPARATOR? param)* RPAREN ENDINSTR*
	;

codeBlock
	:	STARTCODE (functionFire | ifInstr | loopInstr | whileInstr | instr | behFire | expression | codeBlock | (LITERAL | FLOAT | NUMBER) ENDINSTR*)* ENDINSTR* STOPCODE
	;

expression
	:	STARTEXPR ((ARITHMETIC | DIV | STRREF)? (LITERAL | string | ARITHMETIC? NUMBER | ARITHMETIC? FLOAT | modulo | ITERATOR | functionFire | expression | struct | stringRef | variable) (ARITHMETIC | STRREF)?)* STOPEXPR ENDINSTR*
	;

script
	:	(codeBlock | expression | string | ifInstr | loopInstr | whileInstr | string)*
	;

param
	:	functionFire | BOOLEAN | variable | string | LITERAL | ARITHMETIC? NUMBER | ARITHMETIC? FLOAT | expression | ITERATOR | struct | stringRef
	;

condition
	:	(LOGIC? conditionPart)*
	;

conditionPart
	:	(LITERAL | functionFire | struct | expression | ITERATOR) (LSS | LEQ | GEQ | GTR| EQU | NEQ) param
	;

behFire
	:	LITERAL ENDINSTR
	;

modulo
	:	(LITERAL | ITERATOR | ARITHMETIC? NUMBER | ARITHMETIC? FLOAT | functionFire | struct | expression) MOD (LITERAL | ARITHMETIC? NUMBER | ARITHMETIC? FLOAT | functionFire | struct | expression)
	;

/* jednak to nie zbyt dobrze działa */
/*string
	:	QUOTEMARK (string | condition)* .*? QUOTEMARK
	;
*/

/* TODO: string to powinien być dowolny ciąg znaków */
string
	:	QUOTEMARK ((LITERAL | FIREFUNC | ARITHMETIC? NUMBER | ARITHMETIC? FLOAT | LSS | LEQ | GEQ | GTR| EQU | NEQ | SLASH | struct | LPAREN | RPAREN | SEPARATOR | ARITHMETIC | VARREF | ITERATOR | expression | functionFire)+ | (variable (SLASH LITERAL?)?) | string)? QUOTEMARK
	;

instr
	:	'@' LITERAL LPAREN (param (SEPARATOR param)?)? RPAREN ENDINSTR
	;

stringRef /* łapie też *ZMIENNA, gdzie zmienna to liczba */
	:	STRREF (expression | LITERAL)
	;

struct
	:	LITERAL STRUCTFIELD LITERAL
	;

variable
	:   VARREF (LITERAL | NUMBER)
	;

LPAREN	:	'('
	;

RPAREN	:	')'
	;

VARREF
	:	'$'
	;

STARTCODE
	:	QUOTEMARK? '{'
	;

STOPCODE
	:	'}' QUOTEMARK?
	;

STARTEXPR
	:	'['
	;

STOPEXPR:	']'
	;

SLASH
	:	'\\'
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
	;

DIV
	:	'@'
	;

MOD
	:	'%'
	;

fragment DIGIT
	:	'0'..'9'
	;

fragment LETTER
	:	'A'..'Z' | 'a'..'z'
	;

NUMBER
	:	DIGIT+
	;

FLOAT
    :   NUMBER '.' NUMBER
   	;

DOT: '.';

ITERATOR
	:	'_I_'
	;

LITERAL: ((('_' | DOT | SLASH)* (LETTER | NUMBER) ('_' | LETTER | DIGIT | DOT | SLASH | '!' | '?')*)
       | ('_'* DOT DIGIT+ ('.' DIGIT+)? (LETTER ('_' | DIGIT | SLASH | '!' | '?')* | '_'*)))
	   (ARITHMETIC NUMBER | VARREF (LITERAL | NUMBER))?
       ;

ARITHMETIC
	:	'+' | '-' | '*' ~('[' | 'A'..'Z' | '0'..'9' | '$') | DIV
	;

LOGIC
	:	'&&' | '||'
	;

BOOLEAN
	:	'TRUE' | 'FALSE'
	;

SEPARATOR
	:	','
	;

STRUCTFIELD
	:	'|'
	;

QUOTEMARK
	:	'"'
	;	

STRREF
	:	'*'
	;

WS  	
	:   ( ' '
	| '\t'
	| '\r'
	| '\n'
	) -> channel(HIDDEN)
	;

CHAR	
	:  '\'' ~('\''|'\\') '\''
	;
