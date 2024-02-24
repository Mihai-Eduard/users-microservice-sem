package nl.tudelft.sem.template.example.integration.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import nl.tudelft.sem.template.example.controllers.UserController;
import nl.tudelft.sem.template.example.dtos.PrivacySettingResponse;
import nl.tudelft.sem.template.example.dtos.RegisterUserRequest;
import nl.tudelft.sem.template.example.dtos.UserIdResponse;
import nl.tudelft.sem.template.example.dtos.UserProfileRequest;
import nl.tudelft.sem.template.example.dtos.UserResponse;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.generic.DoesNotExistResponse404;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.dtos.generic.InternalServerErrorResponse;
import nl.tudelft.sem.template.example.dtos.generic.UserBannedResponse;
import nl.tudelft.sem.template.example.dtos.generic.UserNotFoundResponse;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse200;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse403;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse404;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest

public class UserControllerTest {
    UserController controller;
    @Mock
    UserService service;
    @Captor
    ArgumentCaptor<RegisterUserRequest> captor;

    @BeforeEach
    void setup() {
        // set up controller
        controller = new UserController(service);
    }

    @Test
    public void changePrivacySettingsAndReturns200AllCorrect() {
        when(service.changeUserPrivacySettings(123L))
                .thenAnswer(
                        invocation -> new PrivacySettingResponse(false));

        ResponseEntity<GenericResponse> httpResponse = controller.changeUserPrivacySettings(123L);

        assertEquals(httpResponse, ResponseEntity.ok(new PrivacySettingResponse(false)));
    }

    @Test
    public void changePrivacySettingsAndUserNotFound() {
        when(service.changeUserPrivacySettings(123L))
                .thenAnswer(
                        invocation -> new UserNotFoundResponse());

        ResponseEntity<GenericResponse> httpResponse = controller.changeUserPrivacySettings(123L);

        assertEquals(httpResponse, ResponseEntity.notFound().build());
    }

