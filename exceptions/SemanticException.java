package exceptions;

import java.util.ArrayList;

public class SemanticException extends Error {

    public SemanticException(ArrayList<String> errors) {
        super(errors.stream().reduce("\n", (prev, curr) -> prev + curr));
    }

}
