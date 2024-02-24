package nl.tudelft.sem.template.example.dtos.book;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.tudelft.sem.template.example.domain.book.Book;

/**
 * The DTO (Data Transfer Object) used for returning multiple books at the same time.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class BookListResponse {
    private List<Book> bookList;
}
