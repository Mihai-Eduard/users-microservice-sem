package nl.tudelft.sem.template.example.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.converters.PasswordConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PasswordConverterTest {
    PasswordConverter converter = new PasswordConverter();

    @Test
    public void correctlyConvertsToDbColumn() {
        String password = "ab123.?._myPassword";
        PasswordType passwordType = new PasswordType(password);
        assertEquals(converter.convertToDatabaseColumn(passwordType), password);
    }

    @Test
    public void correctlyConvertsToEntity() {
        String password = "ab123.?._myPassword";
        PasswordType passwordType = new PasswordType(password);
        assertEquals(converter.convertToEntityAttribute(password), passwordType);
    }
}
