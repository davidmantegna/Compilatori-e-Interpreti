grammar FOOL;

@lexer::members {
   //there is a much better way to do this, check the ANTLR guide
   //I will leave it like this for now just becasue it is quick
   //but it doesn't work well
   public int lexicalErrors=0;
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/
  
prog   : exp SEMIC              #singleExp
       | let in SEMIC           #letInExp
       ;

let    : LET (dec SEMIC)+;

in     : IN ((exp SEMIC)| stms)+ ;

letnest: LET (varasm SEMIC)+;

vardec : type ID ;

varasm : vardec ASM exp ;

fun    : type ID LPAR ( vardec ( COMMA vardec)* )? RPAR ((letnest in SEMIC)? |((exp SEMIC)| stms )) ;

dec    : varasm           #varAssignment
       | fun              #funDeclaration
       ;

type   : INT
       | BOOL
       ;
    
exp    : left=term (operator=(PLUS | MINUS) right=exp)?
       ;
   
term   : left=factor (operator=(TIMES | DIV) right=term)?
       ;
   
factor : left=value (operator=(AND | OR | EQ | GEQ | LEQ | GREATER | LESS) right=value)?
       ;
   
value  : (MINUS)? INTEGER                           #intVal
       | (NOT)? ( TRUE | FALSE )                    #boolVal
       | LPAR exp RPAR                              #baseExp
       | IF cond=exp THEN CLPAR thenBranch=exp CRPAR ELSE CLPAR elseBranch=exp CRPAR  #ifExp
       | ID                                             #varExp
       | ID ( LPAR (exp (COMMA exp)* )? RPAR )?         #funExp
       ;

stms   : ( stm )+ ;
stm    : ID ASM exp SEMIC                                       #stmAssignment
       | IF exp THEN CLPAR stms CRPAR ELSE CLPAR stms CRPAR     #stmIfExp
       ;

   
/*------------------------------------------------------------------
 * LEXER RULES
 *------------------------------------------------------------------*/
SEMIC  : ';' ;
COLON  : ':' ;
COMMA  : ',' ;
EQ     : '==';
LEQ    : '<=';
GEQ    : '>=';
GREATER: '>' ;
LESS   : '<' ;
AND    : '&&';
OR     : '||';
NOT    : '!' ;
ASM    : '=' ;
PLUS   : '+' ;
MINUS  : '-' ;
TIMES  : '*' ;
DIV    : '/' ;
TRUE   : 'true' ;
FALSE  : 'false' ;
LPAR   : '(' ;
RPAR   : ')' ;
CLPAR  : '{' ;
CRPAR  : '}' ;
IF     : 'if' ;
THEN   : 'then' ;
ELSE   : 'else' ;
//PRINT : 'print' ; 
LET    : 'let' ;
IN     : 'in' ;
VAR    : 'var' ;
FUN    : 'fun' ;
INT    : 'int' ;
BOOL   : 'bool' ;



//Numbers
fragment DIGIT : '0'..'9';    
INTEGER       : DIGIT+;

//IDs
fragment CHAR  : 'a'..'z' |'A'..'Z' ;
ID              : CHAR (CHAR | DIGIT)* ;

//ESCAPED SEQUENCES
WS              : (' '|'\t'|'\n'|'\r')-> skip;
LINECOMENTS    : '//' (~('\n'|'\r'))* -> skip;
BLOCKCOMENTS    : '/*'( ~('/'|'*')|'/'~'*'|'*'~'/'|BLOCKCOMENTS)* '*/' -> skip;

//ERR     : . { System.out.println("Invalid char: "+ getText()); lexicalErrors++; } -> channel(HIDDEN);
ERR     : . { errors.add("Invalid char: " + getText());} -> channel(HIDDEN) ;