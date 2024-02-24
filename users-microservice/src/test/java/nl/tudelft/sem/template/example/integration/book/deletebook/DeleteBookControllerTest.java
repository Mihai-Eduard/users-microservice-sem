package nl.tudelft.sem.template.example.integration.book.deletebook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.tudelft.sem.template.example.controllers.collection.ModifyCollectionController;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.book.BookResponse;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
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
public class DeleteBookControllerTest {
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
    public void bookIdOrUserIdNullTest() {
        ResponseEntity<Object> response1 = bookController.deleteBook(null, 1L);
        assertEquals(response1.getStatusCode(), HttpStatus.NOT_FOUND);

        ResponseEntity<Object> response2 = bookController.deleteBook(1L, null);
        assertEquals(response2.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void userNotFoundTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = bookController.deleteBook(1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void userBannedTest() {
        User user = new User();
        user.setUserId(1L);
        user.setRole(new UserEnumType("ADMIN"));
        user.setBanned(new BannedType(true));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = bookController.deleteBook(1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
        assertEquals(response.getBody(), new UserStatusResponse("USER_BANNED"));
    }

    @Test
    public void userNotAnAdminTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        user.setRole(new UserEnumType("USER"));
        ResponseEntity<Object> response1 = bookController.deleteBook(1L, 2L);
        assertEquals(response1.getStatusCode(), HttpStatus.FORBIDDEN);
        assertEquals(response1.getBody(), new UserStatusResponse("NOT_AN_ADMIN"));

        user.setRole(new UserEnumType("AUTHOR"));
        ResponseEntity<Object> response2 = bookController.deleteBook(1L, 2L);
        assertEquals(response2.getStatusCode(), HttpStatus.FORBIDDEN);
        assertEquals(response2.getBody(), new UserStatusResponse("NOT_AN_ADMIN"));
    }

    @Test
    public void bookNotFoundTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("ADMIN"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookService.deleteBook(2L)).thenReturn(new BookResponse(null));

        ResponseEntity<Object> response = bookController.deleteBook(1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void databaseErrorTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("ADMIN"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookService.deleteBook(2L)).thenReturn(null);

        ResponseEntity<Object> response = bookController.deleteBook(1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void deleteSuccessfullyTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("ADMIN"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookService.deleteBook(2L)).thenReturn(new BookResponse(2L));

        ResponseEntity<Object> response = bookController.deleteBook(1L, 2L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), new BookResponse(2L));
    }
}
