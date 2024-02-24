package nl.tudelft.sem.template.example.integration.book.getallbooks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.builders.BookBuilder;
import nl.tudelft.sem.template.example.builders.BookDirector;
import nl.tudelft.sem.template.example.controllers.collection.AccessCollectionController;
import nl.tudelft.sem.template.example.domain.book.Book;
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
public class GetAllBooksControllerTest {

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
    public void getAllBooksSuccessfullyTest() {
        BookBuilder bookBuilder = new BookBuilder();
        BookDirector bookDirector = new BookDirector(bookBuilder);
        List<BookDetailsResponse> bookDetailsResponses = new ArrayList<>();

        bookDirector.constructValidBook();
        Book bookOne = bookBuilder.build();

        BookDetailsResponse bookOneDetails = new BookDetailsResponse(
                bookOne.getBookId(),
                bookOne.getTitle().getBookTitle(),
                new AuthorsConverter().convertToDatabaseColumn(bookOne.getAuthors()),
                new GenresConverter().convertToDatabaseColumn(bookOne.getGenres()),
                new SeriesConverter().convertToDatabaseColumn(bookOne.getSeries()),
                bookOne.getPageNum().getPageNum()
        );

        bookDetailsResponses.add(bookOneDetails);

        bookDirector.constructUpdatedBook();
        Book bookTwo = bookBuilder.build();

        BookDetailsResponse bookTwoDetails = new BookDetailsResponse(
                bookTwo.getBookId(),
                bookTwo.getTitle().getBookTitle(),
                new AuthorsConverter().convertToDatabaseColumn(bookTwo.getAuthors()),
                new GenresConverter().convertToDatabaseColumn(bookTwo.getGenres()),
                new SeriesConverter().convertToDatabaseColumn(bookTwo.getSeries()),
                bookTwo.getPageNum().getPageNum()
        );

        bookDetailsResponses.add(bookTwoDetails);

        bookBuilder.setNumPages(256);
        bookBuilder.setCreator(18L);
        bookBuilder.setAuthors(List.of("author"));
        bookBuilder.setSeries(List.of("series", "SERIES"));
        Book bookThree = bookBuilder.build();

        BookDetailsResponse bookThreeDetails = new BookDetailsResponse(
                bookThree.getBookId(),
                bookThree.getTitle().getBookTitle(),
                new AuthorsConverter().convertToDatabaseColumn(bookThree.getAuthors()),
                new GenresConverter().convertToDatabaseColumn(bookThree.getGenres()),
                new SeriesConverter().convertToDatabaseColumn(bookThree.getSeries()),
                bookThree.getPageNum().getPageNum()
        );

        bookDetailsResponses.add(bookThreeDetails);

        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));

        List<Book> expected = new ArrayList<>();
        expected.add(bookOne);
        expected.add(bookTwo);
        expected.add(bookThree);

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findAll()).thenReturn(expected);

        ResponseEntity<Object> response = accessCollectionController.getAllBooks(user.getUserId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(bookDetailsResponses);
    }

    @Test
    public void nullUserIdTest() {
        ResponseEntity<Object> response = accessCollectionController.getAllBooks(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void userNotFoundTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Object> response = accessCollectionController.getAllBooks(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("USER_NOT_FOUND");
    }

    @Test
    public void userBannedTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(true));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        ResponseEntity<Object> response = accessCollectionController.getAllBooks(user.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo(new UserStatusResponse("USER_BANNED"));
    }

    @Test
    public void dbErrorTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(bookRepository.findAll()).thenThrow(new RuntimeException());

        ResponseEntity<Object> response = accessCollectionController.getAllBooks(user.getUserId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("ERROR_WHEN_RETRIEVING_BOOKS");
    }
}
