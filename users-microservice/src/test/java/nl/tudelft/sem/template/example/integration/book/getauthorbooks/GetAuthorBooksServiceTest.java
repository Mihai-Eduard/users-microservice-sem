package nl.tudelft.sem.template.example.integration.book.getauthorbooks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import nl.tudelft.sem.template.example.domain.book.Authors;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.services.AccessCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GetAuthorBooksServiceTest {
    @Mock
    private BookRepository bookRepository;

    private AccessCollectionService bookService;

    @BeforeEach
    void setUp() {
        bookService = new AccessCollectionService(bookRepository);
    }

    @Test
    void noBookFoundTest() {
        User author = new User();
        DetailType detailType = new DetailType();
        detailType.setName("Author6");
        author.setDetails(detailType);
        Book book1 = new Book();
        book1.setAuthors(new Authors(List.of("Author1", "Author2")));
        Book book2 = new Book();
        book2.setAuthors(new Authors(List.of("Author3", "Author4")));
        Book book3 = new Book();
        book3.setAuthors(new Authors(List.of("Author5", "Author3")));
        Book book4 = new Book();
        book4.setAuthors(new Authors(List.of("Author7")));
        Book book5 = new Book();
        book5.setAuthors(new Authors(List.of("Author3", "Author4", "Author5")));
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3));

        List<Book> books = bookService.getBooksByAuthor(author).getBookList();
        assertEquals(0, books.size());
    }

    @Test
    void multipleBooksWithOneAuthorFound() {
        User author = new User();
        DetailType detailType = new DetailType();
        detailType.setName("Author3");
        author.setDetails(detailType);
        Book book1 = new Book();
        book1.setAuthors(new Authors(List.of("Author1")));
        Book book2 = new Book();
        book2.setAuthors(new Authors(List.of("Author3")));
        Book book3 = new Book();
        book3.setAuthors(new Authors(List.of("Author3")));
        Book book4 = new Book();
        book4.setAuthors(new Authors(List.of("Author4")));
        Book book5 = new Book();
        book5.setAuthors(new Authors(List.of("Author3")));
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3, book4, book5));

        List<Book> books = bookService.getBooksByAuthor(author).getBookList();
        assertEquals(3, books.size());
    }

    @Test
    void multipleBooksWithMultipleAuthorsFound() {
        User author = new User();
        DetailType detailType = new DetailType();
        detailType.setName("Author3");
        author.setDetails(detailType);
        Book book1 = new Book();
        book1.setAuthors(new Authors(List.of("Author1", "Author2", "Author3")));
        Book book2 = new Book();
        book2.setAuthors(new Authors(List.of("Author3", "Author4")));
        Book book3 = new Book();
        book3.setAuthors(new Authors(List.of("Author5")));
        Book book4 = new Book();
        book4.setAuthors(new Authors(List.of("Author3")));
        Book book5 = new Book();
        book5.setAuthors(new Authors(List.of("Author6", "Author3", "Author4")));
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2, book3, book4, book5));

        List<Book> books = bookService.getBooksByAuthor(author).getBookList();
        assertEquals(4, books.size());
    }
}
