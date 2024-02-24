package nl.tudelft.sem.template.example.services;

import javax.transaction.Transactional;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse200;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse403;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse404;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ShelfService {
    private final transient UserRepository userRepository;
    private final transient BookRepository bookRepository;

    public ShelfService(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Validates the parameters of a request of adding a specific book to a specific bookshelf of a given user.
     * Checks if UserID is tied to an existing user, and if the BookID is tied to an existing book.
     *
     * @param userId DTO parameter from request; target user ID
     * @param shelfId DTO parameter from request; target shelf ID
     * @param bookId DTO parameter from request; target book ID
     * @return Response DTO containing optional information on returned values
     */
    @Transactional
    public ManageBookShelfResponse checkBookshelfValidity(long userId, long shelfId, long bookId) {
        try {
            // check if user with this ID exists
            if (!userRepository.existsById(userId)) {
                return new ManageBookShelfResponse404();
            }
            // check if book with this ID exists
            if (!bookRepository.existsById(bookId)) {
                return new ManageBookShelfResponse404();
            }
            // check if the user is _not_ banned
            if (userRepository.findById(userId).get().getBanned().isBanned()) {
                return new ManageBookShelfResponse403("USER_BANNED");
            }
            // if user and book are confirmed to exist, pass one layer up with the correct values;
            // the controller layer will call the endpoint of the Book Service
            return new ManageBookShelfResponse200(shelfId, bookId);
        } catch (Exception e) { // used to propagate error to the controller layer and inform user
            return null; // null signifies an INTERNAL_SERVER_ERROR
        }
    }
}
