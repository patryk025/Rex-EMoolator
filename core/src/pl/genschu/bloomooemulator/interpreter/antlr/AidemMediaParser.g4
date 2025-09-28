parser grammar AidemMediaParser;
options { tokenVocab = AidemMediaLexer; }

// entry
script : LBRACE (statement (SEMI | RBRACE | EOF))* (RBRACE | EOF) ;

statement
  : specialCall
  | methodCall
  | expr
  ;

// @NAME(arg1, arg2, ...)
specialCall
  : AT name=IDENT LPAREN argListOpt RPAREN
  ;

// obj^method(arg1,...)
methodCall
  : (objectName | objectReference) CARET method=IDENT LPAREN argListOpt RPAREN
  ;

objectName : name=IDENT ( PIPE field=IDENT )? ; // variable or struct

objectReference : STAR primary ; // object reference not set to an instance of an object

argListOpt : (arg (COMMA arg)*)? ;

arg
  : expr
  | missing_quote=MISSING_CLOSE_QUOTE // unclosed string arguments, catch that
  ;

// math
expr        : addExpr ;

addExpr     : left=mulExpr (op=(PLUS|MINUS) right=mulExpr)* ;
mulExpr     : left=unaryExpr (op=(STAR|AT|PERC) right=unaryExpr)* ;

unaryExpr   : op=(PLUS|MINUS|STAR) unaryExpr
            | primary;

primary     : NUMBER
            | TRUE
            | FALSE
            | STRING
            | CODE_BLOCK
            | methodCall
            | objectName
            | LBRACK expr RBRACK ;