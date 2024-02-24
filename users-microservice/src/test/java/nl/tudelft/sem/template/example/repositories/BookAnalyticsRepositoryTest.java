package nl.tudelft.sem.template.example.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.example.builders.BookBuilder;
import nl.tudelft.sem.template.example.builders.BookDirector;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class BookAnalyticsRepositoryTest {
    @Autowired
    private BookAnalyticsRepository bookAnalyticsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    private long bookOneId;

    private long bookTwoId;

    /**
     * Setup for the tests.
     */
    @BeforeEach
    public void setup() {
        BookBuilder bookBuilder = new BookBuilder();
        BookDirector bookDirector = new BookDirector(bookBuilder);
        bookDirector.constructValidBook();

        Book bookOne = bookBuilder.build();

        bookDirector.constructUpdatedBook();
        Book bookTwo = bookBuilder.build();

        bookRepository.save(bookOne);
        bookRepository.save(bookTwo);

        bookOneId =  bookRepository.findByTitle(bookOne.getTitle().getBookTitle()).get(0).getBookId();
        bookTwoId = bookRepository.findByTitle(bookTwo.getTitle().getBookTitle()).get(0).getBookId();

        User bob = new User(
                10L,
                new UsernameType("bob"),
                new EmailType("email@example.com"),
                new PasswordType("strongPassword123!"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType("bio", "name", "location", bookOneId, List.of("action")),
                new FollowingType(new ArrayList<>())
        );

        User alice = new User(
                11L,
                new UsernameType("alice"),
                new EmailType("email@example.com"),
                new PasswordType("strongPassword123!"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType("bio", "name", "location", bookTwoId, List.of("action", "comedy")),
                new FollowingType(new ArrayList<>())
        );

        User jeff = new User(
                12L,
                new UsernameType("jeff"),
                new EmailType("email@example.com"),
                new PasswordType("strongPassword123!"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType("bio", "name", "location", bookOneId, List.of("action", "comedy", "horror")),
                new FollowingType(new ArrayList<>())
        );

        userRepository.save(bob);
        userRepository.save(alice);
        userRepository.save(jeff);
    }

    @Test
    public void testGetPopularGenres() {
        List<User> users = userRepository.findAll();
        List<String> popularGenres = bookAnalyticsRepository.getPopularGenres();

        assertEquals(3, users.size());
        assertEquals(3, popularGenres.size());
        assertEquals("action", popularGenres.get(0));
        assertEquals("comedy", popularGenres.get(1));
        assertEquals("horror", popularGenres.get(2));
    }

    @Test
    public void testGetPopularBooks() {
        List<User> users = userRepository.findAll();
        List<Book> books = bookRepository.findAll();
        List<Book> popularBooks = bookRepository.getPopularBooks();

        assertEquals(3, users.size());
        assertEquals(2, books.size());
        assertEquals(2, popularBooks.size());
        assertEquals(bookOneId, popularBooks.get(0).getBookId());
        assertEquals(bookTwoId, popularBooks.get(1).getBookId());
    }
}
