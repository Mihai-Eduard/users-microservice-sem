package nl.tudelft.sem.template.example.integration.book.updatebook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.controllers.collection.ModifyCollectionController;
import nl.tudelft.sem.template.example.domain.book.Authors;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.book.BookRequest;
import nl.tudelft.sem.template.example.dtos.book.BookResponse;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.ModifyCollectionService;
import nl.tudelft.sem.template.example.services.RestDeleteReviewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class UpdateBookControllerTest {
    @Mock
    private ModifyCollectionService bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestDeleteReviewsService restDeleteReviewsService;

    private ModifyCollectionController bookController;

    @BeforeEach
    void setUp() {
        bookController = new ModifyCollectionController(bookService,
                bookRepository,
                userRepository,
                restDeleteReviewsService);
    }

    @Test
    public void bookIdOrUserIdOrRequestBodyNullTest() {
        ResponseEntity<Object> response1 = bookController.updateBook(null, 1L, 2L);
        assertEquals(response1.getStatusCode(), HttpStatus.NOT_FOUND);

        ResponseEntity<Object> response2 = bookController.updateBook(new BookRequest(), null, 1L);
        assertEquals(response2.getStatusCode(), HttpStatus.NOT_FOUND);

        ResponseEntity<Object> response3 = bookController.updateBook(new BookRequest(), 1L, null);
        assertEquals(response3.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void userNotFoundTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bookController.updateBook(new BookRequest(), 1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void bookNotFoundTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bookController.updateBook(new BookRequest(), 1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void userBannedTest() {
        User user = new User();
        user.setUserId(1L);
        user.setRole(new UserEnumType("ADMIN"));
        user.setBanned(new BannedType(true));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(new Book()));

        ResponseEntity<Object> response = bookController.updateBook(new BookRequest(), 1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
        assertEquals(response.getBody(), new UserStatusResponse("USER_BANNED"));
    }

    @Test
    void userAuthorButNotForBookNullAuthorListTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("AUTHOR"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Book book = new Book();
        book.setCreatorId(3L);
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));

        ResponseEntity<Object> response = bookController.updateBook(new BookRequest(), 1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
        assertEquals(response.getBody(), new UserStatusResponse("NOT_AN_AUTHOR"));
    }

    @Test
    void userAuthorButNotForBookNonEmptyAuthorListTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("AUTHOR"));
        user.setUsername(new UsernameType("author1"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Book book = new Book();
        book.setCreatorId(3L);
        book.setAuthors(new Authors(List.of("author5", "author6", "author7")));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));

        ResponseEntity<Object> response = bookController.updateBook(new BookRequest(), 1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
        assertEquals(response.getBody(), new UserStatusResponse("NOT_AN_AUTHOR"));
    }

    @Test
    public void userNotAnAdminTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("USER"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(new Book()));

        ResponseEntity<Object> response = bookController.updateBook(new BookRequest(), 1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
        assertEquals(response.getBody(), new UserStatusResponse("NOT_AN_ADMIN"));
    }

    @Test
    void databaseErrorTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("ADMIN"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(new Book()));
        when(bookService.updateBook(2L, new BookRequest())).thenThrow(new RuntimeException());

        ResponseEntity<Object> response = bookController.updateBook(new BookRequest(), 1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void callingServiceExistenceBookMismatch() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("ADMIN"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(new Book()));
        when(bookService.updateBook(2L, new BookRequest())).thenReturn(new BookResponse(null));

        ResponseEntity<Object> response = bookController.updateBook(new BookRequest(), 1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void updateSuccessfullyAdminTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("ADMIN"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(new Book()));
        when(bookService.updateBook(2L, new BookRequest())).thenReturn(new BookResponse(2L));

        ResponseEntity<Object> response = bookController.updateBook(new BookRequest(), 1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), new BookResponse(2L));
    }

    @Test
    void updateSuccessfullyAuthorTest() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername(new UsernameType("author1"));
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("AUTHOR"));
        Book book = new Book();
        book.setCreatorId(1L);
        book.setAuthors(new Authors(List.of("author1", "author2", "author3")));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(bookService.updateBook(2L, new BookRequest())).thenReturn(new BookResponse(2L));

        ResponseEntity<Object> response = bookController.updateBook(new BookRequest(), 1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), new BookResponse(2L));
    }
}
