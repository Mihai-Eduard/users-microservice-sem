package nl.tudelft.sem.template.example.integration.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;
import org.junit.jupiter.api.Test;

public class UserEntityTest {

    @Test
    public void bannedTypeTest() {
        BannedType bannedTypeTrue = new BannedType(true);
        BannedType bannedTypeFalse = new BannedType(false);

        assertTrue(bannedTypeTrue.isBanned());
        assertFalse(bannedTypeFalse.isBanned());
    }

    @Test
    public void testDetailType() {
        assertThrows(IllegalArgumentException.class, () -> {
            DetailType detailType = new DetailType("", "Name", "Location", 0L, List.of());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            DetailType detailType = new DetailType("Bio", "", "Location", 0L, List.of());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            DetailType detailType = new DetailType("Bio", "Name", "", 0L, List.of());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            DetailType detailType = new DetailType("Bio", "Name", "Location", -1L, List.of());
        });

        DetailType detailType = new DetailType("Bio", "Name", "Location", 1L, List.of("Horror", "History"));

        assertEquals("Bio", detailType.getBio());
        assertEquals("Name", detailType.getName());
        assertEquals("Location", detailType.getLocation());
        assertEquals(1L, detailType.getFavouriteBookId());
        assertEquals("Horror", detailType.getFavouriteGenres().get(0));
    }

    @Test
    public void testEmailType() {
        assertThrows(IllegalArgumentException.class, () -> {
            EmailType emailType = new EmailType("");
        });

        EmailType emailType = new EmailType("example@gmail.com");

        assertEquals("example@gmail.com", emailType.getEmail());
    }

    @Test
    public void testPasswordType() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordType passwordType = new PasswordType("");
        });

        PasswordType passwordType = new PasswordType("MyPass123");

        assertEquals("MyPass123", passwordType.getPassword());
    }

    @Test
    public void testUsernameType() {
        assertThrows(IllegalArgumentException.class, () -> {
            UsernameType usernameType = new UsernameType("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            UsernameType usernameType = new UsernameType(" ");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            UsernameType usernameType = new UsernameType("!");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            UsernameType usernameType = new UsernameType("He..0");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            UsernameType usernameType = new UsernameType("Hell+");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            UsernameType usernameType = new UsernameType("1User");
        });

        UsernameType usernameType = new UsernameType("HelloWorld01");

        assertEquals("HelloWorld01", usernameType.getUsername());

        UsernameType usernameType1 = new UsernameType("U");

        assertEquals("U", usernameType1.getUsername());
    }
}
