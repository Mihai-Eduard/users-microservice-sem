package nl.tudelft.sem.template.example.integration.following;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.controllers.FriendsController;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.FollowingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class FriendsControllerTest {
    @Mock
    private FollowingService followingService;
    @Mock
    private UserRepository userRepository;

    private FriendsController friendsController;

    @BeforeEach
    void setUp() {
        friendsController = new FriendsController(followingService, userRepository);
    }

    @Test
    void nullParametersTest() {
        ResponseEntity<Object> response1 = friendsController.getFriends(1L, null);
        assertEquals(response1.getStatusCode(), HttpStatus.BAD_REQUEST);

        ResponseEntity<Object> response2 = friendsController.getFriends(null, 1L);
        assertEquals(response2.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void userBannedTest() {
        User user = new User();
        user.setBanned(new BannedType(true));
        user.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<Object> response = friendsController.getFriends(1L, 5L);
        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void userOrWantedUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response1 = friendsController.getFriends(1L, 5L);
        assertEquals(404, response1.getStatusCodeValue());

        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response2 = friendsController.getFriends(1L, 5L);
        assertEquals(404, response2.getStatusCodeValue());
    }

    @Test
    void getAuthorBooksSuccessfully() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User wantedUser = new User();
        wantedUser.setUserId(5L);
        wantedUser.setRole(new UserEnumType("AUTHOR"));
        when(userRepository.findById(5L)).thenReturn(Optional.of(wantedUser));

        List<Long> friends = new ArrayList<>();
        friends.add(1L);
        friends.add(2L);
        friends.add(3L);
        when(followingService.getFriends(wantedUser)).thenReturn(friends);

        ResponseEntity<Object> response = friendsController.getFriends(1L, 5L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(friends, response.getBody());
    }

    @Test
    void serverErrorTest() {
        User user = new User();
        user.setUserId(1L);
        user.setBanned(new BannedType(false));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User wantedUser = new User();
        wantedUser.setUserId(5L);
        wantedUser.setRole(new UserEnumType("AUTHOR"));
        when(userRepository.findById(5L)).thenReturn(Optional.of(wantedUser));

        when(followingService.getFriends(wantedUser)).thenThrow(new IllegalArgumentException());

        ResponseEntity<Object> response = friendsController.getFriends(1L, 5L);
        assertEquals(500, response.getStatusCodeValue());
    }
}
