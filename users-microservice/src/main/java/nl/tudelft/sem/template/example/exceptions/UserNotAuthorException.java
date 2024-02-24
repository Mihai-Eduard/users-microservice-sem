package nl.tudelft.sem.template.example.exceptions;

import java.io.Serial;

public class UserNotAuthorException extends UserBookException {
    @Serial
    private static final long serialVersionUID = 1;

    public UserNotAuthorException(Long userId) {
        super("User with the ID: " + userId + " is not an author!");
    }
}
