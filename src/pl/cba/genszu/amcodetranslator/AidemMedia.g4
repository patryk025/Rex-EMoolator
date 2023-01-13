grammar AidemMedia;

ifInstr	
	: INSTRTYPE 'IF' LPAREN (
			(QUOTEMARK condition QUOTEMARK | param) SEPARATOR (param SEPARATOR param SEPARATOR)?
	)	(codeBlock | string) SEPARATOR (codeBlock | string) RPAREN ENDINSTR?
	;

loopInstr
	:	INSTRTYPE 'LOOP' LPAREN codeBlock SEPARATOR string SEPARATOR string SEPARATOR string SEPARATOR RPAREN ENDINSTR?
	;

whileInstr
	:	INSTRTYPE 'WHILE' LPAREN string SEPARATOR string SEPARATOR string SEPARATOR RPAREN ENDINSTR?
	;
	
functionFire
	:	LITERAL FIREFUNC LITERAL LPAREN param* RPAREN ENDINSTR?
	;
	
codeBlock
	:	STARTCODE (functionFire | ifInstr | loopInstr | whileInstr)* STOPCODE
	;
	
expression
	:	STARTEXPR string STOPEXPR
	;

script
	:	(codeBlock | expression | string | ifInstr)*
	;
	
param
	:	functionFire | BOOLEAN | ARGINDEX | string | LITERAL | NUMBER
	;

condition
	:	(LITERAL | functionFire) (LSS | LEQ | GEQ | GTR| EQU | NEQ) param
		(LOGIC condition)?
	;

string
	:	QUOTEMARK LITERAL QUOTEMARK
	;
	
LPAREN	:	'('
	;

RPAREN	:	')'
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

INSTRTYPE
	:	'@'
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
	
NUMBER	:	DIGIT+
	;
    
ITERATOR
	:	'_I_'
	;

SPACE
	:	' '
	;
	
LITERAL
	:	('A'..'Z' | '0'..'9' | '_')+
	;

FLOAT
    :   NUMBER+ '.' NUMBER* 
    |   '.' NUMBER+
   	;

ARITHMETIC
	:	'+' | '-' | '*' | '/'
	;

LOGIC
	:	'&&' | '||'
	;

BOOLEAN
	:	'TRUE' | 'FALSE'
	;

ARGINDEX
	:	'$' NUMBER+
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
