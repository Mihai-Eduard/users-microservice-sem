package nl.tudelft.sem.template.example.search;

import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.*;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.UserSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.nio.charset.CharacterCodingException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class CheckUserBannedHandlerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SearchHandler nextHandler;

    @InjectMocks
    private CheckUserBannedHandler checkUserBannedHandler;



    private User user;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        nextHandler = Mockito.mock(SearchHandler.class);
        checkUserBannedHandler = new CheckUserBannedHandler(userRepository, nextHandler);
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
    }

    @Test
    void handleSearch1() throws UserBannedException, UserNotFoundException {
        SearchRequest request = new SearchRequest(1L, null, null, null);
        user.setBanned(new BannedType(false));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(nextHandler.handleSearch(request)).thenReturn(List.of());

        checkUserBannedHandler.handleSearch(request);

        Mockito.verify(nextHandler).handleSearch(request);
    }

    @Test
    void handleSearch2() {
        SearchRequest request = new SearchRequest(1L, null, null, null);

        user.setBanned(new BannedType(true));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(UserBannedException.class, () -> checkUserBannedHandler.handleSearch(request));
    }

    @Test
    void handleSearch3() {
        SearchRequest request = new SearchRequest(1L, null, null, null);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    }
}