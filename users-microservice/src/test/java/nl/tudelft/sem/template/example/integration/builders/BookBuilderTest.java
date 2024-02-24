package nl.tudelft.sem.template.example.integration.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import nl.tudelft.sem.template.example.builders.BookBuilder;
import nl.tudelft.sem.template.example.builders.BookDirector;
import nl.tudelft.sem.template.example.domain.book.Authors;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.Genre;
import nl.tudelft.sem.template.example.domain.book.Genres;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Series;
import nl.tudelft.sem.template.example.domain.book.Title;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BookBuilderTest {

    @Test
    public void buildsCustomPartialBookCorrectly() {
        Book book = new BookBuilder()
            .setCreator(42L)
            .setAuthors(List.of("Author 1"))
            .setGenres(List.of(Genre.ADVENTURE, Genre.COMEDY))
            .build();

        Book expected = new Book(
            42L,
            null, // null title since partial
            new Genres(List.of(Genre.ADVENTURE, Genre.COMEDY)),
            new Authors(List.of("Author 1")),
            null,
            null
        );

        assertEquals(book, expected);
    }

    @Test
    public void buildsCustomFullBookCorrectly() {
        Book book = new BookBuilder()
            .setCreator(10L)
            .setTitle("custom title")
            .setGenres(List.of(Genre.ADVENTURE))
            .setAuthors(List.of("Author 1"))
            .setSeries(List.of("Series A"))
            .setNumPages(700)
            .build();

        Book expected = new Book(
            10L,
            new Title("custom title"),
            new Genres(List.of(Genre.ADVENTURE)),
            new Authors(List.of("Author 1")),
            new Series(List.of("Series A")),
            new NumPage(700)
        );

        assertEquals(book, expected);
    }

    @Test
    public void correctlyConstructsValidBook() {
        BookBuilder builder = new BookBuilder();
        BookDirector director = new BookDirector(builder);
        director.constructValidBook();
        Book book = builder.build();

        Book expected = new Book(
            123L,
            new Title("title"),
            new Genres(List.of(Genre.ACTION, Genre.ADVENTURE)),
            new Authors(List.of("author1", "author2")),
            new Series(List.of("series")),
            new NumPage(123)
        );

        assertEquals(book, expected);
    }

    @Test
    public void correctlyConstructsUpdatedBook() {
        BookBuilder builder = new BookBuilder();
        BookDirector director = new BookDirector(builder);
        director.constructUpdatedBook();
        Book book = builder.build();

        Book expected = new Book(
            2L,
            new Title("new title"),
            new Genres(List.of(Genre.ACTION, Genre.ADVENTURE)),
            new Authors(List.of("author1", "author2")),
            new Series(List.of("series1", "series2", "series3")),
            new NumPage(100)
        );

        assertEquals(book, expected);
    }
}
