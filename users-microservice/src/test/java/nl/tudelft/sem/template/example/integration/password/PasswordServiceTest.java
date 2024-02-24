package nl.tudelft.sem.template.example.integration.password;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import nl.tudelft.sem.template.example.services.PasswordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class PasswordServiceTest {
    @Autowired
    PasswordService passwordService;

    @Test
    public void correctlyDecodesEncodedPassword() {
        String rawPassword = "pass1233!_abc:/d'\"ui";
        String encPassword = passwordService.passwordEncoder().encode(rawPassword);
        boolean matches = passwordService.passwordEncoder().matches(rawPassword, encPassword);

        assertTrue(matches);
    }

    @Test
    public void doesNotMatchWrongPassword() {
        String rawPassword = "pass1233!_abc:/d'\"ui";
        String otherPassword = "aPassword here";
        String encPassword = passwordService.passwordEncoder().encode(rawPassword);
        boolean matches = passwordService.passwordEncoder().matches(otherPassword, encPassword);

        assertFalse(matches);
    }

    @Test
    public void usesBCryptForPasswordHashing() {
        assertTrue(passwordService.passwordEncoder() instanceof BCryptPasswordEncoder);
    }
}
