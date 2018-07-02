package exceptions;

public class UndeclaredMethodIDException extends Exception {

    public UndeclaredMethodIDException(String methodID,String classID) {
        super(methodID + ": metodo non dichiarato nella classe "+classID+".\n");
    }
}
