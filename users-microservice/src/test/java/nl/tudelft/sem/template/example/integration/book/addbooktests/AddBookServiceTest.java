package nl.tudelft.sem.template.example.integration.book.addbooktests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.dtos.book.BookRequest;
import nl.tudelft.sem.template.example.dtos.book.BookResponse;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.services.ModifyCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class AddBookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private ModifyCollectionService bookService;

    @BeforeEach
    public void setUp() {
        bookService = new ModifyCollectionService(bookRepository);
    }

    @Test
    public void addBookSuccessfully() {
        long creatorId = 123L;

        BookRequest request = new BookRequest();
        request.setTitle("title");
        request.setGenre("action,ADVENTURE");
        request.setAuthor("author1,Author2");
        request.setSeries("series");
        request.setNumberOfPages(123);

        Book book = new Book(
                creatorId,
                new Title(request.getTitle()),
                new GenresConverter().convertToEntityAttribute(request.getGenre()),
                new AuthorsConverter().convertToEntityAttribute(request.getAuthor()),
                new SeriesConverter().convertToEntityAttribute(request.getSeries()),
                new NumPage(request.getNumberOfPages()));

        when(bookRepository.save(book)).thenReturn(book);

        BookResponse response = bookService.addBook(creatorId, request);
        assertThat(response.getBookId()).isEqualTo(book.getBookId());
    }

    @Test
    public void requestBodyNull() {
        long creatorId = 123L;
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(creatorId, null));
    }

    @Test
    public void creatorIdNull() {
        BookRequest request = new BookRequest();
        assertThrows(IllegalArgumentException.class,
                () -> bookService.addBook(null, request));
    }

    @Test
    public void invalidBook() {
        BookRequest request = new BookRequest();
        request.setTitle("title");
        request.setAuthor(null);
        request.setGenre("action,ADVENTURE");
        request.setSeries("series");
        request.setNumberOfPages(null);

        long creatorId = 123L;
        assertThrows(NullPointerException.class,
                () -> bookService.addBook(creatorId, request));
    }
}
