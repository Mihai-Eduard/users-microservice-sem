package nl.tudelft.sem.template.example.integration.book.valueobjects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.tudelft.sem.template.example.domain.book.Title;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * Tests for the Title value object of the Book entity.
 */
@SpringBootTest
public class TitleTest {
    @Test
    public void testNull() {
        assertThrows(IllegalArgumentException.class, () -> new Title(null));
    }

    @Test
    public void testEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Title(""));
    }

    @Test
    public void testNoArgsConstructor() {
        Title title = new Title();
        assertThat(title).isExactlyInstanceOf(Title.class);
    }
}
