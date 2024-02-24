package nl.tudelft.sem.template.example.dtos.book;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * The DTO (Data Transfer Object) used for returning the bookId of the book
 * on which an action was performed successfully.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class BookResponse {
    private Long bookId;
}
