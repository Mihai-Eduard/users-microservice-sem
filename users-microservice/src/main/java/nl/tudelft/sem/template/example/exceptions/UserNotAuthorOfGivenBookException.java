package nl.tudelft.sem.template.example.exceptions;

import java.io.Serial;

public class UserNotAuthorOfGivenBookException extends UserBookException {
    @Serial
    private static final long serialVersionUID = 1;

    public UserNotAuthorOfGivenBookException(Long userId, Long bookId) {
        super("User with the ID: " + userId + " is not an author of the book with the ID: " + bookId + "!");
    }
}
