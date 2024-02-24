package nl.tudelft.sem.template.example.dtos.book;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The DTO (Data Transfer Object) used for adding/editing a book to the database.
 */
@Getter
@Setter
@EqualsAndHashCode
public class BookRequest {
    private String title;
    private String author;
    private String genre;
    private String series;
    private Integer numberOfPages;
}
