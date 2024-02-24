package nl.tudelft.sem.template.example.integration.book.valueobjects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import nl.tudelft.sem.template.example.domain.book.Series;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests for the Series value object of the Book entity.
 */
@SpringBootTest
public class SeriesTest {
    @Test
    public void testNull() {
        assertThrows(IllegalArgumentException.class, () -> new Series(null));
    }

    @Test
    public void testEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Series(new ArrayList<>()));
    }

    @Test
    public void testNoArgsConstructor() {
        Series series = new Series();
        assertThat(series).isExactlyInstanceOf(Series.class);
    }
}
