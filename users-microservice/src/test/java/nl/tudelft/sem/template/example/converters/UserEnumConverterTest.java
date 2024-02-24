package nl.tudelft.sem.template.example.converters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.converters.UserEnumConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserEnumConverterTest {
    UserEnumConverter converter = new UserEnumConverter();

    @Test
    public void correctlyConvertsToDbColumnCorrectUserRole() {
        String role = "USER";
        UserEnumType userEnumType = new UserEnumType(role);

        assertEquals(converter.convertToDatabaseColumn(userEnumType), role);
    }

    @Test
    public void correctlyConvertsToEntityCorrectUserRole() {
        String role = "ADMIN";
        UserEnumType userEnumType = new UserEnumType(role);

        assertEquals(converter.convertToEntityAttribute(role), userEnumType);
    }
}
