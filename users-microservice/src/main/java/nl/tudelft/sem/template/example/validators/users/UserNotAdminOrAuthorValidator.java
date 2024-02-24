package nl.tudelft.sem.template.example.validators.users;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.exceptions.UserBookException;
import nl.tudelft.sem.template.example.exceptions.UserNotAdminException;
import nl.tudelft.sem.template.example.exceptions.UserNotAuthorException;
import nl.tudelft.sem.template.example.exceptions.UserNotAuthorOfGivenBookException;
import nl.tudelft.sem.template.example.modules.user.User;

public class UserNotAdminOrAuthorValidator extends UserBookBaseValidator {
    /**
     * Checks if the user is an admin or author of the book (not used, but needed for function overload).
     *
     * @param user the user to check if he is either an admin or author
     * @throws UserBookException always, since the book is expected to be provided
     */
    @Override
    public void handle(User user) throws UserBookException {
        throw new UserBookException("Just the user was provided!");
    }

    /**
     * Checks if the user is an admin or author of the book.
     *
     * @param user the user to check if he is either an admin or author
     * @param book the book to check if the user is an author of
     * @throws UserNotAuthorOfGivenBookException if the user is an author, but not of the given book
     * @throws UserNotAdminException if the user is neither an admin nor an author
     */
    @Override
    public void handle(User user, Book book) throws UserBookException {
        try {
            new UserNotAuthorOfGivenBookValidator().handle(user, book);
        } catch (UserNotAuthorException e) {
            new UserNotAdminValidator().handle(user);
        }
        super.checkNext(user, book);
    }
}
