package nl.tudelft.sem.template.example.search;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import nl.tudelft.sem.template.example.domain.book.Book;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class PerformSearchHandlerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private PerformSearchHandler performSearchHandler;

    private User user;

    @BeforeEach
    void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        bookRepository = Mockito.mock(BookRepository.class);
        performSearchHandler = new PerformSearchHandler(userRepository, bookRepository);
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
    void handleSearch1() {
        SearchRequest request = new SearchRequest(1L, "username", null, null);
        User user = new User();

        when(userRepository.findByUsername(request.getUsername())).thenReturn(List.of(user));

        List<User> result = performSearchHandler.handleSearch(request);

        Assertions.assertEquals(List.of(user), result);
    }

    @Test
    void handleSearch2() {
        SearchRequest request = new SearchRequest(1L, null, "favoriteBook", null);
        Book book = new Book();
        User user = new User();

        when(bookRepository.findByTitle(request.getFavoriteBook())).thenReturn(List.of(book));
        when(userRepository.findByFavoriteBook(book.getBookId())).thenReturn(List.of(user));

        List<User> result = performSearchHandler.handleSearch(request);

        Assertions.assertEquals(List.of(user), result);
    }

    @Test
    void handleSearch3() {
        SearchRequest request = new SearchRequest(1L, null, null, "friendUsername");
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

        when(userRepository.findByUsername(request.getFriendUsername())).thenReturn(List.of(user1, user2, user3));
        when(userRepository.findByFavoriteBook(anyLong())).thenReturn(List.of(user2));

        List<User> result = performSearchHandler.handleSearch(request);

        Assertions.assertEquals(List.of(), result);
    }

    @Test
    void handleSearch4() {
        SearchRequest request = new SearchRequest(1L, null, null, "friendUsername");
        when(userRepository.findByUsername(request.getFriendUsername())).thenReturn(null);

        List<User> result = performSearchHandler.handleSearch(request);
        Assertions.assertEquals(List.of(), result);
    }

    @Test
    void handleSearch5() {
        SearchRequest request = new SearchRequest(1L, null, null, null);

        Assertions.assertEquals(List.of(), performSearchHandler.handleSearch(request));
    }

    @Test
    void handleSearch6() {
        SearchRequest request = new SearchRequest(1L, null, null, "friendUsername");

        User user1 = new User(new UsernameType("user1"),
                new EmailType("email"),
                new PasswordType("password"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("ADMIN"),
                new DetailType(),
                new FollowingType(List.of()));

        User user2 = new User(new UsernameType("user2"),
                new EmailType("email1"),
                new PasswordType("password3"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType(List.of(user1)));

        user1.setFollowing(new FollowingType(List.of(user2)));

        when(userRepository.findByUsername(request.getFriendUsername())).thenReturn(List.of(user1, user2));
        List<User> result = performSearchHandler.handleSearch(request);

        Assertions.assertEquals(List.of(user2, user1), result);
    }
}
