package nl.tudelft.sem.template.example.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import nl.tudelft.sem.template.example.domain.book.Authors;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthorsConverterTest {
    AuthorsConverter converter = new AuthorsConverter();

    @Test
    public void correctlyConvertsToDbColumn() {
        Authors authors = new Authors(List.of("author1", "author2", "author42"));
        assertEquals(converter.convertToDatabaseColumn(authors), "author1,author2,author42");
    }

    @Test
    public void correctlyConvertsToEntity() {
        Authors authors = new Authors(List.of("author1", "author2", "author42"));
        assertEquals(converter.convertToEntityAttribute("author1,author2,author42"), authors);
    }

    @Test
    public void returnsNullWhenNullList() {
        Authors authors = new Authors();
        assertNull(converter.convertToDatabaseColumn(authors));
    }
}
