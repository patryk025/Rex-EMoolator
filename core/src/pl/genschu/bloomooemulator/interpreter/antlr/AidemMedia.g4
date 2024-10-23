grammar AidemMedia;

// Parser rules
program: block ;
block: '{' statement* ';'* '}' ;
statement
    : ( functionCall
    | specialFunction
    | loopStatement
    | ifStatement
    | expression
    | inlineComment ) ';'*
    ;

functionCall
    : (variable | variableReference | structField) '^' functionName '(' paramList? ')'
    ;

specialFunction
    : '@' functionName '(' paramList? ')'
    ;

loopStatement
    : '@LOOP' '(' QUOTE (variable | block) QUOTE ',' expression ',' expression ',' expression ')'
    | '@FOR' '(' QUOTE variable QUOTE ',' QUOTE (variable | block) QUOTE ',' QUOTE expression QUOTE ',' QUOTE expression QUOTE ',' QUOTE expression QUOTE ')'
    | '@WHILE' '(' conditionSimple ',' QUOTE (variable | block) QUOTE ')'
    ;

ifStatement
    : '@IF' '(' ifCondition ',' trueBranch ',' falseBranch ')'
    ;

ifCondition
    : conditionSimple
    | conditionComplex
    ;

conditionSimple
    : QUOTE? expression QUOTE? ',' comparator ',' QUOTE? expression QUOTE?
    ;

conditionComplex
    : QUOTE complexTerm (logicOperator complexTerm)* QUOTE
    ;

complexTerm
    : expression comparator expression
    | '(' conditionComplex ')'
    ;

trueBranch
    : string
    | QUOTE block QUOTE
    ;

falseBranch
    : string
    | QUOTE block QUOTE
    ;

paramList: param (',' param)* ;
param: expression ;

expression
    : primitive
    | variable
    | variableReference
    | structField
    | '[' mathExpression ']'
    | functionCall
    ;

variableReference
    : '*' variable
    | '*' '[' mathExpression ']'
    ;

structField
    : variable ('|' structColumn)+
    ;

mathExpression
    : mathFactor (mathOperator mathFactor)*
    ;

mathOperator
    : '+' | '-' | '*' | '@' | '%'
    ;

mathFactor
    : (primitive | variable | variableReference | structField | functionCall)
    | '[' mathExpression ']'
    ;

primitive
    : string
    | number
    | BOOLEAN
    ;

number
    : INTEGER
    | FLOAT
    ;

variable: LITERAL ;
functionName: LITERAL ;
structColumn: LITERAL ;
inlineComment: '!' statement ;
comparator: string | QUOTE? (EQUALS | NOT_EQUALS | LESS | LESS_EQUALS | GREATER | GREATER_EQUALS) QUOTE? ;
logicOperator: AND | OR ;

string: STRING | ESCAPED_STRING ;

// Lexer rules
LITERAL: [A-Z_$][A-Z0-9_.-]* ;
INTEGER: '-'? [0-9]+ ;
FLOAT: '-'? [0-9]+ '.' [0-9]+ ;
BOOLEAN: 'TRUE' | 'FALSE' ;
QUOTE: '"' ;
STRING: '"' (~["'&|,<>{}])* '"' ;
AND: '&&' ;
OR: '||' ;
ESCAPED_STRING: '""' (~["'&|,<>{}])* '""' ; // it matches strings as parameters in loops and if
EQUALS: '\'' | '_' ;
NOT_EQUALS: '!\'' | '!_' ;
LESS: '<' ;
LESS_EQUALS: '<\'' | '<_' ;
GREATER: '>' ;
GREATER_EQUALS: '>\'' | '>_' ;
WS: [ \t\r\n]+ -> skip ;