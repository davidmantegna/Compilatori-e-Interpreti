package exceptions;

import java.util.ArrayList;

public class LexerException extends Exception {

    public LexerException(ArrayList<String> errors) {
        super(errors.stream().reduce("", (prev, curr) -> prev + "\n" + curr));
    }
}