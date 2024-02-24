package nl.tudelft.sem.template.example.integration.following;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.modules.builders.UserBuilder;
import nl.tudelft.sem.template.example.modules.builders.UserDirector;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UsernameType;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"mockUserRepository"})
public class UnfollowingFeatureIntegrationTest {
    @MockBean
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    UserBuilder builder;
    UserDirector director;

    User testUserBanned;
    User testCallerValid;
    User testTargetUser;

    /**
     * Setup of the test User objects.
     */
    @BeforeEach
    public void setup() {
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
    public void callerDoesNotExist() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(true);

        mockMvc
                .perform(put("/api/unfollow/2")
                        .param("userID", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void targetDoesNotExist() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        mockMvc
                .perform(put("/api/unfollow/2")
                        .param("userID", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void neitherExists() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(false);

        mockMvc
                .perform(put("/api/unfollow/2")
                        .param("userID", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void bannedCaller() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserBanned));

        String result = mockMvc
                .perform(put("/api/unfollow/2")
                        .param("userID", "1"))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        assertEquals("{\"role\":\"USER_BANNED\"}", result);
    }

    @Test
    public void internalServerError() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(userRepository.findById(1L)).thenThrow(new RuntimeException());

        mockMvc
                .perform(put("/api/unfollow/2")
                        .param("userID", "1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void userNotFollowed() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));

        mockMvc
                .perform(put("/api/unfollow/2")
                        .param("userID", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void successful() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));

        mockMvc
                .perform(put("/api/follow/2")
                        .param("userID", "1"))
                .andExpect(status().isOk());

        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertEquals(List.of(testTargetUser), capturedUser.getFollowing().getFollowedUsers());

        mockMvc
                .perform(put("/api/unfollow/2")
                        .param("userID", "1"))
                .andExpect(status().isOk());

        verify(userRepository, times(2)).save(userArgumentCaptor.capture());
        User capturedUserAfterUnfollowing = userArgumentCaptor.getValue();
        assertEquals(List.of(), capturedUserAfterUnfollowing.getFollowing().getFollowedUsers());
    }

    @Test
    public void followTwoThenUnfollowBoth() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(true);

        director.constructValidUser();
        builder.setEmail(new EmailType("otherTargetAccount@foo.com"));
        builder.setUsername(new UsernameType("otherTargetAccount"));
        User secondTarget = builder.build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));
        when(userRepository.findById(3L)).thenReturn(Optional.of(secondTarget));

        mockMvc
                .perform(put("/api/follow/2")
                        .param("userID", "1"))
                .andExpect(status().isOk());

        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertEquals(List.of(testTargetUser), capturedUser.getFollowing().getFollowedUsers());

        mockMvc
                .perform(put("/api/follow/3")
                        .param("userID", "1"))
                .andExpect(status().isOk());

        verify(userRepository, times(2)).save(userArgumentCaptor.capture());
        User capturedUserAfterSecondFollow = userArgumentCaptor.getValue();
        assertEquals(List.of(testTargetUser, secondTarget), capturedUserAfterSecondFollow.getFollowing().getFollowedUsers());

        mockMvc
                .perform(put("/api/unfollow/2")
                        .param("userID", "1"))
                .andExpect(status().isOk());

        verify(userRepository, times(3)).save(userArgumentCaptor.capture());
        User capturedUserAfterFirstUnfollow = userArgumentCaptor.getValue();
        assertEquals(List.of(secondTarget), capturedUserAfterFirstUnfollow.getFollowing().getFollowedUsers());

        mockMvc
                .perform(put("/api/unfollow/3")
                        .param("userID", "1"))
                .andExpect(status().isOk());

        verify(userRepository, times(4)).save(userArgumentCaptor.capture());
        User capturedUserAfterSecondUnfollow = userArgumentCaptor.getValue();
        assertEquals(List.of(), capturedUserAfterSecondUnfollow.getFollowing().getFollowedUsers());
    }
}
