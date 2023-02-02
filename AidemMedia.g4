grammar AidemMedia;

ifInstr	
	: '@IF' LPAREN (
			QUOTEMARK condition QUOTEMARK SEPARATOR
		|	param SEPARATOR QUOTEMARK (LSS | LEQ | GEQ | GTR| EQU | NEQ) QUOTEMARK SEPARATOR param SEPARATOR
	)	(codeBlock | string) SEPARATOR (codeBlock | string) RPAREN ENDINSTR?
	;

loopInstr
	:	'@LOOP' LPAREN codeBlock SEPARATOR (string | LITERAL | expression | DIGIT | NUMBER | functionFire) SEPARATOR (string | LITERAL | expression | DIGIT | NUMBER | functionFire) SEPARATOR (string | LITERAL | expression | DIGIT | NUMBER | functionFire) RPAREN ENDINSTR?
	;

whileInstr
	:	'@WHILE' LPAREN param SEPARATOR QUOTEMARK (LSS | LEQ | GEQ | GTR| EQU | NEQ) QUOTEMARK SEPARATOR param SEPARATOR (codeBlock | string) RPAREN ENDINSTR?
	;
	
functionFire
	:	(LITERAL | stringRef | struct | SELF) FIREFUNC LITERAL LPAREN (SEPARATOR? param)* RPAREN ENDINSTR?
	;
	
codeBlock
	:	STARTCODE (functionFire | ifInstr | loopInstr | whileInstr | instr)* STOPCODE
	;
	
expression
	:	STARTEXPR (ARITHMETIC? (LITERAL | string | DIGIT | NUMBER | ITERATOR | functionFire | expression | struct | stringRef))* STOPEXPR
	;

script
	:	(codeBlock | expression | string | ifInstr | loopInstr | whileInstr)*
	;
	
param
	:	functionFire | BOOLEAN | variable | string | LITERAL | DIGIT | NUMBER | expression | ITERATOR | struct | stringRef
	;

condition
	:	(LITERAL | functionFire | struct) (LSS | LEQ | GEQ | GTR| EQU | NEQ) param
		(LOGIC condition)?
	;

/* TODO: string to powinien być dowolny ciąg znaków */
string
	:	QUOTEMARK ((LITERAL | DIGIT | NUMBER | LSS | LEQ | GEQ | GTR| EQU | NEQ | SLASH | struct | LPAREN | RPAREN | SEPARATOR | ARITHMETIC)+ | (variable (SLASH LITERAL?)?) | string)? QUOTEMARK
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
	:   VARREF (LITERAL | DIGIT)
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

DIGIT	:	'0'..'9'
	;
	
NUMBER	
	:	ARITHMETIC? DIGIT+
	;
    
ITERATOR
	:	'_I_'
	;
	
LITERAL
	:	('.'? ('A'..'Z' | '0'..'9' | '_')+)+
	;

FLOAT
    :   NUMBER+ '.' NUMBER* 
    |   '.' NUMBER+
   	;

ARITHMETIC
	:	'+' | '-' | '*' ~('[' | 'A'..'Z') | '/' | '@'
	;

LOGIC
	:	'&&' | '||'
	;

BOOLEAN
	:	'TRUE' | 'FALSE'
	;

SELF
	:	'THIS'
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
