package exceptions;

public class MultipleIDException extends Exception {

    public MultipleIDException(String identificativo, String type) {
        super(identificativo + ": identificativo già definito. Il tipo di '" + identificativo + "' nello scope attuale è stato precedentemente definito come " + type + " \n");
    }

}