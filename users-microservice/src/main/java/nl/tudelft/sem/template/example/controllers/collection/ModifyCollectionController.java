package nl.tudelft.sem.template.example.controllers.collection;

import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.book.BookRequest;
import nl.tudelft.sem.template.example.dtos.book.BookResponse;
import nl.tudelft.sem.template.example.dtos.review.ReviewDetailsResponse;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotAdminException;
import nl.tudelft.sem.template.example.exceptions.UserNotAuthorOfGivenBookException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.converters.BannedConverter;
import nl.tudelft.sem.template.example.modules.user.converters.UserEnumConverter;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.ModifyCollectionService;
import nl.tudelft.sem.template.example.services.RestDeleteReviewsService;
import nl.tudelft.sem.template.example.validators.users.UserBannedValidator;
import nl.tudelft.sem.template.example.validators.users.UserBookValidator;
import nl.tudelft.sem.template.example.validators.users.UserNotAdminOrAuthorValidator;
import nl.tudelft.sem.template.example.validators.users.UserNotAdminValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ModifyCollectionController {
    private final transient ModifyCollectionService bookService;
    private final transient BookRepository bookRepository;
    private final transient UserRepository userRepository;
    private final transient RestDeleteReviewsService restDeleteReviewsService;

    /**
     * Constructor for the BookController.
     *
     * @param bookService the BookService used by the controller
     * @param bookRepository the BookRepository used by the controller
     * @param userRepository the UserRepository used by the controller
     * @param restDeleteReviewsService the RestDeleteReviewsService used by the controller
     */
    public ModifyCollectionController(ModifyCollectionService bookService,
                                      BookRepository bookRepository,
                                      UserRepository userRepository,
                                      RestDeleteReviewsService restDeleteReviewsService) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.restDeleteReviewsService = restDeleteReviewsService;
    }

    /**
     * Update a book in the database. Only possible for users that are either admins or the authors of the book. From
     * the provided fields in the body, only the ones that are not null and have a valid format will be updated.
     *
     * @param requestBody book (in JSON format) to be updated in the database
     * @param userId ID of the user that made the request
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with the ID of the book</li>
     *         <li>ResponseEntity with code 403 if the user is not an admin, it is banned, or it is not the author
     *         of the book, along with the current state of the user</li>
     *         <li>ResponseEntity with code 404 if the book or user does not exist</li>
     *         <li>ResponseEntity with code 500 if other error occurred (e.g., server error, database error)</li>
     *     </ul>
     */
    @PutMapping("/collection")
    public ResponseEntity<Object> updateBook(@RequestBody BookRequest requestBody,
                                             @RequestParam("userID") Long userId,
                                             @RequestParam("bookID") Long bookId) {

        if (requestBody == null || userId == null || bookId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            User user = userRepository.findById(userId).orElseThrow();
            Book book = bookRepository.findById(bookId).orElseThrow();

            UserBookValidator userBannedValidator = new UserBannedValidator();
            userBannedValidator.setNext(new UserNotAdminOrAuthorValidator());
            userBannedValidator.handle(user, book);

            BookResponse response = bookService.updateBook(bookId, requestBody);
            if (response.getBookId() == null) {
                throw new IllegalArgumentException("Inconsistency in database!");
            } else {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new BookResponse(response.getBookId()));
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();

        } catch (UserBannedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("USER_BANNED"));

        } catch (UserNotAuthorOfGivenBookException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("NOT_AN_AUTHOR"));
        } catch (UserNotAdminException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("NOT_AN_ADMIN"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a book from the database. Only possible for admins.
     *
     * @param userId ID of the user that made the request
     * @param bookId ID of the book to be deleted
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with the ID of the book</li>
     *         <li>ResponseEntity with code 403 if the user is not an admin or if it is banned,
     *         along with the current state of the </li>
     *         <li>ResponseEntity with code 404 if the book or user does not exist</li>
     *         <li>ResponseEntity with code 500 if other error occurred (e.g., server error, database error)</li>
     *     </ul>
     */
    @DeleteMapping("/collection")
    public ResponseEntity<Object> deleteBook(@RequestParam("userID") Long userId,
                                             @RequestParam("bookID") Long bookId) {

        if (userId == null || bookId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            User user = userRepository.findById(userId).orElseThrow();

            UserBookValidator userBannedValidator = new UserBannedValidator();
            userBannedValidator.setNext(new UserNotAdminValidator());
            userBannedValidator.handle(user, null);

            BookResponse response = bookService.deleteBook(bookId);
            if (response.getBookId() == null) {
                throw new NoSuchElementException();
            } else {

                deleteReviewFromMicroservice(bookId);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new BookResponse(response.getBookId()));
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();

        } catch (UserBannedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("USER_BANNED"));

        } catch (UserNotAdminException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("NOT_AN_ADMIN"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Extracted method for deleting reviews from the microservice.
     *
     * @param bookId ID of the book
     * @return true if all reviews were deleted successfully, false otherwise
     */
    @SuppressWarnings("all")
    public boolean deleteReviewFromMicroservice(Long bookId) {
        try {
            List<ReviewDetailsResponse> reviews = restDeleteReviewsService.getReviewsFromMicroservice();
            for (ReviewDetailsResponse review : reviews) {
                if (!review.getBookId().equals(bookId)) {
                    continue;
                }
                HttpStatus status = restDeleteReviewsService.deleteReviewFromMicroservice(
                        review.getReviewId(),
                        review.getUserId());
                if (!status.equals(HttpStatus.OK)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Add a book to the database. Only possible for users that are either authors or admins.
     *
     * @param creatorId ID of the user that created the book
     * @param requestBody book (in JSON format) to be saved into the database
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with the ID of the book</li>
     *         <li>ResponseEntity with code 403 if the user making the request is a regular user or banned</li>
     *         <li>ResponseEntity with code 404 if the user making the request does not exist</li>
     *         <li>ResponseEntity with code 500 for other errors</li>
     *     </ul>
     */
    @PostMapping("/collection")
    public ResponseEntity<Object> addBook(@RequestParam("userID") Long creatorId,
                                          @RequestBody BookRequest requestBody) {

        //check for null parameters
        if (requestBody == null || creatorId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            //try to retrieve the user with the given userID from the database,
            //otherwise throw an exception
            User user = userRepository.findById(creatorId).orElseThrow(
                    () -> new UserNotFoundException("User not found"));

            //if the user is banned, throw an exception
            if (new BannedConverter().convertToDatabaseColumn(user.getBanned())) {
                throw new UserBannedException();
            }

            if (new UserEnumConverter().convertToDatabaseColumn(user.getRole()).equals("USER")) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("NOT_ADMIN_OR_AUTHOR"));
            }
        } catch (UserNotFoundException e) {

            //in case the user making the request was not found, return a 404 NOT_FOUND status code
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("USER_NOT_FOUND");
        } catch (UserBannedException e) {

            //in case the user is banned, return a 403 FORBIDDEN status code
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("USER_BANNED"));
        }

        try {
            //try to add the book to the database
            BookResponse response = bookService.addBook(creatorId, requestBody);

            //if no exceptions were thrown and the book is added successfully,
            //return a 200 OK status code and the ID of the book
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            //in case of other errors, return a 500 INTERNAL_SERVER_ERROR status code
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR_WHEN_ADDING_BOOK");
        }
    }
}
