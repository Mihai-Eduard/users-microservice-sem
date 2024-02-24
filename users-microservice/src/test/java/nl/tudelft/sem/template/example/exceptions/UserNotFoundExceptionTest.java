package nl.tudelft.sem.template.example.exceptions;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


class UserNotFoundExceptionTest {
    @Test
    void defaultConstructor_MessageIsUserNotFound() {
        UserNotFoundException exception = new UserNotFoundException();

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void userIdConstructor_MessageWithUserId() {
        Long userId = 123L;
        UserNotFoundException exception = new UserNotFoundException(userId);

        assertEquals("User not found with ID: " + userId, exception.getMessage());
    }

    @Test
    void messageConstructor_CustomMessage() {
        String customMsg = "Custom message";
        UserNotFoundException exception = new UserNotFoundException(customMsg);

        assertEquals(customMsg, exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor_CustomMessageAndCause() {
        String customMsg = "Custom message";
        Throwable cause = new RuntimeException("Some cause");

        UserNotFoundException exception = new UserNotFoundException(customMsg, cause);

        assertEquals(customMsg, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

}