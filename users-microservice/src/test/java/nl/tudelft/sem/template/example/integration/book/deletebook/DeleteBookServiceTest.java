package nl.tudelft.sem.template.example.integration.book.deletebook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.dtos.book.BookResponse;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.services.ModifyCollectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DeleteBookServiceTest {
    @Mock
    private BookRepository bookRepository;

    private ModifyCollectionService bookService;

    @BeforeEach
    void setUp() {
        bookService = new ModifyCollectionService(bookRepository);
    }

    @Test
    void bookNotFoundTest() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BookResponse response = bookService.deleteBook(1L);
        assertEquals(response, new BookResponse(null));
    }

    @Test
    void databaseErrorTest() {
        Book book = new Book();
        book.setBookId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doThrow(new RuntimeException()).when(bookRepository).deleteById(1L);

        BookResponse response = bookService.deleteBook(1L);
        assertNull(response);
    }

    @Test
    void deleteSuccessfullyTest() {
        Book book = new Book();
        book.setBookId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse response = bookService.deleteBook(1L);
        assertEquals(response, new BookResponse(1L));
        verify(bookRepository, times(1)).deleteById(1L);
    }
}
