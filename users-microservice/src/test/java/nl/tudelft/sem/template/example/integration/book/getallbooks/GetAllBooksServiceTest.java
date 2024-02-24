package nl.tudelft.sem.template.example.integration.book.getallbooks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.example.builders.BookBuilder;
import nl.tudelft.sem.template.example.builders.BookDirector;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.dtos.book.BookListResponse;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.services.AccessCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GetAllBooksServiceTest {

    @Mock
    private BookRepository bookRepository;

    private AccessCollectionService bookService;

    @BeforeEach
    public void setUp() {
        bookService = new AccessCollectionService(bookRepository);
    }

    @Test
    public void getAllBooksTest() {
        BookBuilder bookBuilder = new BookBuilder();
        BookDirector bookDirector = new BookDirector(bookBuilder);
        List<Book> expected = new ArrayList<>();

        bookDirector.constructValidBook();
        Book bookOne = bookBuilder.build();
        expected.add(bookOne);

        bookDirector.constructUpdatedBook();
        Book bookTwo = bookBuilder.build();
        expected.add(bookTwo);

        bookBuilder.setNumPages(200);
        bookBuilder.setCreator(14L);
        bookBuilder.setAuthors(List.of("author"));
        Book bookThree = bookBuilder.build();
        expected.add(bookThree);

        when(bookRepository.findAll()).thenReturn(expected);

        BookListResponse response = bookService.getAllBooks();
        assertThat(response.getBookList().size() == expected.size()
                && response.getBookList().containsAll(expected)).isTrue();
    }

    @Test
    public void duplicatesTest() {
        BookBuilder bookBuilder = new BookBuilder();
        BookDirector bookDirector = new BookDirector(bookBuilder);
        List<Book> expected = new ArrayList<>();

        bookDirector.constructValidBook();
        Book bookOne = bookBuilder.build();
        expected.add(bookOne);

        bookDirector.constructUpdatedBook();
        Book bookTwo = bookBuilder.build();
        expected.add(bookTwo);

        //Add the duplicates
        bookOne.setBookId(3L);
        expected.add(bookOne);
        bookTwo.setBookId(7L);
        expected.add(bookOne);

        when(bookRepository.findAll()).thenReturn(expected);
        BookListResponse response = bookService.getAllBooks();

        assertThat(expected.size()).isEqualTo(4);
        assertThat(response.getBookList().size() == 4 && response.getBookList().containsAll(expected)).isTrue();
    }

    @Test
    public void getAllBooksEmptyTest() {
        when(bookRepository.findAll()).thenReturn(new ArrayList<>());

        BookListResponse response = bookService.getAllBooks();
        assertThat(response.getBookList()).isEmpty();
    }
}
