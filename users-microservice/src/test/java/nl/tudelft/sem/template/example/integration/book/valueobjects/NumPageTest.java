package nl.tudelft.sem.template.example.integration.book.valueobjects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.tudelft.sem.template.example.domain.book.NumPage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test for the NumPage value object in the Book entity.
 */
@SpringBootTest
public class NumPageTest {
    @Test
    public void throwsErrorForNegativePages() {
        assertThrows(IllegalArgumentException.class, () -> new NumPage(-10));
    }

    @Test
    public void throwsErrorForZeroPages() {
        assertThrows(IllegalArgumentException.class, () -> new NumPage(0));
    }

    @Test
    public void allowsPositivePages() {
        assertDoesNotThrow(() -> new NumPage(1));
        assertEquals((new NumPage(1)).getPageNum(), 1);
    }

    @Test
    public void testNoArgsConstructor() {
        NumPage numPage = new NumPage();
        assertThat(numPage).isExactlyInstanceOf(NumPage.class);
    }
}
