package nl.tudelft.sem.template.example.integration.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.dtos.LoginUserRequest;
import nl.tudelft.sem.template.example.dtos.PrivacySettingResponse;
import nl.tudelft.sem.template.example.dtos.RegisterUserRequest;
import nl.tudelft.sem.template.example.dtos.UserIdResponse;
import nl.tudelft.sem.template.example.dtos.UserProfileRequest;
import nl.tudelft.sem.template.example.dtos.UserResponse;
import nl.tudelft.sem.template.example.dtos.generic.DoesNotExistResponse404;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.dtos.generic.InternalServerErrorResponse;
import nl.tudelft.sem.template.example.dtos.generic.UserBannedResponse;
import nl.tudelft.sem.template.example.dtos.generic.UserNotFoundResponse;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse200;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse403;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse404;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.AnalyticsService;
import nl.tudelft.sem.template.example.services.PasswordService;
import nl.tudelft.sem.template.example.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mockUserRepository")
@AutoConfigureMockMvc
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    UserService service;
    @Mock
    PasswordService passwordService;

    @Mock
    AnalyticsService analyticsService;

    @Mock
    PasswordEncoder encoder;
    @Captor
    ArgumentCaptor<User> userCaptor;
    @Captor
    ArgumentCaptor<String> passwordCaptor;

    /**
     * Before each test, set up the mocks and service.
     */
    @BeforeEach
    public void setup() {
        when(passwordService.passwordEncoder()).thenReturn(encoder);
        // set up service
        service = new UserService(userRepository, passwordService);
    }

    @Test
    public void changePrivacySettingsTestOk() {
        // fake user
        User found = new User(
                new UsernameType("correctUname1"),
                new EmailType("example@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType()   // no followers
        );

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(found));


        GenericResponse response = service.changeUserPrivacySettings(1L);
        verify(userRepository).save(userCaptor.capture());

        User captured = userCaptor.getValue();
        assertTrue(captured.getPrivacy().isEnableCollection());

        assertEquals(new PrivacySettingResponse(true), response);
    }

    @Test
    public void changePrivacySettingsTestUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        GenericResponse response = service.changeUserPrivacySettings(1L);

        verify(userRepository, never()).save(any(User.class));

        assertThat(response).isInstanceOf(UserNotFoundResponse.class);
    }

    @Test
    public void changePrivacySettingsTestInternalServerError() {
        when(userRepository.existsById(1L)).thenThrow(RuntimeException.class);

        GenericResponse response = service.changeUserPrivacySettings(1L);

        verify(userRepository, never()).save(any(User.class));

        assertThat(response).isInstanceOf(InternalServerErrorResponse.class);
    }

    @Test
    public void changePrivacySettingsPurgeDataTest() {
        // fake user
        User found = new User(
                new UsernameType("correctUname1"),
                new EmailType("example@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(false),
                new PrivacyType(true),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType()   // no followers
        );

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(found));

        UserService serviceWithAnalytics = new UserService(userRepository, passwordService, analyticsService);

        GenericResponse response = serviceWithAnalytics.changeUserPrivacySettings(1L);
        assertEquals(new PrivacySettingResponse(false), response);

        verify(userRepository).save(userCaptor.capture());
        verify(analyticsService).purgeUserData(1L);

        User captured = userCaptor.getValue();
        assertFalse(captured.getPrivacy().isEnableCollection());
    }

    @Test
    public void changePrivacySettingsTestUserBanned() {
        // fake user
        User found = new User(
                new UsernameType("correctUname1"),
                new EmailType("example@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(true),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType()   // no followers
        );

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(found));

        GenericResponse response = service.changeUserPrivacySettings(1L);

        verify(userRepository, never()).save(any(User.class));

        assertThat(response).isInstanceOf(UserBannedResponse.class);
    }

    @Test
    public void loginUserTestCorrectCredentials() {

        when(passwordService.passwordEncoder().matches(any(String.class), any(String.class)))
            .thenReturn(true);

        User expected = new User(
                new UsernameType("correctUname1"),
                new EmailType("example@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType()   // no followers
        );

        when(userRepository.findUserByEmail(any(EmailType.class)))
                .thenReturn(expected);

        service = new UserService(userRepository, passwordService);

        LoginUserRequest request = new LoginUserRequest("example@foo.com", "0xHashedPasswordx0");

        User found = service.loginUser(request);

        assertEquals(expected, found);
    }

    @Test
    public void loginUserTestIncorrectCredentials() {
        when(passwordService.passwordEncoder().matches(any(String.class), any(String.class)))
                .thenReturn(false);

        User expected = new User(
                new UsernameType("correctUname1"),
                new EmailType("example@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType()   // no followers
        );

        when(userRepository.findUserByEmail(any(EmailType.class)))
                .thenReturn(expected);

        service = new UserService(userRepository, passwordService);

        LoginUserRequest request = new LoginUserRequest("example@foo.com", "wrong-password");

        User found = service.loginUser(request);

        assertNull(found);
    }

    @Test
    public void loginUserTestInvalidEmail() {

        service = new UserService(userRepository, passwordService);

        LoginUserRequest request = new LoginUserRequest("", "wrong-password");

        User found = service.loginUser(request);

        assertNull(found);
    }

    @Test
    public void loginUserTestNoUser() {
        when(userRepository.findUserByEmail(any(EmailType.class)))
                .thenReturn(null);

        service = new UserService(userRepository, passwordService);

        LoginUserRequest request = new LoginUserRequest("example@foo.com", "wrong-password");

        User found = service.loginUser(request);

        assertNull(found);
    }

    @Test
    public void editUserProfileTestOk() {
        UserProfileRequest request = new UserProfileRequest(
                "newName",
                "newBio",
                "newLocation",
                123L,
                "base64",
                List.of("genre1", "genre2"));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // fake user
        User found = new User(
                new UsernameType("correctUname1"),
                new EmailType("example@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType()   // no followers
        );

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(found));

        GenericResponse response = service.editUserProfile(request, 1L);

        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();

        User expected = new User(
                new UsernameType("correctUname1"),
                new EmailType("example@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType("newBio",
                        "newName",
                        "newLocation",
                        123L,
                        List.of("genre1", "genre2")),
                new FollowingType()   // no followers
        );

        assertEquals(expected, captured);
        assertEquals(new UserIdResponse(found.getUserId()), response);
    }

    @Test
    public void editUserProfileTestUserNotFound() {
        UserProfileRequest request = new UserProfileRequest(
                "newName",
                "newBio",
                "newLocation",
                123L,
                "base64",
                List.of("genre1", "genre2"));

        when(userRepository.existsById(1L)).thenReturn(false);

        GenericResponse response = service.editUserProfile(request, 1L);

        verify(userRepository, never()).save(any(User.class));

        assertThat(response).isInstanceOf(UserNotFoundResponse.class);
    }

    @Test
    public void editUserProfileTestUserBanned() {
        UserProfileRequest request = new UserProfileRequest(
                "newName",
                "newBio",
                "newLocation",
                123L,
                "base64",
                List.of("genre1", "genre2"));

        // fake user
        User found = new User(
                new UsernameType("correctUname1"),
                new EmailType("example@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(true), // BANNED
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType()   // no followers
        );

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(found));

        GenericResponse response = service.editUserProfile(request, 1L);

        verify(userRepository, never()).save(any(User.class));

        assertThat(response).isInstanceOf(UserBannedResponse.class);
    }

    @Test
    public void editUserProfileTestInternalServerError() {
        when(userRepository.existsById(anyLong())).thenThrow(new RuntimeException());
        UserProfileRequest request = new UserProfileRequest(
                "newName",
                "newBio",
                "newLocation",
                123L,
                "base64",
                List.of("genre1", "genre2"));

        GenericResponse response = service.editUserProfile(request, 1L);

        assertEquals(InternalServerErrorResponse.class, response.getClass());
    }

    @Test
    public void registerUserTestCorrectUnameCorrectEmailCheckDb() {
        // set up mock DB
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        // and set up mock password hashing service
        when(passwordService.passwordEncoder().encode(any(String.class)))
            .thenReturn("0xHashedPasswordx0");

        // provide sample DTO
        RegisterUserRequest registrationReq = new RegisterUserRequest("example@foo.com", "unhashedPW", "correctUname1");

        // call the registration method
        service.registerUser(registrationReq);
        verify(userRepository).save(userCaptor.capture());
        User captured = userCaptor.getValue();

        // expected User
        User expected = new User(
            new UsernameType("correctUname1"),
            new EmailType("example@foo.com"),
            new PasswordType("0xHashedPasswordx0"),
            new BannedType(false),
            new PrivacyType(false),
            new UserEnumType("USER"),
            new DetailType(),
            new FollowingType()   // no followers
        );

        assertEquals(expected, captured);
    }

    @Test
    public void registerUserTestCorrectUnameCorrectEmailCheckResponseOneUser() {
        // set up mock DB
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        // and set up mock password hashing service
        when(passwordService.passwordEncoder().encode(any(String.class)))
            .thenReturn("0xHashedPasswordx0");
        // provide sample DTO
        RegisterUserRequest registrationReq = new RegisterUserRequest("example@foo.com", "unhashedPW", "correctUname1");
        // call the registration method
        UserIdResponse response = service.registerUser(registrationReq);
        assertEquals(response, new UserIdResponse(0));
    }

    @Test
    public void registerUserTestCorrectUnameCorrectEmailCheckResponseMultipleUsers() {
        // set up mock DB
        // assume that new user is _assigned ID>0_
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> (mockHelper((User) invocation.getArgument(0), 123L)));
        // and set up mock password hashing service
        when(passwordService.passwordEncoder().encode(any(String.class)))
            .thenReturn("0xHashedPasswordx0");
        // provide sample DTO
        RegisterUserRequest registrationReq = new RegisterUserRequest("example@foo.com", "unhashedPW", "correctUname1");
        // check if response is correct
        UserIdResponse response = service.registerUser(registrationReq);
        assertEquals(response, new UserIdResponse(123));
    }

    @Test
    public void returnsNullIfIncorrectUsernameAndDoesNotSaveToDb() {
        // set up mock DB
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        // and set up mock password hashing service
        when(passwordService.passwordEncoder().encode(any(String.class)))
            .thenReturn("0xHashedPasswordx0");
        // provide sample DTO
        RegisterUserRequest registrationReq = new RegisterUserRequest("example@foo.com", "unhashedPW", "1wrongUname");
        // call the registration method
        UserIdResponse response = service.registerUser(registrationReq);
        // verify that DB never called and that response given is null
        verify(userRepository, never()).save(any(User.class));
        assertNull(response);
    }

    @Test
    public void returnsNullIfUserWithEmailExistsAndDoesNotSaveToDb() {
        RegisterUserRequest registrationReq = new RegisterUserRequest("example@foo.com", "unhashedPW", "correctUname1");
        // set up mock DB
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findUserByEmail(new EmailType(registrationReq.getEmail())))
            .thenReturn(new User(
                new UsernameType(registrationReq.getUsername()),
                new EmailType(registrationReq.getEmail()),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(false),
                new PrivacyType(),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType()   // no followers
            ));
        // and set up mock password hashing service
        when(passwordService.passwordEncoder().encode(any(String.class)))
            .thenReturn("0xHashedPasswordx0");
        // provide sample DTO
        // call the registration method
        UserIdResponse response = service.registerUser(registrationReq);
        // verify that DB never called and that response given is null
        verify(userRepository, never()).save(any(User.class));
        assertNull(response);
    }

    @Test
    public void verifyCallsHashDuringRegistration() {
        // set up mock DB
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        // and set up mock password hashing service
        when(passwordService.passwordEncoder().encode(any(String.class)))
            .thenReturn("0xHashedPasswordx0");
        // call the service
        RegisterUserRequest registrationReq = new RegisterUserRequest("example@foo.com", "unhashedPW", "correctUname1");
        service.registerUser(registrationReq);

        // create an argument captor to check arguments
        verify(encoder, times(1)).encode(passwordCaptor.capture());
        assertEquals(passwordCaptor.getValue(), "unhashedPW");
    }

    @Test
    public void returnsNullIfEmptyPasswordProvidedInRegistration() {
        // set up mock DB
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        // and set up mock password hashing service
        when(passwordService.passwordEncoder().encode(any(String.class)))
            .thenReturn("0xHashedPasswordx0");
        // call the service with empty password
        RegisterUserRequest registrationReq = new RegisterUserRequest("example@foo.com", "", "correctUname");
        assertNull(service.registerUser(registrationReq));
    }

    @Test
    public void returnsNullIfEmptyEmailProvidedInRegistration() {
        // set up mock DB
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        // and set up mock password hashing service
        when(passwordService.passwordEncoder().encode(any(String.class)))
            .thenReturn("0xHashedPasswordx0");

        // call the service with empty email
        RegisterUserRequest registrationReq = new RegisterUserRequest("", "correctPassword", "correctUname");
        assertNull(service.registerUser(registrationReq));
    }

    @Test
    public void returnsNullAndCatchesSimulatedExceptionInRegistration() {
        // set up mock DB to be faulty!
        when(userRepository.save(any(User.class)))
            .thenThrow(DataAccessResourceFailureException.class);
        // and set up mock password hashing service
        when(passwordService.passwordEncoder().encode(any(String.class)))
            .thenReturn("0xHashedPasswordx0");
        // provide sample DTO
        RegisterUserRequest registrationReq = new RegisterUserRequest("example@foo.com", "unhashedPW", "correctUname1");
        // assert that exception is caught
        assertDoesNotThrow(() -> service.registerUser(registrationReq));
        // call the faulty service & check if null returned
        UserIdResponse response = service.registerUser(registrationReq);
        assertNull(response);
    }

    @Test
    public void returns404ResponseIfUserDoesNotExistInPasswordChange() {
        // mock the DB to signify that user does not exist
        when(userRepository.existsById(123L)).thenReturn(false);
        // check if correct status returned
        GenericResponse response = service.changeUserPassword("testString", 123L);
        assertEquals(response, new ChangePasswordResponse404());
    }

    @Test
    public void returns403ResponseIfUserBannedInPasswordChange() {
        // set up user entity used for testing
        User testUser = new User(
            new UsernameType("correctUname1"),
            new EmailType("example@foo.com"),
            new PasswordType("0xHashedPasswordx0"),
            new BannedType(true), // BANNED
            new PrivacyType(false),
            new UserEnumType("USER"),
            new DetailType(),
            new FollowingType()   // no followers
        );
        // mock the DB: user exists & can be returned
        when(userRepository.existsById(123L)).thenReturn(true);
        when(userRepository.findById(123L)).thenReturn(Optional.of(testUser));
        // check if correct status returned
        GenericResponse response = service.changeUserPassword("testString", 123L);
        assertEquals(response, new ChangePasswordResponse403("USER_BANNED"));
    }

    @Test
    public void returns200ResponseIfUserCorrectAndPasswordUpdated() {
        // set up user entity used for testing
        User testUser = new User(
            new UsernameType("correctUname1"),
            new EmailType("example@foo.com"),
            new PasswordType("0xHashedPasswordx0"),
            new BannedType(false),
            new PrivacyType(false),
            new UserEnumType("USER"),
            new DetailType(),
            new FollowingType()   // no followers
        );
        // mock the DB: user exists & can be returned
        when(userRepository.existsById(123L)).thenReturn(true);
        when(userRepository.findById(123L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordService.passwordEncoder().encode(any(String.class)))
            .thenAnswer(invocation -> "HashedPassword-" + invocation.getArgument(0));
        // call the method under test
        GenericResponse response = service.changeUserPassword("newPassword", 123L);
        // capture the saved User
        verify(userRepository, times(1)).save(userCaptor.capture());
        // check if saved with correct data & correct status returned
        User expected = new User(
            new UsernameType("correctUname1"),
            new EmailType("example@foo.com"),
            new PasswordType("HashedPassword-newPassword"),
            new BannedType(false),
            new PrivacyType(false),
            new UserEnumType("USER"),
            new DetailType(),
            new FollowingType()   // no followers
        ); // and verify that saved the correct values
        assertEquals(userCaptor.getValue(), expected);
        // and assert that correct status code returned
        assertEquals(response, new ChangePasswordResponse200());
    }

    @Test
    public void returns500IfServerErrorEncounteredInChangePassword() {
        // mock user to exist
        when(userRepository.existsById(123L)).thenReturn(true);
        // but mock the repo to return empty -> internal error!
        when(userRepository.findById(123L)).thenReturn(Optional.empty());
        // and call the method to find the internal error
        // while checking that the error is caught
        assertDoesNotThrow(() -> service.changeUserPassword("newPassword", 123L));
        GenericResponse response = service.changeUserPassword("newPassword", 123L);
        assertEquals(response, new InternalServerErrorResponse());
    }

    @Test
    public void returns404IfUserDoesNotExistInGetUser() {
        // mock user to not exist
        when(userRepository.existsById(123L)).thenReturn(false);
        // call service
        GenericResponse response = service.getUserById(123L);
        // and check that 404 returned
        assertEquals(response, new DoesNotExistResponse404());
    }

    @Test
    public void returnsCorrectUserIfUserPresentInGetUser() {
        // mock user to exist
        when(userRepository.existsById(123L)).thenReturn(true);
        // make a dummy user
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
        // and mock the DB response
        when(userRepository.findById(123L)).thenReturn(Optional.of(expected));
        // call the service and check if response is correct
        GenericResponse response = service.getUserById(123L);
        assertEquals(response, new UserResponse(expected));
    }

    /**
     * Takes an existing user and modifies their ID, for testing purposes only.
     *
     * @param user original user
     * @param testId ID to be set
     * @return this user but with new ID
     */
    private User mockHelper(User user, long testId) {
        user.setUserId(testId);
        return user;
    }

}
