package exceptions;

public class SegFaultError extends Error {

    public SegFaultError() {
        super("Tentativo di accedere a un indirizzo di memoria non valido.");
    }

}
