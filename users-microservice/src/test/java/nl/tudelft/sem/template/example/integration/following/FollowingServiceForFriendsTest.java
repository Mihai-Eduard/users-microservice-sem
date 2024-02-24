package nl.tudelft.sem.template.example.integration.following;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.FollowingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FollowingServiceForFriendsTest {
    @Mock
    private UserRepository userRepository;

    private FollowingService followingService;

    @BeforeEach
    void setUp() {
        followingService = new FollowingService(userRepository);
    }

    @Test
    void nullValuesTest() {
        User user = new User();
        user.setUserId(1L);
        user.setFollowing(null);

        List<Long> result1 = followingService.getFriends(user);
        assertEquals(0, result1.size());

        user.setFollowing(new FollowingType(null));

        List<Long> result2 = followingService.getFriends(user);
        assertEquals(0, result2.size());

        User friend1 = new User();
        friend1.setUserId(2L);
        User friend2 = new User();
        friend2.setUserId(3L);
        User friend3 = new User();
        friend3.setUserId(4L);

        user.setFollowing(new FollowingType(List.of(friend1, friend2, friend3)));
        friend1.setFollowing(null);
        friend2.setFollowing(new FollowingType(null));
        friend3.setFollowing(new FollowingType(List.of()));

        List<Long> result3 = followingService.getFriends(user);
        assertEquals(0, result3.size());
    }

    @Test
    void noFriendsFoundTest() {
        User user = new User();
        user.setUserId(1L);

        List<Long> result1 = followingService.getFriends(user);
        assertEquals(0, result1.size());

        User friend1 = new User();
        friend1.setUserId(2L);
        User friend2 = new User();
        friend2.setUserId(3L);
        User friend3 = new User();
        friend3.setUserId(4L);
        user.setFollowing(new FollowingType(List.of(friend1, friend2, friend3)));

        List<Long> result2 = followingService.getFriends(user);
        assertEquals(0, result2.size());

        user.setFollowing(new FollowingType(List.of()));
        friend1.setFollowing(new FollowingType(List.of(user)));
        friend2.setFollowing(new FollowingType(List.of(user)));
        friend3.setFollowing(new FollowingType(List.of()));

        List<Long> result3 = followingService.getFriends(user);
        assertEquals(0, result3.size());
    }

    @Test
    void multipleFriendsFoundForOneUserTest() {
        User user = new User();
        user.setUserId(1L);
        User friend1 = new User();
        friend1.setUserId(2L);
        User friend2 = new User();
        friend2.setUserId(3L);
        User friend3 = new User();
        friend3.setUserId(4L);

        user.setFollowing(new FollowingType(List.of(friend1, friend2, friend3)));
        friend1.setFollowing(new FollowingType(List.of(user, friend2)));
        friend2.setFollowing(new FollowingType(List.of(user)));

        List<Long> result = followingService.getFriends(user);
        assertEquals(2, result.size());
        assertEquals(friend1.getUserId(), result.get(0));
        assertEquals(friend2.getUserId(), result.get(1));
    }

    @Test
    void multipleFriendsFoundForMultipleUsersTest() {
        User user1 = new User();
        user1.setUserId(1L);
        User user2 = new User();
        user2.setUserId(2L);
        User user3 = new User();
        user3.setUserId(3L);
        User user4 = new User();
        user4.setUserId(4L);
        User user5 = new User();
        user5.setUserId(5L);

        user1.setFollowing(new FollowingType(List.of(user2, user3, user4)));
        user2.setFollowing(new FollowingType(List.of(user1, user3)));
        user3.setFollowing(new FollowingType(List.of(user1, user2, user4, user5)));
        user4.setFollowing(new FollowingType(List.of(user1, user3, user5)));
        user5.setFollowing(new FollowingType(List.of()));

        List<Long> result1 = followingService.getFriends(user1);
        assertEquals(3, result1.size());
        assertEquals(user2.getUserId(), result1.get(0));
        assertEquals(user3.getUserId(), result1.get(1));
        assertEquals(user4.getUserId(), result1.get(2));

        List<Long> result2 = followingService.getFriends(user2);
        assertEquals(2, result2.size());
        assertEquals(user1.getUserId(), result2.get(0));
        assertEquals(user3.getUserId(), result2.get(1));

        List<Long> result3 = followingService.getFriends(user3);
        assertEquals(3, result3.size());
        assertEquals(user1.getUserId(), result3.get(0));
        assertEquals(user2.getUserId(), result3.get(1));
        assertEquals(user4.getUserId(), result3.get(2));

        List<Long> result4 = followingService.getFriends(user4);
        assertEquals(2, result4.size());
        assertEquals(user1.getUserId(), result4.get(0));
        assertEquals(user3.getUserId(), result4.get(1));

        List<Long> result5 = followingService.getFriends(user5);
        assertEquals(0, result5.size());
    }
}
