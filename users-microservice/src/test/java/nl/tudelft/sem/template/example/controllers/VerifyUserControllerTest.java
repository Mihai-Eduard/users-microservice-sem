package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.dtos.UserResponse;
import nl.tudelft.sem.template.example.dtos.VerifyResponse;
import nl.tudelft.sem.template.example.dtos.generic.DoesNotExistResponse404;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class VerifyUserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private VerifyUserController verifyUserController;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        verifyUserController = new VerifyUserController(userService);
    }

    @Test
    void verifyUserRole1() {
        long userId = 1L;
        User user = new User();
        user.setRole(new UserEnumType("USER"));

        when(userService.getUserById(anyLong())).thenReturn(new UserResponse(user));

        ResponseEntity<VerifyResponse> responseEntity = verifyUserController.verifyUserRole(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(new VerifyResponse("USER"), responseEntity.getBody());
    }

    @Test
    void verifyUserRole2() {
        long userId = 1L;
        User user = new User();
        user.setRole(new UserEnumType("ADMIN"));

        when(userService.getUserById(anyLong())).thenReturn(new UserResponse(user));

        ResponseEntity<VerifyResponse> responseEntity = verifyUserController.verifyUserRole(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(new VerifyResponse("ADMIN"), responseEntity.getBody());
    }

    @Test
    void verifyUserRole3() {
        long userId = 1L;
        User user = new User();
        user.setRole(new UserEnumType("AUTHOR"));

        when(userService.getUserById(anyLong())).thenReturn(new UserResponse(user));

        ResponseEntity<VerifyResponse> responseEntity = verifyUserController.verifyUserRole(userId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(new VerifyResponse("AUTHOR"), responseEntity.getBody());
    }

    @Test
    void verifyUserRole4() {
        long userId = 1L;

        when(userService.getUserById(anyLong())).thenThrow(new RuntimeException("Some error"));

        ResponseEntity<VerifyResponse> responseEntity = verifyUserController.verifyUserRole(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(new VerifyResponse("Error retrieving user role"), responseEntity.getBody());
    }

    @Test
    void verifyUserRole5() {
        long userId = 1L;
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> {
            user.setRole(new UserEnumType("INVALID_ROLE"));

            when(userService.getUserById(anyLong())).thenReturn(new UserResponse(user));

            ResponseEntity<VerifyResponse> responseEntity = verifyUserController.verifyUserRole(userId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
            assertEquals(new VerifyResponse("Invalid user role"), responseEntity.getBody());
        });
    }

    @Test
    void verifyUserRole6() {
        long userId = 1L;

        when(userService.getUserById(anyLong())).thenReturn(new DoesNotExistResponse404());

        verifyUserController.verifyUserRole(userId);
    }

    @Test
    void verifyUserRole7() {
        long userId = 1L;

        when(userService.getUserById(anyLong())).thenThrow(new RuntimeException("Some error"));

        ResponseEntity<VerifyResponse> responseEntity = verifyUserController.verifyUserRole(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(new VerifyResponse("Error retrieving user role"), responseEntity.getBody());
    }

}