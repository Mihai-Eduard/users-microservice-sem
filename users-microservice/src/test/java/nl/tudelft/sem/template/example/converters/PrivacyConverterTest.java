package nl.tudelft.sem.template.example.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.converters.PrivacyConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PrivacyConverterTest {
    PrivacyConverter converter = new PrivacyConverter();

    @Test
    public void correctlyConvertsToDbColumnTrue() {
        Boolean collect = true;
        PrivacyType privacyType = new PrivacyType(collect);

        assertEquals(converter.convertToDatabaseColumn(privacyType), collect);
    }

    @Test
    public void correctlyConvertsToDbColumnFalse() {
        Boolean collect = false;
        PrivacyType privacyType = new PrivacyType(collect);

        assertEquals(converter.convertToDatabaseColumn(privacyType), collect);
    }

    @Test
    public void correctlyConvertsToEntityTrue() {
        Boolean collect = true;
        PrivacyType privacyType = new PrivacyType(collect);

        assertEquals(converter.convertToEntityAttribute(collect), privacyType);
    }

    @Test
    public void correctlyConvertsToEntityFalse() {
        Boolean collect = false;
        PrivacyType privacyType = new PrivacyType(collect);

        assertEquals(converter.convertToEntityAttribute(collect), privacyType);
    }
}
