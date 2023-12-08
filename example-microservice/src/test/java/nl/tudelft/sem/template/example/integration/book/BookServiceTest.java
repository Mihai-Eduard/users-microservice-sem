package nl.tudelft.sem.template.example.integration.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.models.BookModel;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.services.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockBookRepository"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class BookServiceTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void updateBookSuccessfully() {
        Book book = new Book();
        BookModel newBook = new BookModel();
        book.setTitle(new Title("InitialTitle"));
        newBook.setTitle("UpdatedTitle");
        Book updatedBook = new Book();
        updatedBook.setTitle(new Title("UpdatedTitle"));

        when(bookRepository.save(book)).thenReturn(book);

        Book results = new BookService(bookRepository).updateBook(book, newBook);
        assertThat(results).isEqualTo(updatedBook);
    }
}