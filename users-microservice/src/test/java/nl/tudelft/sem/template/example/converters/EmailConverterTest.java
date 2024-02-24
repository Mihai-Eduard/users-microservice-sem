package nl.tudelft.sem.template.example.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.converters.EmailConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailConverterTest {
    EmailConverter converter = new EmailConverter();

    @Test
    public void correctlyConvertsToDbColumn() {
        String email = "test123@foo.com";
        EmailType emailType = new EmailType(email);
        assertEquals(converter.convertToDatabaseColumn(emailType), email);
    }

    @Test
    public void correctlyConvertsToEntity() {
        String email = "test123@foo.com";
        EmailType emailType = new EmailType(email);
        assertEquals(converter.convertToEntityAttribute(email), emailType);
    }
}
