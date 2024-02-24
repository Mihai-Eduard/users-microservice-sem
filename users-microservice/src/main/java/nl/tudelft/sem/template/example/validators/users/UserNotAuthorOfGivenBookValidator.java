package nl.tudelft.sem.template.example.validators.users;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.exceptions.UserBookException;
import nl.tudelft.sem.template.example.exceptions.UserNotAuthorException;
import nl.tudelft.sem.template.example.exceptions.UserNotAuthorOfGivenBookException;
import nl.tudelft.sem.template.example.modules.user.User;

public class UserNotAuthorOfGivenBookValidator extends UserBookBaseValidator {

    /**
     * Checks if the user is an author of the book (not used, but needed for function overload).
     *
     * @param user the user to check if it is an author of the given book
     * @throws UserBookException always, since the book is expected to be provided
     */
    @Override
    public void handle(User user) throws UserBookException {
        throw new UserBookException("Just the user was provided!");
    }

    /**
     * Checks if the user is an author of the book.
     *
     * @param user the user to check if it is an author of the given book
     * @param book the book to check if the user is an author of
     * @throws UserNotAuthorException if the user is not an author
     * @throws UserNotAuthorOfGivenBookException if the user is an author, but not of the given book
     */
    @Override
    public void handle(User user, Book book) throws UserBookException {
        try {
            new UserNotAuthorValidator().handle(user);
            boolean isAuthor = book.getAuthors().getListAuthors().stream()
                    .anyMatch(author -> author.equals(user.getUsername().getUsername()));
            if (!isAuthor) {
                throw new UserNotAuthorOfGivenBookException(user.getUserId(), book.getBookId());
            }
        } catch (UserNotAuthorException e) {
            throw new UserNotAuthorException(user.getUserId());
        } catch (Exception e) {
            throw new UserNotAuthorOfGivenBookException(user.getUserId(), book.getBookId());
        }
        super.checkNext(user, book);
    }
}
