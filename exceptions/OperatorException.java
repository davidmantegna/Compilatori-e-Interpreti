package exceptions;

public class OperatorException extends Exception {
    public OperatorException(String m) {
        super("Operatore " + m + " non è valido per il nostro Linguaggio");
    }
}
