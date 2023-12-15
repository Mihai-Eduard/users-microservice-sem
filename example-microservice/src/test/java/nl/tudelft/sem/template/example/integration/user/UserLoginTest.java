package nl.tudelft.sem.template.example.integration.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import nl.tudelft.sem.template.example.controllers.UserController;
import nl.tudelft.sem.template.example.dtos.LoginUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserResponse;
import nl.tudelft.sem.template.example.dtos.UserRoleResponse;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;
import nl.tudelft.sem.template.example.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class UserLoginTest {
    UserController controller;
    @Mock
    UserService service;

    @Captor
    ArgumentCaptor<LoginUserRequest> captor;

    @Test
    public void loginAndReturns200AllCorrect() {
        // set up controller
        controller = new UserController(service);

        LoginUserRequest request = new LoginUserRequest(
            "my2CoolUsername1",
            "strongPassword123!"
        );

        final User user = new User(
                123L,
                new UsernameType("my2CoolUsername1"),
                new EmailType("email@example.com"),
                new PasswordType("strongPassword123!"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType("bio", "name", "location", 1L, new ArrayList<>()),
                new FollowingType(new ArrayList<>())
        );

        // mock service to return null when username disallowed
        when(service.loginUser(request))
                .thenAnswer(invocation -> user);

        ResponseEntity<?> httpResponse = controller.loginUser(request);

        // check if service passed correct DTO
        verify(service, times(1)).loginUser(captor.capture());


        assertEquals(captor.getValue(), request);

        RegisterUserResponse expected = new RegisterUserResponse(123L);

        assertEquals(ResponseEntity.ok(expected), httpResponse);
    }

    @Test
    public void loginWithIncorrectCredentialsOrNoUser() {
        // set up controller
        controller = new UserController(service);

        LoginUserRequest request = new LoginUserRequest(
                "my2CoolUsername1",
                "wrongPassword123!"
        );

        // mock service to return null when username disallowed
        when(service.loginUser(request))
                .thenAnswer(invocation -> null);

        ResponseEntity<?> httpResponse = controller.loginUser(request);

        // check if service passed correct DTO
        verify(service, times(1)).loginUser(captor.capture());


        assertEquals(captor.getValue(), request);

        assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Incorrect password and/or email address"),
                httpResponse);
    }

    @Test
    public void loginBannedUser() {
        // set up controller
        controller = new UserController(service);

        LoginUserRequest request = new LoginUserRequest(
                "my2CoolUsername1",
                "strongPassword123!"
        );

        final User user = new User(
                123L,
                new UsernameType("my2CoolUsername1"),
                new EmailType("email@example.com"),
                new PasswordType("strongPassword123!"),
                new BannedType(true), // USER IS BANNED
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType("bio", "name", "location", 1L, new ArrayList<>()),
                new FollowingType(new ArrayList<>())
        );

        // mock service to return null when username disallowed
        when(service.loginUser(request))
                .thenAnswer(invocation -> user);

        ResponseEntity<?> httpResponse = controller.loginUser(request);

        // check if service passed correct DTO
        verify(service, times(1)).loginUser(captor.capture());


        assertEquals(captor.getValue(), request);

        final UserRoleResponse role = new UserRoleResponse("USER_BANNED");
        final var expected = ResponseEntity.status(HttpStatus.FORBIDDEN).body(role);

        assertEquals(expected, httpResponse);
    }
}
