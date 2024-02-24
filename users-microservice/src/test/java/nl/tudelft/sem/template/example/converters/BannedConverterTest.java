package nl.tudelft.sem.template.example.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.converters.BannedConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class BannedConverterTest {
    BannedConverter converter = new BannedConverter();

    @Test
    public void correctlyConvertsToDbColumnTrue() {
        BannedType type = new BannedType(true);
        assertEquals(converter.convertToDatabaseColumn(type), true);
    }

    @Test
    public void correctlyConvertsToDbColumnFalse() {
        BannedType type = new BannedType(false);
        assertEquals(converter.convertToDatabaseColumn(type), false);
    }

    @Test
    public void correctlyConvertsToEntityTrue() {
        Boolean data = true;
        assertEquals(converter.convertToEntityAttribute(data), new BannedType(true));
    }

    @Test
    public void correctlyConvertsToEntityFalse() {
        Boolean data = false;
        assertEquals(converter.convertToEntityAttribute(data), new BannedType(false));
    }
}
