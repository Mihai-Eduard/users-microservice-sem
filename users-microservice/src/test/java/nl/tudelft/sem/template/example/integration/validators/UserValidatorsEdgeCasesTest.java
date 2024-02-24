package nl.tudelft.sem.template.example.integration.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.exceptions.UserBookException;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.validators.users.UserBannedValidator;
import nl.tudelft.sem.template.example.validators.users.UserBookValidator;
import nl.tudelft.sem.template.example.validators.users.UserNotAdminOrAuthorValidator;
import nl.tudelft.sem.template.example.validators.users.UserNotAuthorOfGivenBookValidator;
import nl.tudelft.sem.template.example.validators.users.UserNotAuthorValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserValidatorsEdgeCasesTest {
    @Test
    void userNotAdminOrAuthorValidatorTest() {
        UserBookValidator validator = new UserNotAdminOrAuthorValidator();
        assertThrows(UserBookException.class, () -> validator.handle(new User()));
    }

    @Test
    void userNotAuthorOfGivenBookValidatorTest() {
        UserBookValidator validator = new UserNotAuthorOfGivenBookValidator();
        assertThrows(UserBookException.class, () -> validator.handle(new User()));
    }

    @Test
    void userNotAuthorValidatorTest() {
        User user = new User();
        user.setRole(new UserEnumType("AUTHOR"));
        user.setBanned(new BannedType(false));
        UserBookValidator validator = new UserNotAuthorValidator();
        validator.setNext(new UserBannedValidator());
        assertDoesNotThrow(() -> validator.handle(user, new Book()));
    }
}
