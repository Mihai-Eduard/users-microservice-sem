package nl.tudelft.sem.template.example.validators.users;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.exceptions.UserBookException;
import nl.tudelft.sem.template.example.exceptions.UserNotAuthorException;
import nl.tudelft.sem.template.example.modules.user.User;

public class UserNotAuthorValidator extends UserBookBaseValidator {
    /**
     * Checks if the user is an author.
     *
     * @param user the user to check if it is an author
     * @throws UserNotAuthorException if the user is not an author
     */
    @Override
    public void handle(User user) throws UserBookException {
        if (!user.getRole().getUserRole().equals("AUTHOR")) {
            throw new UserNotAuthorException(user.getUserId());
        }
    }

    /**
     * Checks if the user is an author.
     *
     * @param user the user to check if it is an author
     * @param book the book to check if the conditions hold (not used, but needed for function overload)
     * @throws UserNotAuthorException if the user is not an author
     */
    @Override
    public void handle(User user, Book book) throws UserBookException {
        handle(user);
        super.checkNext(user, book);
    }
}
