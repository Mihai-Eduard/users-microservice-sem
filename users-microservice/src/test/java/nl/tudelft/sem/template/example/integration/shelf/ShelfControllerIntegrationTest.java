package nl.tudelft.sem.template.example.integration.shelf;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import java.util.Optional;
import nl.tudelft.sem.template.example.controllers.ShelfController;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse200;
import nl.tudelft.sem.template.example.modules.builders.UserBuilder;
import nl.tudelft.sem.template.example.modules.builders.UserDirector;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.RestService;
import nl.tudelft.sem.template.example.services.ShelfService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


/**
 * This class implements integration tests,
 * mocking the responses from the ShelfService repository,
 * and integrating both ShelfService and ShelfController.
 */
@SpringBootTest
public class ShelfControllerIntegrationTest {

    private ShelfService shelfService;
    private ShelfController shelfController;
    private static WireMockServer wireMockServer;

    private User correctUser;
    private User bannedUser;

    @Mock
    UserRepository userRepository;
    @Mock
    BookRepository bookRepository;

    @BeforeAll
    static void setupWM() {
        // set-up wiremock
        wireMockServer = new WireMockServer(options().port(8081));
        wireMockServer.start();
        configureFor(8081); // we will use the same port that the other team uses
    }

    @BeforeEach
    void setupComponents() {
        // set up: we will mock repositories, but test integration
        // of the service, controller with the other team's endpoint
        this.shelfService = new ShelfService(userRepository, bookRepository);
        this.shelfController = new ShelfController(this.shelfService, new RestService());
        // and build users
        UserBuilder builder = new UserBuilder();
        UserDirector director = new UserDirector(builder);

        director.constructValidUser();
        this.correctUser = builder.build();

        director.constructBannedUser();
        this.bannedUser = builder.build();
    }

    @AfterAll
    static void closeWiremock() {
        wireMockServer.stop();
    }

    @Test
    public void returnsSuccessWhenCorrectlyAdded() {
        // set up mock DB -> both true!
        when(userRepository.existsById(123L)).thenReturn(true);
        when(bookRepository.existsById(5L)).thenReturn(true);
        // configure mock response from DB
        when(userRepository.findById(123L)).thenReturn(Optional.of(correctUser));
        // set up wiremock to return 201 CREATED just like it would in a real scenario
        wireMockServer.stubFor(
            post(urlEqualTo("/bookshelf/2/book?userId=123&bookshelfId=2"))
                .willReturn(aResponse().withStatus(201).withResponseBody(new Body("{ \"bookId\":5 }")))
        );
        // and check if the controller returns the correct response
        assertEquals(shelfController.addBookToBookshelf(123L, 2L, 5L), ResponseEntity.status(
            HttpStatus.OK).body(new ManageBookShelfResponse200(2L, 5L)));
    }

    @Test
    public void returnsSuccessWhenCorrectlyRemoved() {
        // set up mock DB -> both true!
        when(userRepository.existsById(123L)).thenReturn(true);
        when(bookRepository.existsById(5L)).thenReturn(true);
        // configure mock response from DB
        when(userRepository.findById(123L)).thenReturn(Optional.of(correctUser));
        // set up wiremock to return 201 CREATED just like it would in a real scenario
        wireMockServer.stubFor(
            delete(urlEqualTo("/bookshelf/2/book?userId=123&books=5&bookshelfId=2"))
                .willReturn(aResponse().withStatus(200))
        );
        // and check if the controller returns the correct response
        assertEquals(shelfController.removeBookFromBookshelf(123L, 2L, 5L), ResponseEntity.status(
            HttpStatus.OK).body(new ManageBookShelfResponse200(2L, 5L)));
    }

    @Test
    public void returns500IfBookshelfMicroserviceEncounteredError() {
        // set up mock DB -> both true!
        when(userRepository.existsById(123L)).thenReturn(true);
        when(bookRepository.existsById(5L)).thenReturn(true);
        // configure mock response from DB
        when(userRepository.findById(123L)).thenReturn(Optional.of(correctUser));
        // set up wiremock to return 201 CREATED just like it would in a real scenario
        wireMockServer.stubFor(
            post(urlEqualTo("/bookshelf/2/book?userId=123&bookshelfId=2"))
                .willReturn(aResponse().withStatus(404).withResponseBody(new Body("Bookshelf does not exist!")))
        );
        // and check if the controller returns the correct response
        assertEquals(shelfController.addBookToBookshelf(123L, 2L, 5L), ResponseEntity.status(
            HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @Test
    public void returns404IfUserNotFound() {
        when(userRepository.existsById(123L)).thenReturn(false);
        assertEquals(shelfController.addBookToBookshelf(123L, 2L, 5L), ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        assertEquals(
            shelfController.removeBookFromBookshelf(123L, 2L, 5L),
            ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Test
    public void returns404IfBookNotFound() {
        when(userRepository.existsById(123L)).thenReturn(true);
        when(bookRepository.existsById(5L)).thenReturn(false);
        assertEquals(
            shelfController.addBookToBookshelf(123L, 2L, 5L),
            ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        assertEquals(
            shelfController.removeBookFromBookshelf(123L, 2L, 5L),
            ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Test
    public void returns403IfUserBanned() {
        when(userRepository.existsById(123L)).thenReturn(true);
        when(bookRepository.existsById(5L)).thenReturn(true);
        // configure mock response from DB
        when(userRepository.findById(123L)).thenReturn(Optional.of(bannedUser));
        // and check
        assertEquals(
            shelfController.addBookToBookshelf(123L, 2L, 5L),
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        );
        assertEquals(
            shelfController.removeBookFromBookshelf(123L, 2L, 5L),
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        );
    }
}
