package nl.tudelft.sem.template.example.integration.book.getbook;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.tudelft.sem.template.example.controllers.collection.AccessCollectionController;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.book.BookDetailsResponse;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.AccessCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class GetBookControllerTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    private AccessCollectionController accessCollectionController;

    @BeforeEach
    public void setUp() {
        AccessCollectionService bookService = new AccessCollectionService(bookRepository);
        accessCollectionController = new AccessCollectionController(bookService, userRepository);
    }

    @Test
    public void getBookSuccessfullyTest() {
        User user = new User();
        user.setUserId(6L);
        user.setBanned(new BannedType(false));

        Book book = new Book(
                8L,
                new Title("title"),
                new GenresConverter().convertToEntityAttribute("action"),
                new AuthorsConverter().convertToEntityAttribute("author"),
                new SeriesConverter().convertToEntityAttribute("series"),
                new NumPage(123));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));

        BookDetailsResponse details = new BookDetailsResponse(
                book.getBookId(),
                book.getTitle().getBookTitle(),
                new AuthorsConverter().convertToDatabaseColumn(book.getAuthors()),
                new GenresConverter().convertToDatabaseColumn(book.getGenres()),
                new SeriesConverter().convertToDatabaseColumn(book.getSeries()),
                book.getPageNum().getPageNum()
        );

        ResponseEntity<Object> response = accessCollectionController.getBook(user.getUserId(), book.getBookId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(details);
    }

    @Test
    public void nullTest() {
        ResponseEntity<Object> response1 = accessCollectionController.getBook(null, 1L);
        ResponseEntity<Object> response2 = accessCollectionController.getBook(1L, null);
        ResponseEntity<Object> response3 = accessCollectionController.getBook(null, null);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void userNotFoundTest() {
        Book book = new Book(
                8L,
                new Title("title"),
                new GenresConverter().convertToEntityAttribute("action"),
                new AuthorsConverter().convertToEntityAttribute("author"),
                new SeriesConverter().convertToEntityAttribute("series"),
                new NumPage(123));

        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = accessCollectionController.getBook(5L, book.getBookId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    public void userBannedTest() {
        User user = new User();
        user.setUserId(3L);
        user.setBanned(new BannedType(true));

        Book book = new Book(
                8L,
                new Title("title"),
                new GenresConverter().convertToEntityAttribute("action"),
                new AuthorsConverter().convertToEntityAttribute("author"),
                new SeriesConverter().convertToEntityAttribute("series"),
                new NumPage(123));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));

        ResponseEntity<Object> response = accessCollectionController.getBook(user.getUserId(), book.getBookId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(new UserStatusResponse("USER_BANNED"));
    }

    @Test
    public void bookNotFoundTest() {
        User user = new User();
        user.setUserId(9L);
        user.setBanned(new BannedType(false));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = accessCollectionController.getBook(user.getUserId(), 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("BOOK_NOT_FOUND");
    }

    @Test
    public void dbErrorTest() {
        User user = new User();
        user.setUserId(9L);
        user.setBanned(new BannedType(false));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenThrow(new RuntimeException());

        ResponseEntity<Object> response = accessCollectionController.getBook(user.getUserId(), 1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("ERROR_WHEN_RETRIEVING_BOOK");
    }
}