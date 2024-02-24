package nl.tudelft.sem.template.example.integration.book.valueobjects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import nl.tudelft.sem.template.example.domain.book.Genres;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests for the Genres value object of the Book entity.
 */
@SpringBootTest
public class GenresTest {
    @Test
    public void testNull() {
        assertThrows(IllegalArgumentException.class, () -> new Genres(null));
    }

    @Test
    public void testEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Genres(new ArrayList<>()));
    }

    @Test
    public void testNoArgsConstructor() {
        Genres genres = new Genres();
        assertThat(genres).isExactlyInstanceOf(Genres.class);
    }
}
