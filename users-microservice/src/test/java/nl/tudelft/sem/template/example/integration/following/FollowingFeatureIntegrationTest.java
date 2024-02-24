package nl.tudelft.sem.template.example.integration.following;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class FollowingFeatureIntegrationTest {
    @MockBean
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    User testUserBanned;
    User testCallerValid;
    User testTargetUser;

    /**
     * Setup of the test User objects.
     */
    @BeforeEach
    public void setup() {
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
    public void callerDoesNotExist() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(true);

        mockMvc
                .perform(put("/api/follow/2")
                        .param("userID", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void targetDoesNotExist() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        mockMvc
                .perform(put("/api/follow/2")
                        .param("userID", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void neitherExists() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.existsById(2L)).thenReturn(false);

        mockMvc
                .perform(put("/api/follow/2")
                        .param("userID", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void bannedCaller() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUserBanned));

        String result = mockMvc
                .perform(put("/api/follow/2")
                        .param("userID", "1"))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        assertEquals("{\"role\":\"USER_BANNED\"}", result);
    }

    @Test
    public void successfulSingleUser() throws Exception {
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
    }

    @Test
    public void triesToFollowSameUserTwice() throws Exception {
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
                .perform(put("/api/follow/2")
                        .param("userID", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void triesToFollowSelf() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));

        mockMvc
                .perform(put("/api/follow/1")
                        .param("userID", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void followTwoUsers() throws Exception {

        User targetTwo = new User(
                new UsernameType("targetUser2"),
                new EmailType("targetAccount2@foo.com"),
                new PasswordType("0xHashedPasswordx0"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType(new ArrayList<>()));

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testCallerValid));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTargetUser));
        when(userRepository.findById(3L)).thenReturn(Optional.of(targetTwo));

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
        User capturedUserTwo = userArgumentCaptor.getValue();
        assertEquals(List.of(testTargetUser, targetTwo), capturedUserTwo.getFollowing().getFollowedUsers());
    }
}
