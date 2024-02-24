package nl.tudelft.sem.template.example.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import nl.tudelft.sem.template.example.domain.book.Series;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SeriesConverterTest {
    @Test
    public void convertToDatabaseColumnTest() {
        SeriesConverter seriesConverter = new SeriesConverter();
        Series series = new Series(List.of("one,two"));

        assertEquals(seriesConverter.convertToDatabaseColumn(series), "one,two");
    }
}
