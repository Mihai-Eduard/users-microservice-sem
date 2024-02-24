package nl.tudelft.sem.template.example.dtos.book;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.tudelft.sem.template.example.domain.book.Book;

/**
 * The DTO (Data Transfer Object) used for returning the contents of a book.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class BookContentResponse {
    private Book book;
}