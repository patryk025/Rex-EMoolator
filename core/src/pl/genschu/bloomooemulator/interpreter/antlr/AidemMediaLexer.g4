lexer grammar AidemMediaLexer;

// --- keywords / literals ---
TRUE  : 'TRUE';
FALSE : 'FALSE';

NUMBER: [0-9]+ ('.' [0-9]+)?;
STRING: '"' ( BALANCED_CONTENT | ~[,)+] )* '"' ;

fragment BALANCED_CONTENT // dirty fix for function calls in strings
  : '(' ( BALANCED_CONTENT | ~[)] )* ')'     // nested ()
  ;

CODE_BLOCK: '"{' ( ~[}] | '{' ~[}]* '}' )* '}"' ; // separate token for easier parsing (max 2 level depth)

// --- punctuation / operators ---
AT    : '@';
CARET : '^';
LPAREN: '(';
RPAREN: ')';
LBRACK: '[';
RBRACK: ']';
COMMA : ',';
SEMI  : ';';
STAR  : '*';
PLUS  : '+';
MINUS : '-';
PERC  : '%';
PIPE  : '|';
LBRACE: '{';
RBRACE: '}';
MISSING_CLOSE_QUOTE: '"' ~[",)]+ ;

// variable name/method
IDENT : [A-Za-z0-9_$][A-Za-z0-9_$./?-]* ;

// inline comment
LINE_COMMENT : '!' ~[;]* -> skip;

// whitespace
WS : [ \t\r\n]+ -> skip;

