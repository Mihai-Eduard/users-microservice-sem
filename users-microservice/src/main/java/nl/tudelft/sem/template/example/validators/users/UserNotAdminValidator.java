package nl.tudelft.sem.template.example.validators.users;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.exceptions.UserBookException;
import nl.tudelft.sem.template.example.exceptions.UserNotAdminException;
import nl.tudelft.sem.template.example.modules.user.User;

public class UserNotAdminValidator extends UserBookBaseValidator {
    /**
     * Checks if the user is an admin.
     *
     * @param user the user to check if it is an admin
     * @throws UserNotAdminException if the user is not an admin
     */
    @Override
    public void handle(User user) throws UserBookException {
        if (!user.getRole().getUserRole().equals("ADMIN")) {
            throw new UserNotAdminException(user.getUserId());
        }
    }

    /**
     * Checks if the user is an admin.
     *
     * @param user the user to check if it is an admin
     * @param book the book to check if the conditions hold (not used, but needed for function overload)
     * @throws UserNotAdminException if the user is not an admin
     */
    @Override
    public void handle(User user, Book book) throws UserBookException {
        handle(user);
        super.checkNext(user, book);
    }
}
