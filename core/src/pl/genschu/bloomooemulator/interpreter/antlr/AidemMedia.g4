grammar AidemMedia;

ifInstr	
	: '@IF' LPAREN (
			QUOTEMARK condition QUOTEMARK SEPARATOR
		|	conditionSimple SEPARATOR
	)	ifTrue SEPARATOR ifFalse RPAREN ENDINSTR*
	;

loopInstr
	:	'@LOOP' LPAREN loopCodeParam SEPARATOR param SEPARATOR param SEPARATOR param RPAREN ENDINSTR*
	;

whileInstr
	:	'@WHILE' LPAREN param SEPARATOR QUOTEMARK compare QUOTEMARK SEPARATOR param SEPARATOR (codeBlock | string) RPAREN ENDINSTR*
	;

functionFire
	:	(literal | iterator | stringRef | struct | variable | varWithNumber) FIREFUNC literal LPAREN (SEPARATOR? param)* RPAREN ENDINSTR*
	;

codeBlock
	:	STARTCODE (functionFire | ifInstr | loopInstr | whileInstr | instr | behFire | expression | codeBlock | (literal | floatNumber | number) | comment ENDINSTR*)* ENDINSTR* STOPCODE
	;

varWithNumber
	:	literal arithmetic number
	;

loopCodeParam
	:	(codeBlock | string | literal)
	;

conditionSimple
	:	param SEPARATOR QUOTEMARK compare QUOTEMARK SEPARATOR param
	;

ifTrue
	:	(codeBlock | string)
	;

ifFalse
	:	(codeBlock | string)
	;

COMMENT
	:	'!' ~('_' | '\'') .*? ENDINSTR
	;

comment
	:	COMMENT
	;

expression
	:	STARTEXPR (arithmetic? (literal | string | arithmetic? number | arithmetic? floatNumber | modulo | iterator | functionFire | expression | struct | stringRef | variable) (arithmetic | STRREF)?)* STOPEXPR ENDINSTR*
	;

script
	:	(codeBlock | expression | string | ifInstr | loopInstr | whileInstr | string)*
	;

param
	:	functionFire | bool | variable | string | literal | arithmetic? number | arithmetic? floatNumber | expression | iterator | struct | stringRef
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

string
	:	QUOTEMARK ((literal | arithmetic? (number | floatNumber) | compare | SLASH | struct | LPAREN | RPAREN | arithmetic | VARREF | iterator | expression | functionFire)+ | (variable (SLASH literal?)?) | string | bool)? QUOTEMARK
	;

instr
	:	'@' literal LPAREN (param (SEPARATOR param)?)? RPAREN ENDINSTR
	;

stringRef
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

ADD
    :   '+'
    ;

SUBTRACT
    :   '-'
    ;

MULT
    :   '*' ~('[' | 'A'..'Z' | '0'..'9' | '$' | '(')
    ;

DIV
    :   '@'
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
	:	variable? LITERAL variable?
    ;

arithmetic
	:	(ADD | SUBTRACT | MULT | DIV | MOD | LPAREN | RPAREN)
	;

logic
	:	LOGIC
	;

compare
	:	(LSS | LEQ | GEQ | GTR| EQU | NEQ)
	;

bool
	:  BOOLEAN
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

BOOLEAN
	:	'TRUE' | 'FALSE'
	;

LITERAL: {!(getText().toUpperCase().startsWith("TRUE") || getText().toUpperCase().startsWith("FALSE"))}?
		((('_' | DOT | SLASH)* (LETTER | DIGIT | '_')) ('_' | LETTER | DIGIT | DOT | SLASH | '?')*)
		| ('_'* DOT DIGIT+ ('.' DIGIT+)? (LETTER ('_' | DIGIT | SLASH | '?')* | '_'*))
		((ADD | SUBTRACT | MULT | DIV | MOD) | NUMBER | VARREF)?
		;

/*
ARITHMETIC
	:	ADD | SUBTRACT | MULT | DIV
	;
*/

LOGIC
	:	'&&' | '||'
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
