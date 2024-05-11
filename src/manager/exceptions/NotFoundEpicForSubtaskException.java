package manager.exceptions;

public class NotFoundEpicForSubtaskException extends RuntimeException {
    public NotFoundEpicForSubtaskException(String message) {
        super(message);
    }
}
