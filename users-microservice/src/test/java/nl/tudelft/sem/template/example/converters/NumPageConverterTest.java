package nl.tudelft.sem.template.example.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.converters.NumPageConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;



@SpringBootTest
public class NumPageConverterTest {
    @Test
    public void convertToDatabaseColumnTest() {
        NumPageConverter numPageConverter = new NumPageConverter();
        assertEquals(numPageConverter.convertToDatabaseColumn(new NumPage(4)), 4);
    }
}
