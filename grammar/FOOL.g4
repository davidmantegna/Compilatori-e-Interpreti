grammar FOOL;

@header {
    import java.util.ArrayList;
}

@lexer::members {
   public ArrayList<String> lexicalErrors = new ArrayList<>();
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

//TODO i campi sono modificabili

prog   : exp  SEMIC                                                                           #singleExp
       | let ((exp SEMIC)| stms)                                                              #letInExp
       | (classdec)+ SEMIC (let)? ((exp SEMIC)| stms)	                                      #classExp
       ;

classdec : CLASS ID (EXTENDS ID)? LPAR (vardec (COMMA vardec)*)? RPAR CLPAR (method SEMIC)* CRPAR ;

let    : LET (dec SEMIC)+ IN;

letnest: LET (varasm SEMIC)+ IN;

vardec : type ID ;

varasm : vardec ASM exp ;

fun    : (type | VOID) ID LPAR ( vardec ( COMMA vardec)* )? RPAR (letnest)? ((exp)| stms ) ;

dec    : varasm
       | fun
       ;

type   : INT
       | BOOL
       | ID
       ;
    
exp    : left=term (operator=(PLUS | MINUS) right=exp)? ;
   
term   : left=factor (operator=(TIMES | DIV) right=term)? ;
   
factor : left=value  (operator=(AND | OR | EQ | GEQ | LEQ | GREATER | LESS) right=value)? ;

funcall: ID (LPAR (exp (COMMA exp)* )? RPAR)? ;

newexp : NEW ID LPAR (exp (COMMA exp)* )? RPAR;

value  : (MINUS)? INTEGER                                                               #intVal
       | (NOT)? ( TRUE | FALSE )                                                        #boolVal
       | LPAR exp RPAR                                                                  #baseExp
       | IF cond=exp THEN CLPAR thenBranch=exp CRPAR ELSE CLPAR elseBranch=exp CRPAR    #ifExp
       | (MINUS | NOT)? ID                                                              #varExp
       | funcall                                                                        #funExp
       | ID DOT funcall                                                                 #methodExp
       | newexp                                                                         #newMethod
       | NULL                                                                           #nullExp
       ;

stms   : ( stm )+ ;

stm    : ID ASM exp SEMIC                                                               #stmAssignment
       | IF cond=exp THEN CLPAR thenBranch=stms CRPAR ELSE CLPAR elseBranch=stms CRPAR  #stmIfExp
       ;

method : fun ;

   
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
CLASS  : 'class' ;
EXTENDS: 'extends' ;
THIS   : 'this' ;
NEW    : 'new' ;
DOT    : '.' ;
VOID   : 'void' ;
NULL   : 'null' ;


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

ERR     : . { lexicalErrors.add("Invalid char: " + getText());} -> channel(HIDDEN) ;