package exceptions;

public class MultipleIDException extends Exception {

    public MultipleIDException(String ID) {
        super(ID + ": identificativo già definito.");
    }

}