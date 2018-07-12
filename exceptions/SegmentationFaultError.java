package exceptions;

public class SegmentationFaultError extends Error {

    public SegmentationFaultError() {
        super("Tentativo di accedere a un indirizzo di memoria non valido.\n");
    }

}
