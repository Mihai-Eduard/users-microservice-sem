package nl.tudelft.sem.template.example.exceptions;

import java.io.Serial;

public class UserBannedException extends UserBookException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserBannedException() {
        super("User is banned");
    }

    public UserBannedException(String message) {
        super(message);
    }

    public UserBannedException(Long userId) {
        super("User banned with ID: " + userId);
    }

    public UserBannedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserBannedException(Throwable cause) {
        super(cause);
    }
}
