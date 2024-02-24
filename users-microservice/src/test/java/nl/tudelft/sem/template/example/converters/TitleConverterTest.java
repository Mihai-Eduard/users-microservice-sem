package nl.tudelft.sem.template.example.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.TitleConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TitleConverterTest {
    @Test
    public void convertToDatabaseColumnTest() {
        TitleConverter titleConverter = new TitleConverter();
        Title title = new Title("title");

        assertEquals(titleConverter.convertToDatabaseColumn(title), "title");
    }
}
