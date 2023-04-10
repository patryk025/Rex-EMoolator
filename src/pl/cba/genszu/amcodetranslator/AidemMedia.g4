grammar AidemMedia;

ifInstr	
	: '@IF' LPAREN (
			QUOTEMARK condition QUOTEMARK SEPARATOR
		|	param SEPARATOR QUOTEMARK compare QUOTEMARK SEPARATOR param SEPARATOR
	)	(codeBlock | string) SEPARATOR (codeBlock | string) RPAREN ENDINSTR*
	;

loopInstr
	:	'@LOOP' LPAREN codeBlock SEPARATOR (string | literal | expression | arithmetic? number | functionFire | struct) SEPARATOR (string | literal | expression | arithmetic? number | functionFire | struct) SEPARATOR (string | literal | expression | arithmetic? number | functionFire | struct) RPAREN ENDINSTR*
	;

whileInstr
	:	'@WHILE' LPAREN param SEPARATOR QUOTEMARK compare QUOTEMARK SEPARATOR param SEPARATOR (codeBlock | string) RPAREN ENDINSTR*
	;

functionFire
	:	(literal | iterator | stringRef | struct | variable) FIREFUNC literal LPAREN (SEPARATOR? param)* RPAREN ENDINSTR*
	;

codeBlock
	:	STARTCODE (functionFire | ifInstr | loopInstr | whileInstr | instr | behFire | expression | codeBlock | (literal | floatNumber | number) | comment ENDINSTR*)* ENDINSTR* STOPCODE
	;

COMMENT
	:	'!' .*? ENDINSTR*
	;

comment
	:	COMMENT
	;

expression
	:	STARTEXPR ((arithmetic | DIV | STRREF)? (literal | string | arithmetic? number | arithmetic? floatNumber | modulo | iterator | functionFire | expression | struct | stringRef | variable) (arithmetic | STRREF)?)* STOPEXPR ENDINSTR*
	;

script
	:	(codeBlock | expression | string | ifInstr | loopInstr | whileInstr | string)*
	;

param
	:	functionFire | BOOLEAN | variable | string | literal | arithmetic? number | arithmetic? floatNumber | expression | iterator | struct | stringRef
	;

condition
	:	(logic? conditionPart)*
	;

conditionPart
	:	(literal | functionFire | struct | expression | iterator) compare param
	;

behFire
	:	literal ENDINSTR
	;

modulo
	:	(literal | iterator | arithmetic? number | arithmetic? floatNumber | functionFire | struct | expression) MOD (literal | arithmetic? number | arithmetic? floatNumber | functionFire | struct | expression)
	;

iterator
	:	ITERATOR
	;

/* jednak to nie zbyt dobrze działa */
/*string
	:	QUOTEMARK (string | condition)* .*? QUOTEMARK
	;
*/

/* TODO: string to powinien być dowolny ciąg znaków */
string
	:	QUOTEMARK ((literal | FIREFUNC | arithmetic? number | arithmetic? floatNumber | compare | SLASH | struct | LPAREN | RPAREN | SEPARATOR | arithmetic | VARREF | iterator | expression | functionFire)+ | (variable (SLASH literal?)?) | string)? QUOTEMARK
	;

instr
	:	'@' literal LPAREN (param (SEPARATOR param)?)? RPAREN ENDINSTR
	;

stringRef /* łapie też *ZMIENNA, gdzie zmienna to liczba */
	:	STRREF (expression | literal)
	;

struct
	:	literal STRUCTFIELD literal
	;

variable
	:   VARREF (literal | number)
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

number
	:	NUMBER
	;

floatNumber
	: FLOAT
	;

literal
	:	LITERAL
    ;

arithmetic
	:	ARITHMETIC
	;

logic
	:	LOGIC
	;

compare
	:	(LSS | LEQ | GEQ | GTR| EQU | NEQ)
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
	   (ARITHMETIC (LITERAL | NUMBER) | VARREF (LITERAL | NUMBER))?
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
