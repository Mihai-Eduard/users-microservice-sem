package nl.tudelft.sem.template.example.integration.book.getauthorbooks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.builders.BookBuilder;
import nl.tudelft.sem.template.example.builders.BookDirector;
import nl.tudelft.sem.template.example.controllers.collection.AccessCollectionController;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.dtos.book.BookListResponse;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.AccessCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class GetAuthorBooksControllerTest {
    @Mock
    private AccessCollectionService bookService;
    @Mock
    private UserRepository userRepository;

    private AccessCollectionController bookController;

    @BeforeEach
    void setUp() {
        bookController = new AccessCollectionController(bookService, userRepository);
    }

    @Test
    void nullParametersTest() {
        ResponseEntity<Object> response1 = bookController.getBooksByAuthor(1L, null);
        assertEquals(404, response1.getStatusCodeValue());

        ResponseEntity<Object> response2 = bookController.getBooksByAuthor(null, 5L);
        assertEquals(404, response2.getStatusCodeValue());
    }

    @Test
    void userBannedTest() {
        User user = new User();
        user.setBanned(new BannedType(true));
        user.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = bookController.getBooksByAuthor(1L, 5L);
        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void providedUserNotAnAuthor() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User author = new User();
        author.setUserId(5L);
        author.setRole(new UserEnumType("USER"));
        when(userRepository.findById(5L)).thenReturn(Optional.of(author));

        ResponseEntity<Object> response = bookController.getBooksByAuthor(1L, 5L);
        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void userOrAuthorNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response1 = bookController.getBooksByAuthor(1L, 5L);
        assertEquals(404, response1.getStatusCodeValue());

        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response2 = bookController.getBooksByAuthor(1L, 5L);
        assertEquals(404, response2.getStatusCodeValue());
    }

    @Test
    void getAuthorBooksSuccessfully() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User author = new User();
        author.setUserId(5L);
        author.setRole(new UserEnumType("AUTHOR"));
        when(userRepository.findById(5L)).thenReturn(Optional.of(author));

        List<Book> books = new ArrayList<>();
        BookBuilder bookBuilder = new BookBuilder();
        new BookDirector(bookBuilder).constructValidBook();
        books.add(bookBuilder.build());
        when(bookService.getBooksByAuthor(author)).thenReturn(new BookListResponse(books));

        ResponseEntity<Object> response = bookController.getBooksByAuthor(1L, 5L);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void serverErrorTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User author = new User();
        author.setUserId(5L);
        author.setRole(new UserEnumType("AUTHOR"));
        when(userRepository.findById(5L)).thenReturn(Optional.of(author));

        when(bookService.getBooksByAuthor(author)).thenThrow(new IllegalArgumentException());

        ResponseEntity<Object> response = bookController.getBooksByAuthor(1L, 5L);
        assertEquals(500, response.getStatusCodeValue());
    }
}
