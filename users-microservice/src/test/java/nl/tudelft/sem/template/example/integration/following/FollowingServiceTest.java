package nl.tudelft.sem.template.example.integration.following;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import nl.tudelft.sem.template.example.services.FollowingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
public class FollowingServiceTest {

    @Mock
    UserRepository userRepository;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    FollowingService followingService;

    User testUserBanned;
    User testCallerValid;
    User testTargetUser;

    /**
     * Setup of the SUT and test objects.
     */
    @BeforeEach
    public void setup() {
        this.followingService = new FollowingService(userRepository);

        this.testUserBanned = new User(
                new UsernameType("bannedUser"),
                new EmailType("bannedAccount@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(true), // BANNED
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType());

        this.testCallerValid = new User(
                new UsernameType("validCaller"),
                new EmailType("validCallerAccount@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType(new ArrayList<>()));

        this.testTargetUser = new User(
                new UsernameType("targetUser"),
                new EmailType("targetAccount@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType(new ArrayList<>()));
    }

    @Test
    public void checkCallerDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(true);

        HttpStatus result = followingService.followUser(1L, 2L);
        assertEquals(HttpStatus.NOT_FOUND, result);
    }

    @Test
    public void checkWantedDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        HttpStatus result = followingService.followUser(1L, 2L);
        assertEquals(HttpStatus.NOT_FOUND, result);
    }

    @Test
    public void checkBothDontExist() {
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(false);

        HttpStatus result = followingService.followUser(1L, 2L);
        assertEquals(HttpStatus.NOT_FOUND, result);
    }

    @Test
    public void callerIsBanned() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserBanned));

        HttpStatus result = followingService.followUser(1L, 2L);
        assertEquals(HttpStatus.FORBIDDEN, result);
    }

    @Test
    public void triesToFollowSelf() {
        when(userRepository.existsById(1L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));

        HttpStatus result = followingService.followUser(1L, 1L);
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void triesToFollowTwice() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        // add the target user to the list of followed users of the caller
        testCallerValid.getFollowing().getFollowedUsers().add(testTargetUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));

        HttpStatus result = followingService.followUser(1L, 2L);
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void internalServerError() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        // will throw a NullPointerException
        when(userRepository.findById(1L)).thenReturn(null);
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));

        HttpStatus result = followingService.followUser(1L, 2L);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result);
    }

    @Test
    public void validFollowOneUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));

        final HttpStatus result = followingService.followUser(1L, 2L);

        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertEquals(testCallerValid, capturedUser);
        assertEquals(testCallerValid.getFollowing().getFollowedUsers(), List.of(testTargetUser));
        assertEquals(HttpStatus.OK, result);
    }

    @Test
    public void validFollowMultipleUsers() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(true);

        User secondTargetUser = new User(
                new UsernameType("targetUser2"),
                new EmailType("targetAccount2@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType(new ArrayList<>()));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));
        when(userRepository.findById(3L)).thenReturn(Optional.of(secondTargetUser));

        final HttpStatus result = followingService.followUser(1L, 2L);

        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertEquals(testCallerValid, capturedUser);
        assertEquals(testCallerValid.getFollowing().getFollowedUsers(), List.of(testTargetUser));
        assertEquals(HttpStatus.OK, result);

        final HttpStatus secondResult = followingService.followUser(1L, 3L);
        verify(userRepository, times(2)).save(userArgumentCaptor.capture());
        User capturedSecond = userArgumentCaptor.getValue();

        assertEquals(testCallerValid, capturedSecond);
        assertEquals(testCallerValid.getFollowing().getFollowedUsers(), List.of(testTargetUser, secondTargetUser));
        assertEquals(HttpStatus.OK, secondResult);
    }

    @Test
    public void mutualFollowing() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));

        final HttpStatus result = followingService.followUser(1L, 2L);

        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertEquals(testCallerValid, capturedUser);
        assertEquals(testCallerValid.getFollowing().getFollowedUsers(), List.of(testTargetUser));
        assertEquals(HttpStatus.OK, result);

        final HttpStatus secondResult = followingService.followUser(2L, 1L);
        verify(userRepository, times(2)).save(userArgumentCaptor.capture());
        User capturedSecond = userArgumentCaptor.getValue();

        assertEquals(testTargetUser, capturedSecond);
        assertEquals(testTargetUser.getFollowing().getFollowedUsers(), List.of(testCallerValid));
        assertEquals(HttpStatus.OK, secondResult);
    }
}