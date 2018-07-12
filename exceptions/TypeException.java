package exceptions;

import org.antlr.v4.runtime.ParserRuleContext;

public class TypeException extends Exception {

    public TypeException(String m, ParserRuleContext ctx) {
        super("\033[31;1m\nErrore: \033[0m\"" + m + "\". Errore alla linea " + ctx.start.getLine() + "\n");
    }

}
