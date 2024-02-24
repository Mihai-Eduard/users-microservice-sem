package nl.tudelft.sem.template.example.validators.users;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.exceptions.UserBookException;
import nl.tudelft.sem.template.example.modules.user.User;

public abstract class UserBookBaseValidator implements UserBookValidator {
    private transient UserBookValidator nextValidator;

    /**
     * Sets the next validator in the chain.
     *
     * @param nextValidator the next validator in the chain
     */
    @Override
    public void setNext(UserBookValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

    /**
     * Checks the next validator in the chain.
     *
     * @param user the user to check if the user-related conditions are satisfied
     * @param book the book to check if it has the suitable relation with the given user
     * @throws UserBookException if the user or book does not satisfy the constraints
     */
    protected void checkNext(User user, Book book) throws UserBookException {
        if (nextValidator != null) {
            nextValidator.handle(user, book);
        }
    }
}
