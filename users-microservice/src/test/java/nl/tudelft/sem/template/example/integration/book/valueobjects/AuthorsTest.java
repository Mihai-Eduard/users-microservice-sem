package nl.tudelft.sem.template.example.integration.book.valueobjects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import nl.tudelft.sem.template.example.domain.book.Authors;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests for the Authors value object of the Book entity.
 */
@SpringBootTest
public class AuthorsTest {
    @Test
    public void testNull() {
        assertThrows(IllegalArgumentException.class, () -> new Authors(null));
    }

    @Test
    public void testEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Authors(new ArrayList<>()));
    }

    @Test
    public void testNoArgsConstructor() {
        Authors authors = new Authors();
        assertThat(authors).isExactlyInstanceOf(Authors.class);
    }
}
