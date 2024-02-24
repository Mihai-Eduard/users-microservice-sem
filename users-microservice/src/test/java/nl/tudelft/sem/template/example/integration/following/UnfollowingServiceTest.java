package nl.tudelft.sem.template.example.integration.following;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.modules.builders.UserBuilder;
import nl.tudelft.sem.template.example.modules.builders.UserDirector;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.User;
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
public class UnfollowingServiceTest {

    @Mock
    UserRepository userRepository;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    FollowingService followingService;

    UserBuilder builder;
    UserDirector director;

    User testUserBanned;
    User testCallerValid;
    User testTargetUser;

    /**
     * Setup of the SUT and test objects.
     */
    @BeforeEach
    public void setup() {
        this.followingService = new FollowingService(userRepository);

        this.builder = new UserBuilder();
        this.director = new UserDirector(this.builder);

        director.constructBannedUser();
        this.testUserBanned = builder.build();

        director.constructValidUser();
        this.testCallerValid = builder.build();

        director.constructValidUser();
        builder.setEmail(new EmailType("targetAccount@foo.com"));
        builder.setUsername(new UsernameType("targetAccount"));
        this.testTargetUser = builder.build();
    }

    @Test
    public void checkCallerDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(true);

        HttpStatus result = followingService.unfollowUser(1L, 2L);
        assertEquals(HttpStatus.NOT_FOUND, result);
    }

    @Test
    public void checkWantedDoesNotExist() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        HttpStatus result = followingService.unfollowUser(1L, 2L);
        assertEquals(HttpStatus.NOT_FOUND, result);
    }

    @Test
    public void checkBothDontExist() {
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(false);

        HttpStatus result = followingService.unfollowUser(1L, 2L);
        assertEquals(HttpStatus.NOT_FOUND, result);
    }

    @Test
    public void callerIsBanned() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserBanned));

        HttpStatus result = followingService.unfollowUser(1L, 2L);
        assertEquals(HttpStatus.FORBIDDEN, result);
    }

    @Test
    public void internalServerError() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        // will throw a NullPointerException
        when(userRepository.findById(1L)).thenThrow(new RuntimeException());
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));

        HttpStatus result = followingService.unfollowUser(1L, 2L);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result);
    }

    @Test
    public void userNotFollowed() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        // add the target user to the list of followed users of the caller
        director.constructValidUser();
        builder.setEmail(new EmailType("otherUser@foo.com"));
        builder.setUsername(new UsernameType("otherUser"));
        User otherUser = builder.build();

        // add a miscellaneous user to the list of users being followed, to make sure the equality checking works
        testCallerValid.getFollowing().getFollowedUsers().add(otherUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));

        HttpStatus result = followingService.unfollowUser(1L, 2L);
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void successful() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        // add the target user to the list of followed users of the caller
        director.constructValidUser();
        builder.setEmail(new EmailType("otherUser@foo.com"));
        builder.setUsername(new UsernameType("otherUser"));
        User otherUser = builder.build();

        // add a miscellaneous user to the list of users being followed, to make sure only the wanted user is removed
        testCallerValid.getFollowing().getFollowedUsers().add(otherUser);

        // add the wanted user to the list, so they can be unfollowed
        testCallerValid.getFollowing().getFollowedUsers().add(testTargetUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));

        HttpStatus result = followingService.unfollowUser(1L, 2L);
        assertEquals(HttpStatus.OK, result);

        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();

        assertEquals(testCallerValid, capturedUser);
        assertEquals(testCallerValid.getFollowing().getFollowedUsers(), List.of(otherUser));
    }
}