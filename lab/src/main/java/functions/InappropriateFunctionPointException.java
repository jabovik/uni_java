package functions;

public class InappropriateFunctionPointException extends Exception {

    public InappropriateFunctionPointException() {
        super();
    }

    public InappropriateFunctionPointException(String message) {
        super(message);
    }

    public InappropriateFunctionPointException(Throwable cause) {
        super(cause);
    }

    public InappropriateFunctionPointException(String message, Throwable cause) {
        super(message, cause);
    }
}
