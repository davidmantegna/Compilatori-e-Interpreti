grammar FOOL;

@header {
    import java.util.ArrayList;
}

@lexer::members {
   public ArrayList<String> errors = new ArrayList<>();
}

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

//TODO test object orientation

prog   : exp SEMIC                                                                      #singleExp
       | let in SEMIC                                                                   #letInExp
       | (classdec)+ ((let in)?| exp) SEMIC	                                            #classExp
       ;

let    : LET (dec SEMIC)+ ;

in     : IN ((exp SEMIC)| stms) ;

letnest: LET (varasm SEMIC)+ ;

vardec : type ID ;

varasm : vardec ASM exp ;

fun    : type ID LPAR ( vardec ( COMMA vardec)* )? RPAR ((letnest in SEMIC)? |((exp SEMIC)| stms )) ;

dec    : varasm                                                                         #varAssignment
       | fun                                                                            #funDeclaration
       ;

type   : INT
       | BOOL
       | ID
       ;
    
exp    : left=term (operator=(PLUS | MINUS) right=exp)? ;
   
term   : left=factor (operator=(TIMES | DIV) right=term)? ;
   
factor : left=value  (operator=(AND | OR | EQ | GEQ | LEQ | GREATER | LESS) right=value)? ;

funcall : ID (LPAR (exp (COMMA exp)* )? RPAR)? ;

newexp : NEW ID (LPAR (exp (COMMA exp)* )? RPAR)?;

value  : (MINUS)? INTEGER                                                               #intVal
       | (NOT)? ( TRUE | FALSE )                                                        #boolVal
       | LPAR exp RPAR                                                                  #baseExp
       | IF cond=exp THEN CLPAR thenBranch=exp CRPAR ELSE CLPAR elseBranch=exp CRPAR    #ifExp
       | (MINUS)? ID                                                                    #varExp
       | THIS                                                                           #thisExp
       | funcall                                                                        #functionCall
       | (ID|THIS) DOT funcall                                                          #methodExp
       | newexp                                                                         #newFunction
       ;

stms   : ( stm )+ ;

//TODO parentesi condizione IF
stm    : ID ASM exp SEMIC                                                               #stmAssignment
       | IF cond=exp THEN CLPAR thenBranch=stms CRPAR ELSE CLPAR elseBranch=stms CRPAR  #stmIfExp
       ;

method : fun ;

classdec  : CLASS ID (EXTENDS ID)? (LPAR (vardec (COMMA vardec)*)? RPAR)? (CLPAR ((method)+)? CRPAR)? ;

   
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

ERR     : . { errors.add("Invalid char: " + getText());} -> channel(HIDDEN) ;