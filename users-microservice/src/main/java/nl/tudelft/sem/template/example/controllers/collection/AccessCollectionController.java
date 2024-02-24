package nl.tudelft.sem.template.example.controllers.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.book.BookDetailsResponse;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.converters.BannedConverter;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.AccessCollectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccessCollectionController {
    //outdated, will be changed when eduard extracts his methods to the new services and BookService will be deleted
    private final transient AccessCollectionService bookService;
    private final transient UserRepository userRepository;

    /**
     * Constructor for the BookController.
     *
     * @param bookService the BookService used by the controller
     * @param userRepository the UserRepository used by the controller
     */
    public AccessCollectionController(AccessCollectionService bookService, UserRepository userRepository) {
        this.bookService = bookService;
        this.userRepository = userRepository;
    }

    /**
     * Get all books by a given author.
     *
     * @param authorId the id of the author
     * @param userId the id of the user that made the request
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with the list of specific books</li>
     *         <li>ResponseEntity with code 403 if the user that made a request is banned or
     *         the provided user is not an author</li>
     *         <li>ResponseEntity with code 404 if the user that made the request or the provided author
     *         do not exist</li>
     *         <li>ResponseEntity with code 500 if other error occurred (e.g., server error, database error)</li>
     *     </ul>
     */
    @GetMapping("authorBooks/{authorID}")
    public ResponseEntity<Object> getBooksByAuthor(@RequestParam("userID") Long userId,
                                                   @PathVariable("authorID") Long authorId) {

        if (authorId == null || userId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            User user = userRepository.findById(userId).orElseThrow();
            if (user.getBanned().isBanned()) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("USER_BANNED"));
            }

            User author = userRepository.findById(authorId).orElseThrow();
            if (!author.getRole().getUserRole().equals("AUTHOR")) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("REQUESTED_USER_NOT_AN_AUTHOR"));
            }

            List<Book> books = bookService.getBooksByAuthor(author).getBookList();
            List<BookDetailsResponse> response = new ArrayList<>();
            for (Book book : books) {
                response.add(new BookDetailsResponse(
                        book.getBookId(),
                        book.getTitle().getBookTitle(),
                        new AuthorsConverter().convertToDatabaseColumn(book.getAuthors()),
                        new GenresConverter().convertToDatabaseColumn(book.getGenres()),
                        new SeriesConverter().convertToDatabaseColumn(book.getSeries()),
                        book.getPageNum().getPageNum()));
            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a book from the database with the given ID. Only possible for user that are not banned.
     *
     * @param userId The ID of the user making the request
     * @param bookId The ID of the book to be retrieved from the database
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with the contents of the book</li>
     *         <li>ResponseEntity with code 403 if the user is banned</li>
     *         <li>ResponseEntity with code 404 if the book or user does not exist</li>
     *         <li>ResponseEntity with code 500 for other errors</li>
     *     </ul>
     */
    @GetMapping("/book/{bookID}")
    public ResponseEntity<Object> getBook(@RequestParam("userID") Long userId,
                                          @PathVariable("bookID") Long bookId) {
        //check for null parameters
        if (userId == null || bookId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            //try to retrieve the user with the given userID from the database, otherwise throw an exception
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new UserNotFoundException("User not found"));

            //if the user is banned, throw an exception
            if (new BannedConverter().convertToDatabaseColumn(user.getBanned())) {
                throw new UserBannedException();
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
            //try to retrieve the book with the given bookID from the database, otherwise throw an exception
            Book book = bookService.getBook(bookId).getBook();

            //create the response object with the book details
            BookDetailsResponse response = new BookDetailsResponse(
                    book.getBookId(),
                    book.getTitle().getBookTitle(),
                    new AuthorsConverter().convertToDatabaseColumn(book.getAuthors()),
                    new GenresConverter().convertToDatabaseColumn(book.getGenres()),
                    new SeriesConverter().convertToDatabaseColumn(book.getSeries()),
                    book.getPageNum().getPageNum());

            //if the book and user exist, and the user is not banned, return the book's details
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (NoSuchElementException e) {

            //in case the book does not exist, return a 404 NOT_FOUND status code
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("BOOK_NOT_FOUND");
        } catch (Exception e) {

            //in case of other errors, return a 500 INTERNAL_SERVER_ERROR status code
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR_WHEN_RETRIEVING_BOOK");
        }
    }

    /**
     * Retrieves all distinct books from the database. Only possible for registered users that are not banned.
     *
     * @param userId The ID of the user making the request.
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with a list of all distinct books in the database</li>
     *         <li>ResponseEntity with code 403 if the user is banned</li>
     *         <li>ResponseEntity with code 404 if the user does not exist</li>
     *         <li>ResponseEntity with code 500 for other errors</li>
     *     </ul>
     */
    @GetMapping("/getAllBooks")
    public ResponseEntity<Object> getAllBooks(@RequestParam("userID") Long userId) {
        //check for null parameters
        if (userId == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            //try to retrieve the user with the given userID from the database,
            //otherwise throw an exception
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new UserNotFoundException("User not found"));

            //if the user is banned, throw an exception
            if (new BannedConverter().convertToDatabaseColumn(user.getBanned())) {
                throw new UserBannedException();
            }
        } catch (UserNotFoundException e) {

            //in case the user making the request was not found,
            //return a 404 NOT_FOUND status code
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
            //retrieve the list of all books in the database and create a list of book details
            List<Book> books = bookService.getAllBooks().getBookList();
            List<BookDetailsResponse> response = new ArrayList<>();

            //for every book in the database, add its details to the list
            for (Book book : books) {
                BookDetailsResponse details = new BookDetailsResponse(
                        book.getBookId(),
                        book.getTitle().getBookTitle(),
                        new AuthorsConverter().convertToDatabaseColumn(book.getAuthors()),
                        new GenresConverter().convertToDatabaseColumn(book.getGenres()),
                        new SeriesConverter().convertToDatabaseColumn(book.getSeries()),
                        book.getPageNum().getPageNum());

                response.add(details);
            }

            //if the user exists and is not banned,
            //return a 200 OK status code and a list of all book details
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {

            //in case of other exceptions,
            //return a 500 INTERNAL_SERVER_ERROR status code
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR_WHEN_RETRIEVING_BOOKS");
        }
    }
}
