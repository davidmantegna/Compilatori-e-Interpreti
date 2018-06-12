package exceptions;

public class UndeclaredIDException extends Exception {
    public UndeclaredIDException(String ID) {
        super(ID + ": identificativo non dichiarato.");
    }
}
