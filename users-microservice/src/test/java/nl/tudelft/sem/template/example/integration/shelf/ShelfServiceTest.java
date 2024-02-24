package nl.tudelft.sem.template.example.integration.shelf;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse200;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse403;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse404;
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
import nl.tudelft.sem.template.example.services.ShelfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ShelfServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    BookRepository bookRepository;
    ShelfService shelfService;

    @BeforeEach
    public void setup() {
        this.shelfService = new ShelfService(userRepository, bookRepository);
    }

    @Test
    public void returns404ResponseWhenUserDoesNotExist() {
        // set up mock DB
        when(userRepository.existsById(123L)).thenReturn(false);
        when(bookRepository.existsById(5L)).thenReturn(true);
        // call shelfService
        ManageBookShelfResponse response = shelfService.checkBookshelfValidity(123, 1, 5);
        // check if userRepo queried
        verify(userRepository, times(1)).existsById(123L);
        // and correct result returned
        assertEquals(response.getClass(), ManageBookShelfResponse404.class);
    }

    @Test
    public void returns404ResponseWhenBookDoesNotExist() {
        // set up mock DB
        when(userRepository.existsById(123L)).thenReturn(true);
        when(bookRepository.existsById(5L)).thenReturn(false);
        // call shelfService
        ManageBookShelfResponse response = shelfService.checkBookshelfValidity(123, 1, 5);
        // check if userRepo queried
        verify(userRepository, times(1)).existsById(123L);
        // and if bookRepo queried
        verify(bookRepository, times(1)).existsById(5L);
        // and correct result returned
        assertEquals(response.getClass(), ManageBookShelfResponse404.class);
    }

    @Test
    public void returns403WhenUserBanned() {
        // set up mock DB -> both true!
        when(userRepository.existsById(123L)).thenReturn(true);
        when(bookRepository.existsById(5L)).thenReturn(true);
        // make sure that the returned User is banned
        User exampleBanned = new User(
            new UsernameType("username"),
            new EmailType("example@mail.com"),
            new PasswordType("123HashedPassword"),
            new BannedType(true), // BANNED
            new PrivacyType(false),
            new UserEnumType("USER"),
            new DetailType(),
            new FollowingType()
        ); // configure mock response from DB
        when(userRepository.findById(123L)).thenReturn(Optional.of(exampleBanned));
        // and check if indeed 403 returned
        ManageBookShelfResponse response = shelfService.checkBookshelfValidity(123, 1, 5);
        assertEquals(response, new ManageBookShelfResponse403("USER_BANNED"));
    }

    @Test
    public void returns200OKWhenBookAndUserPresent() {
        // set up mock DB -> both true!
        when(userRepository.existsById(123L)).thenReturn(true);
        when(bookRepository.existsById(5L)).thenReturn(true);
        // make sure that the returned User is not banned
        User exampleUser = new User(
            new UsernameType("username"),
            new EmailType("example@mail.com"),
            new PasswordType("123HashedPassword"),
            new BannedType(false),
            new PrivacyType(false),
            new UserEnumType("USER"),
            new DetailType(),
            new FollowingType()
        );
        // configure mock response from DB
        when(userRepository.findById(123L)).thenReturn(Optional.of(exampleUser));
        // and check if 200 OK returned
        ManageBookShelfResponse response = shelfService.checkBookshelfValidity(123, 1, 5);
        assertEquals(response, new ManageBookShelfResponse200(1L, 5L));
    }

    @Test
    public void simulateInternalError() {
        // simple error: DB returns that user present, but returned user empty.
        when(userRepository.existsById(123L)).thenReturn(true);
        when(bookRepository.existsById(5L)).thenReturn(true);
        when(userRepository.findById(123L)).thenReturn(Optional.empty());
        // and check that no exception thrown
        assertDoesNotThrow(() -> {
            shelfService.checkBookshelfValidity(123, 1, 5);
        });
        // and that null returned
        assertNull(shelfService.checkBookshelfValidity(123, 1, 5));
    }
}