    @Test
    public void changePrivacySettingsAndUserBanned() {
        when(service.changeUserPrivacySettings(123L))
                .thenAnswer(
                        invocation -> new UserBannedResponse());

        ResponseEntity<GenericResponse> httpResponse = controller.changeUserPrivacySettings(123L);

        final UserStatusResponse role = new UserStatusResponse("USER_BANNED");
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.FORBIDDEN).body(role));
    }

    @Test
    public void changePrivacySettingsAndInternalServerError() {
        when(service.changeUserPrivacySettings(123L))
                .thenAnswer(
                        invocation -> new InternalServerErrorResponse());

        ResponseEntity<GenericResponse> httpResponse = controller.changeUserPrivacySettings(123L);

        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @Test
    public void editProfileAndReturns200AllCorrect() {
        UserProfileRequest request = new UserProfileRequest(
                "newName",
                "newBio",
                "newLocation",
                123L,
                "base64",
                List.of("genre1", "genre2"));

        when(service.editUserProfile(request, 123L))
                .thenAnswer(
                        invocation -> new UserIdResponse(123L));

        ResponseEntity<GenericResponse> httpResponse = controller.changeProfile(request, 123L);

        assertEquals(httpResponse, ResponseEntity.ok(new UserIdResponse(123L)));
    }

    @Test
    public void editProfileAndUserNotFound() {
        UserProfileRequest request = new UserProfileRequest(
                "newName",
                "newBio",
                "newLocation",
                123L,
                "base64",
                List.of("genre1", "genre2"));

        when(service.editUserProfile(request, 123L))
                .thenAnswer(
                        invocation -> new UserNotFoundResponse());

        ResponseEntity<GenericResponse> httpResponse = controller.changeProfile(request, 123L);

        assertEquals(httpResponse, ResponseEntity.notFound().build());
    }

    @Test
    public void editProfileAndUserBanned() {
        UserProfileRequest request = new UserProfileRequest(
                "newName",
                "newBio",
                "newLocation",
                123L,
                "base64",
                List.of("genre1", "genre2"));

        when(service.editUserProfile(request, 123L))
                .thenAnswer(
                        invocation -> new UserBannedResponse());

        ResponseEntity<GenericResponse> httpResponse = controller.changeProfile(request, 123L);

        final UserStatusResponse role = new UserStatusResponse("USER_BANNED");
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.FORBIDDEN).body(role));
    }

    @Test
    public void editProfileAndInternalServerError() {
        UserProfileRequest request = new UserProfileRequest(
                "newName",
                "newBio",
                "newLocation",
                123L,
                "base64",
                List.of("genre1", "genre2"));

        when(service.editUserProfile(request, 123L))
                .thenAnswer(
                        invocation -> new InternalServerErrorResponse());

        ResponseEntity<GenericResponse> httpResponse = controller.changeProfile(request, 123L);

        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @Test
    public void editProfileAndBadRequest() {
        UserProfileRequest request = new UserProfileRequest(
                "newName",
                "newBio",
                "newLocation",
                123L,
                "base64",
                List.of("genre1", "genre2"));

        when(service.editUserProfile(request, 123L))
                .thenThrow(new IllegalArgumentException());

        ResponseEntity<GenericResponse> httpResponse = controller.changeProfile(request, 123L);

        assertEquals(httpResponse, ResponseEntity.badRequest().build());
    }

    @Test
    public void callsRegisterAndReturns200AllCorrect() {
        // use sample DTO
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(
            "test@sample.com",
            "strongPassword123!",
            "my2CoolUsername1"
        );
        // mock service to return the passed data
        when(service.registerUser(any(RegisterUserRequest.class)))
            .thenAnswer(
                invocation -> new UserIdResponse(123L));
        // and call controller
        ResponseEntity<UserIdResponse> httpResponse = controller.registerNewUser(registerUserRequest);
        verify(service, times(1)).registerUser(captor.capture());
        // check if service passed correct DTO
        assertEquals(captor.getValue(), registerUserRequest);
        // check if service returned correct response
        UserIdResponse expected = new UserIdResponse(123L);
        assertEquals(httpResponse, ResponseEntity.ok(expected));
    }

    @Test
    public void callsRegisterWithDisallowedUsernameAndReturns400BadRequest() {
        // use sample DTO
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(
            "test@sample.com",
            "strongPassword123!",
            "1WrongUsername"
        );
        // mock service to return null when username disallowed
        when(service.registerUser(registerUserRequest))
            .thenAnswer(invocation -> null);
        ResponseEntity<UserIdResponse> httpResponse = controller.registerNewUser(registerUserRequest);
        // check if service passed correct DTO
        verify(service, times(1)).registerUser(captor.capture());
        assertEquals(captor.getValue(), registerUserRequest);
        // check if service returned correct response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Test
    public void callsRegisterWithEmptyPasswordAndReturns400BadRequest() {
        // use sample DTO
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(
            "testingEmpty@sample.com",
            "",
            "correctUsername"
        );
        // mock service to return null when password empty (tested in User Service)
        when(service.registerUser(registerUserRequest))
            .thenAnswer(invocation -> null);
        ResponseEntity<UserIdResponse> httpResponse = controller.registerNewUser(registerUserRequest);
        // check if service passed correct DTO
        verify(service, times(1)).registerUser(captor.capture());
        assertEquals(captor.getValue(), registerUserRequest);
        // check if service returned correct response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Test
    public void callsRegisterWithEmptyEmailAndReturns400BadRequest() {
        // use sample DTO
        RegisterUserRequest registerUserRequest = new RegisterUserRequest(
            "",
            "strongPassword123!",
            "correctUsername"
        );
        // mock service to return null when email empty (tested in User Service)
        when(service.registerUser(registerUserRequest))
            .thenAnswer(invocation -> null);
        ResponseEntity<UserIdResponse> httpResponse = controller.registerNewUser(registerUserRequest);
        // check if service passed correct DTO
        verify(service, times(1)).registerUser(captor.capture());
        assertEquals(captor.getValue(), registerUserRequest);
        // check if service returned correct response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Test
    public void callsChangePasswordAndReturns200Ok() {
        // mock the service to accept request
        when(service.changeUserPassword(any(String.class), any(Long.class))).thenReturn(new ChangePasswordResponse200());
        // call the endpoint method
        ResponseEntity<GenericResponse> httpResponse = controller.changePassword("newPassword", 123L);
        // verify that returns correct HTTP response
        assertEquals(httpResponse, ResponseEntity.ok().build());
    }

    @Test
    public void callsChangePasswordAndReturns404ForMissingUser() {
        // mock the service to accept request
        when(service.changeUserPassword(any(String.class), any(Long.class))).thenReturn(new ChangePasswordResponse404());
        // call the endpoint method
        ResponseEntity<GenericResponse> httpResponse = controller.changePassword("newPassword", 123L);
        // verify that returns correct HTTP response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Test
    public void callsChangePasswordAndReturns403ForBannedUser() {
        // response that will be returned by the service
        GenericResponse service403 = new ChangePasswordResponse403("USER_BANNED");
        // mock the service to accept request
        when(service.changeUserPassword(any(String.class), any(Long.class))).thenReturn(service403);
        // call the endpoint method
        ResponseEntity<GenericResponse> httpResponse = controller.changePassword("newPassword", 123L);
        // verify that returns correct HTTP response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.FORBIDDEN).body(service403));
    }

    @Test
    public void callsChangePasswordAndReturns500ForInternalError() {
        // mock the service to accept request
        when(service.changeUserPassword(any(String.class), any(Long.class))).thenReturn(new InternalServerErrorResponse());
        // call the endpoint method
        ResponseEntity<GenericResponse> httpResponse = controller.changePassword("newPassword", 123L);
        // verify that returns correct HTTP response
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @Test
    public void callsGetUserAndReturns404IfUserDoesNotExist() {
        // mock the service to return 404
        when(service.getUserById(any(Long.class))).thenReturn(new DoesNotExistResponse404());
        // and call the endpoint
        ResponseEntity<User> httpResponse = controller.getUserById(123L, 123L);
        // and check if 404 returned
        assertEquals(httpResponse, ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Test
    public void callsGetUserAndReturnsUserIfUserExists() {
        // mock the response of the service (containing the user)
        User expected = new User(
            new UsernameType("correctUname1"),
            new EmailType("example@foo.com"),
            new PasswordType("HashedPassword-newPassword"),
            new BannedType(false),
            new PrivacyType(false),
            new UserEnumType("USER"),
            new DetailType(),
            new FollowingType()   // no followers
        );
        when(service.getUserById(123L)).thenReturn(new UserResponse(expected));
        // call the endpoint
        ResponseEntity<User> httpResponse = controller.getUserById(123L, 123L);
        // and check if response with user returned
        assertEquals(httpResponse, ResponseEntity.ok(expected));
    }

    @Test
    void test2() {
        when(service.getUserById(anyLong())).thenReturn(new DoesNotExistResponse404());

        ResponseEntity<User> result = controller.getUserById(123L, 123L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() == null);
        verify(service, times(1)).getUserById(123L);
    }

    @Test
    void test1() {
        User expectedUser = new User();
        UserResponse userResponse = new UserResponse(expectedUser);
        when(service.getUserById(123L)).thenReturn(userResponse);

        ResponseEntity<User> result = controller.getUserById(123L, 123L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedUser, result.getBody());
        verify(service, times(2)).getUserById(123L);
    }

    @Test
    void test() {
        when(service.getUserById(anyLong())).thenReturn(new DoesNotExistResponse404());

        ResponseEntity<User> result = controller.getUserById(123L, 123L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertTrue(result.getBody() == null);
        verify(service, times(1)).getUserById(123L);
    }

    @Test
    void test3() {
        User expectedUser = new User();
        UserResponse userResponse = new UserResponse(expectedUser);
        when(service.getUserById(123L)).thenReturn(userResponse);

        ResponseEntity<User> result = controller.getUserById(123L, 123L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedUser, result.getBody());
        verify(service, times(2)).getUserById(123L);
    }

    @Test
    void test4() {
        User u1 = new User(new UsernameType("admin"),
                new EmailType("email"),
                new PasswordType("password"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("ADMIN"),
                new DetailType(),
                new FollowingType());
        User u2 = new User(new UsernameType("user1"),
                new EmailType("email1"),
                new PasswordType("password3"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType());
        User u3 = new User(new UsernameType("user2"),
                new EmailType("email1"),
                new PasswordType("password"),
                new BannedType(true),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType());
        UserBannedResponse userResponse = new UserBannedResponse();
        u3.setUserId(123L);
        when(service.getUserById(123L)).thenReturn(userResponse);

        ResponseEntity<User> result = controller.getUserById(123L, 123L);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        verify(service, times(1)).getUserById(123L);
    }

    @Test
    void test5() {
        User u1 = new User(new UsernameType("admin"),
                new EmailType("email"),
                new PasswordType("password"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("ADMIN"),
                new DetailType(),
                new FollowingType());
        User u2 = new User(new UsernameType("user1"),
                new EmailType("email1"),
                new PasswordType("password3"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType());
        User u3 = new User(new UsernameType("user2"),
                new EmailType("email1"),
                new PasswordType("password"),
                new BannedType(true),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType());
        DoesNotExistResponse404 userResponse = new DoesNotExistResponse404();
        u3.setUserId(123L);
        when(service.getUserById(123L)).thenReturn(userResponse);

        ResponseEntity<User> result = controller.getUserById(123L, 123L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(service, times(1)).getUserById(123L);
    }

}
