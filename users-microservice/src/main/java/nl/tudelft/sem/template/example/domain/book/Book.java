package nl.tudelft.sem.template.example.domain.book;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.NumPageConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.domain.book.converters.TitleConverter;

/**
 * A DDD entity representing a book in our domain.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {
    /**
     * Identifier for the book.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long bookId;

    private long creatorId;

    @Convert(converter = TitleConverter.class)
    private Title title;

    @Convert(converter = GenresConverter.class)
    private Genres genres;

    @Convert(converter = AuthorsConverter.class)
    private Authors authors;

    @Convert(converter = SeriesConverter.class)
    private Series series;

    @Convert(converter = NumPageConverter.class)
    private NumPage pageNum;

    /**
     * Constructor for Book class without bookId.
     *
     * @param creatorId id of the user that added the book to the system
     * @param title Title of the book
     * @param genres List of genres of the book
     * @param authors List of authors of the book
     * @param pageNum Number of pages of the book
     */
    public Book(long creatorId, Title title, Genres genres, Authors authors, Series series, NumPage pageNum) {
        this.creatorId = creatorId;
        this.title = title;
        this.genres = genres;
        this.authors = authors;
        this.series = series;
        this.pageNum = pageNum;
    }
}
