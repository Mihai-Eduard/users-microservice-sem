package nl.tudelft.sem.template.example.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import nl.tudelft.sem.template.example.domain.book.Genre;
import nl.tudelft.sem.template.example.domain.book.Genres;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GenresConverterTest {
    GenresConverter converter = new GenresConverter();

    @Test
    public void correctlyConvertsToDbColumn() {
        Genres genres = new Genres(List.of(Genre.ADVENTURE, Genre.ACTION));
        assertEquals(converter.convertToDatabaseColumn(genres), "ADVENTURE,ACTION");
    }

    @Test
    public void correctlyConvertsToEntity() {
        Genres genres = new Genres(List.of(Genre.ADVENTURE, Genre.ACTION));
        assertEquals(converter.convertToEntityAttribute("ADVENTURE,ACTION"), genres);
    }

    @Test
    public void returnsNullWhenNullList() {
        Genres genres = new Genres();
        assertNull(converter.convertToDatabaseColumn(genres));
    }
}
