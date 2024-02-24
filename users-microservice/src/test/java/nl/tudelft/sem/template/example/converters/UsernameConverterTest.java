package nl.tudelft.sem.template.example.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.example.modules.user.UsernameType;
import nl.tudelft.sem.template.example.modules.user.converters.UsernameConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UsernameConverterTest {
    UsernameConverter converter = new UsernameConverter();

    @Test
    public void correctlyConvertsToDbColumn() {
        String username = "correctUname123";
        UsernameType usernameType = new UsernameType(username);

        assertEquals(converter.convertToDatabaseColumn(usernameType), username);
    }

    @Test
    public void correctlyConvertsToEntity() {
        String username = "correctUname123";
        UsernameType usernameType = new UsernameType(username);

        assertEquals(converter.convertToEntityAttribute(username), usernameType);
    }
}
