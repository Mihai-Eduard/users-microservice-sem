package nl.tudelft.sem.template.example.builders;

import java.util.List;
import lombok.Setter;
import nl.tudelft.sem.template.example.domain.book.Authors;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.Genre;
import nl.tudelft.sem.template.example.domain.book.Genres;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Series;
import nl.tudelft.sem.template.example.domain.book.Title;

@Setter
public class BookBuilder implements BookBuilderInterface {
    private long creatorId;
    private Title title;
    private Genres genres;
    private Authors authors;
    private Series series;
    private NumPage numPages;

    /**
     * Adds the creatorId field to the builder.
     *
     * @param creatorId ID of creator
     * @return self (builder instance)
     */
    @Override
    public BookBuilder setCreator(long creatorId) {
        this.creatorId = creatorId;
        return this;
    }

    /**
     * Adds the title field to the builder.
     *
     * @param title title of the book
     * @return self (builder instance)
     */
    @Override
    public BookBuilderInterface setTitle(String title) {
        this.title = new Title(title);
        return this;
    }

    /**
     * Adds the genres to the builder.
     *
     * @param genres list of genres (ENUM)
     * @return self (builder instance)
     */
    @Override
    public BookBuilderInterface setGenres(List<Genre> genres) {
        this.genres = new Genres(genres);
        return this;
    }

    /**
     * Adds the authors to the builder.
     *
     * @param authors list of authors of the book#
     * @return self (builder instance)
     */
    @Override
    public BookBuilderInterface setAuthors(List<String> authors) {
        this.authors = new Authors(authors);
        return this;
    }

    /**
     * Adds the series to the builder.
     *
     * @param series list of series
     * @return self (builder instance)
     */
    @Override
    public BookBuilderInterface setSeries(List<String> series) {
        this.series = new Series(series);
        return this;
    }

    /**
     * Adds the number of pages to the builder.
     *
     * @param numPages number of pages
     * @return self (builder instance)
     */
    @Override
    public BookBuilderInterface setNumPages(int numPages) {
        this.numPages = new NumPage(numPages);
        return this;
    }

    /**
     * Builds the book entity.
     *
     * @return Book entity with the specified fields
     */
    @Override
    public Book build() {
        return new Book(
            this.creatorId,
            this.title,
            this.genres,
            this.authors,
            this.series,
            this.numPages
        );
    }
}
