package exceptions;

public class StackOverflowError extends Error {
    public StackOverflowError() {
        super("Stack Overflow");
    }
}
