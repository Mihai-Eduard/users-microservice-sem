package nl.tudelft.sem.template.example.search;

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
import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class CheckUserExistsHandlerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SearchHandler nextHandler;

    @InjectMocks
    private CheckUserExistsHandler checkUserExistsHandler;

    private User user;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        nextHandler = Mockito.mock(SearchHandler.class);
        checkUserExistsHandler = new CheckUserExistsHandler(userRepository, nextHandler);
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
    }

    @Test
    void handleSearch1() throws UserBannedException, UserNotFoundException {
        SearchRequest request = new SearchRequest(1L, null, null, null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(nextHandler.handleSearch(request)).thenReturn(List.of());

        checkUserExistsHandler.handleSearch(request);

        Mockito.verify(nextHandler).handleSearch(request);
    }

    @Test
    void handleSearch2() {
        SearchRequest request = new SearchRequest(1L, null, null, null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> checkUserExistsHandler.handleSearch(request));
    }
}
