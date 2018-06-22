package exceptions;

public class UndeclaredIDException extends Exception {
    public UndeclaredIDException(String identificatore) {
        super(identificatore + ": identificativo non dichiarato. Errore alla linea ");
    }
}
