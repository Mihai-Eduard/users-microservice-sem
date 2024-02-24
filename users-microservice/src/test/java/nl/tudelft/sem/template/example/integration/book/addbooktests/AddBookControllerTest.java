package nl.tudelft.sem.template.example.integration.book.addbooktests;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.tudelft.sem.template.example.controllers.collection.ModifyCollectionController;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.book.BookRequest;
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
public class AddBookControllerTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModifyCollectionService bookService;
    @Mock
    private RestDeleteReviewsService restDeleteReviewsService;

    private ModifyCollectionController modifyCollectionController;

    /**
     * Sets up the controller for each test.
     */
    @BeforeEach
    public void setUp() {
        modifyCollectionController = new ModifyCollectionController(bookService,
                bookRepository,
                userRepository,
                restDeleteReviewsService);
    }

    @Test
    public void addBookSuccessfullyTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("ADMIN"));

        BookRequest requestBody = new BookRequest();
        requestBody.setTitle("title");
        requestBody.setAuthor("author1,Author2");
        requestBody.setGenre("action,ADVENTURE");
        requestBody.setSeries("series");
        requestBody.setNumberOfPages(123);

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookService.addBook(user.getUserId(), requestBody)).thenReturn(new BookResponse(2L));

        ResponseEntity<Object> response = modifyCollectionController.addBook(user.getUserId(), requestBody);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new BookResponse(2L));
    }

    @Test
    public void nullBodyTest() {
        ResponseEntity<Object> response = modifyCollectionController.addBook(1L, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void nullIdTest() {
        BookRequest request = new BookRequest();
        ResponseEntity<Object> response = modifyCollectionController.addBook(null, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void userNotFoundTest() {
        BookRequest requestBody = new BookRequest();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = modifyCollectionController.addBook(1L, requestBody);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void userBannedTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(true));
        user.setRole(new UserEnumType("ADMIN"));

        BookRequest requestBody = new BookRequest();
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookService.addBook(user.getUserId(), requestBody)).thenReturn(new BookResponse(2L));

        ResponseEntity<Object> response = modifyCollectionController.addBook(user.getUserId(), requestBody);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(new UserStatusResponse("USER_BANNED"));
    }

    @Test
    public void regularUserTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("USER"));

        BookRequest requestBody = new BookRequest();
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookService.addBook(user.getUserId(), requestBody)).thenReturn(new BookResponse(2L));

        ResponseEntity<Object> response = modifyCollectionController.addBook(user.getUserId(), requestBody);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(new UserStatusResponse("NOT_ADMIN_OR_AUTHOR"));
    }

    @Test
    public void databaseErrorTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        user.setRole(new UserEnumType("ADMIN"));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookService.addBook(user.getUserId(), new BookRequest())).thenThrow(new RuntimeException());

        ResponseEntity<Object> response = modifyCollectionController.addBook(user.getUserId(), new BookRequest());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
