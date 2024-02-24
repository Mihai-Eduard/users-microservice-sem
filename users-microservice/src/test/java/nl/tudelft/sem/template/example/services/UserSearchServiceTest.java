package nl.tudelft.sem.template.example.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.search.SearchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


class UserSearchServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private UserSearchService userSearchService;

    private User user;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        bookRepository = Mockito.mock(BookRepository.class);
        userSearchService = new UserSearchService(userRepository, bookRepository);
        User u1 = new User(new UsernameType("admin1"),
                new EmailType("email"),
                new PasswordType("password"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("ADMIN"),
                new DetailType(),
                new FollowingType());
        user = new User(new UsernameType("admin"),
                new EmailType("email"),
                new PasswordType("password"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("ADMIN"),
                new DetailType(),
                new FollowingType(List.of(u1)));
        User user1 = new User(new UsernameType("admin"),
                new EmailType("email"),
                new PasswordType("password"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("ADMIN"),
                new DetailType(),
                new FollowingType(List.of(user)));
        User user2 = new User(new UsernameType("user1"),
                new EmailType("email1"),
                new PasswordType("password3"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType(List.of(user1, user)));
        User user3 = new User(new UsernameType("user2"),
                new EmailType("email1"),
                new PasswordType("password"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType(List.of(user1, user2)));
        u1.getFollowing().setFollowedUsers(List.of(user1, user2, user3));
    }

    @Test
    void searchUsers1() throws UserNotFoundException, UserBannedException {
        SearchRequest request = new SearchRequest(1L, "username", null, null);


        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(request.getUsername())).thenReturn(List.of(user));

        List<User> result = userSearchService.searchUsers(request.getUserId(), request.getUsername(), null, null);

        assertEquals(List.of(user), result);
    }

    @Test
    void searchUsers2() {
        SearchRequest request = new SearchRequest(1L, null, "favBook", null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userSearchService.searchUsers(request.getUserId(), null, request.getFavoriteBook(), null));
    }

    @Test
    void searchUsers3() throws UserNotFoundException, UserBannedException {
        SearchRequest request = new SearchRequest(2L, null, null, "friendUsername");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(request.getFriendUsername())).thenReturn(List.of(user));
        when(userRepository.findByFavoriteBook(anyLong())).thenReturn(List.of(user));

        List<User> result = userSearchService.searchUsers(request.getUserId(), null, null, request.getFriendUsername());

        assertEquals(List.of(), result);
    }
}
