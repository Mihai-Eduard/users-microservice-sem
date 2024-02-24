package nl.tudelft.sem.template.example.exceptions;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


public class UserBannedExceptionTest {

    @Test
    void defaultConstructor_MessageIsUserBanned() {
        UserBannedException exception = new UserBannedException();

        assertEquals("User is banned", exception.getMessage());
    }

    @Test
    void messageConstructor_CustomMessage() {
        String customMsg = "message";
        UserBannedException exception = new UserBannedException(customMsg);

        assertEquals(customMsg, exception.getMessage());
    }

    @Test
    void messageAndCauseConstructor_CustomMessageAndCause() {
        String customMsg = "Msg";
        Throwable cause = new RuntimeException("Some cause");

        UserBannedException exception = new UserBannedException(customMsg, cause);

        assertEquals(customMsg, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void userIdConstructor_MessageWithUserId() {
        Long userId = 123L;
        UserBannedException exception = new UserBannedException(userId);

        assertEquals("User banned with ID: " + userId, exception.getMessage());
    }

    @Test
    void causeConstructor_CauseIsSet() {
        Throwable cause = new RuntimeException("Test test");
        UserBannedException exception = new UserBannedException(cause);

        assertEquals(cause, exception.getCause());
    }
}
