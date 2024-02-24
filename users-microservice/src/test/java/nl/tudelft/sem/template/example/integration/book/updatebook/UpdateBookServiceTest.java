package nl.tudelft.sem.template.example.integration.book.updatebook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import nl.tudelft.sem.template.example.builders.BookBuilder;
import nl.tudelft.sem.template.example.builders.BookDirector;
import nl.tudelft.sem.template.example.domain.book.Authors;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.Genre;
import nl.tudelft.sem.template.example.domain.book.Genres;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Series;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.dtos.book.BookRequest;
import nl.tudelft.sem.template.example.dtos.book.BookResponse;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.services.ModifyCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UpdateBookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Captor
    private ArgumentCaptor<Book> bookArgumentCaptor;

    private ModifyCollectionService bookService;

    @BeforeEach
    void setUp() {
        bookService = new ModifyCollectionService(bookRepository);
    }

    @Test
    void bookNotFoundTest() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BookResponse response = bookService.updateBook(1L, new BookRequest());
        assertEquals(response, new BookResponse(null));
    }

    @Test
    void updateBookSuccessfullyTitleAndGenresAndAuthorsTest() {
        // create builder and director for sample book creation
        BookBuilder builder = new BookBuilder();
        BookDirector director = new BookDirector(builder);
        // construct the sample book
        director.constructUpdatedBook();
        Book book = builder.build();
        book.setBookId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookRequest requestBody = new BookRequest();
        requestBody.setTitle("new title");
        requestBody.setGenre("ACTION,COMEDY");
        requestBody.setAuthor("author2,author3,author4");

        BookResponse response = bookService.updateBook(1L, requestBody);
        assertEquals(response, new BookResponse(1L));
        verify(bookRepository, times(1)).save(bookArgumentCaptor.capture());

        Book updatedBook = bookArgumentCaptor.getValue();
        assertEquals(updatedBook, book);
        assertEquals(updatedBook.getTitle(), new Title("new title"));
        assertEquals(updatedBook.getGenres(), new Genres(Arrays.asList(Genre.ACTION, Genre.COMEDY)));
        assertEquals(updatedBook.getAuthors(), new Authors(Arrays.asList("author2", "author3", "author4")));
        assertEquals(updatedBook.getSeries(), new Series(Arrays.asList("series1", "series2", "series3")));
        assertEquals(updatedBook.getPageNum(), new NumPage(100));
    }

    @Test
    void updateBookSuccessfullySeriesAndNumPage() {
        Book book = new Book(
                1L,
                2L,
                new Title("title"),
                new Genres(Arrays.asList(Genre.ACTION, Genre.ADVENTURE)),
                new Authors(Arrays.asList("author1", "author2")),
                new Series(Arrays.asList("series1", "series2", "series3")),
                new NumPage(100)
        );
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookRequest requestBody = new BookRequest();
        requestBody.setSeries("series3,series4");
        requestBody.setNumberOfPages(200);

        BookResponse response = bookService.updateBook(1L, requestBody);
        assertEquals(response, new BookResponse(1L));
        verify(bookRepository, times(1)).save(bookArgumentCaptor.capture());

        Book updatedBook = bookArgumentCaptor.getValue();
        assertEquals(updatedBook, book);
        assertEquals(updatedBook.getTitle(), new Title("title"));
        assertEquals(updatedBook.getGenres(), new Genres(Arrays.asList(Genre.ACTION, Genre.ADVENTURE)));
        assertEquals(updatedBook.getAuthors(), new Authors(Arrays.asList("author1", "author2")));
        assertEquals(updatedBook.getSeries(), new Series(Arrays.asList("series3", "series4")));
        assertEquals(updatedBook.getPageNum(), new NumPage(200));
    }

    @Test
    void updateBookSuccessfullyCompleteBook() {
        Book book = new Book(
                1L,
                2L,
                new Title("title"),
                new Genres(Arrays.asList(Genre.ACTION, Genre.ADVENTURE)),
                new Authors(Arrays.asList("author1", "author2")),
                new Series(Arrays.asList("series1", "series2", "series3")),
                new NumPage(100)
        );
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookRequest requestBody = new BookRequest();
        requestBody.setTitle("new title");
        requestBody.setGenre("ACTION,COMEDY");
        requestBody.setAuthor("author2,author3,author4");
        requestBody.setSeries("series3,series4");
        requestBody.setNumberOfPages(200);

        BookResponse response = bookService.updateBook(1L, requestBody);
        assertEquals(response, new BookResponse(1L));
        verify(bookRepository, times(1)).save(bookArgumentCaptor.capture());

        Book updatedBook = bookArgumentCaptor.getValue();
        assertEquals(updatedBook, book);
        assertEquals(updatedBook.getTitle(), new Title("new title"));
        assertEquals(updatedBook.getGenres(), new Genres(Arrays.asList(Genre.ACTION, Genre.COMEDY)));
        assertEquals(updatedBook.getAuthors(), new Authors(Arrays.asList("author2", "author3", "author4")));
        assertEquals(updatedBook.getSeries(), new Series(Arrays.asList("series3", "series4")));
        assertEquals(updatedBook.getPageNum(), new NumPage(200));
    }

    @Test
    void databaseErrorTest() {
        Book book = new Book(
                1L,
                2L,
                new Title("title"),
                new Genres(Arrays.asList(Genre.ACTION, Genre.ADVENTURE)),
                new Authors(Arrays.asList("author1", "author2")),
                new Series(Arrays.asList("series1", "series2", "series3")),
                new NumPage(100)
        );
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookRequest requestBody = new BookRequest();
        requestBody.setTitle("new title");
        requestBody.setGenre("ACTION,COMEDY");
        requestBody.setAuthor("author2,author3,author4");
        requestBody.setSeries("series3,series4");
        requestBody.setNumberOfPages(200);
        when(bookRepository.save(any())).thenThrow(new RuntimeException());

        BookResponse response = bookService.updateBook(1L, requestBody);
        assertNull(response);
    }
}
