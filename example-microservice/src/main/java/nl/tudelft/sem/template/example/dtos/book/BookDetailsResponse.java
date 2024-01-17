package nl.tudelft.sem.template.example.dtos.book;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class BookDetailsResponse {
    private Long bookID;
    private String title;
    private String author;
    private String genre;
    private String series;
    private Integer numberOfPages;
}
