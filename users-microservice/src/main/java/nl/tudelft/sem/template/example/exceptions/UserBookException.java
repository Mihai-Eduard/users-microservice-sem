package nl.tudelft.sem.template.example.exceptions;

import java.io.Serial;

public class UserBookException extends Exception {
    @Serial
    private static final long serialVersionUID = 1;

    public UserBookException(String message) {
        super(message);
    }

    public UserBookException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserBookException(Throwable cause) {
        super(cause);
    }
}
