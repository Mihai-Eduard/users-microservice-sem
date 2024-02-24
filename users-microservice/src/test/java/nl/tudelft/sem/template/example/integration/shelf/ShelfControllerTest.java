package nl.tudelft.sem.template.example.integration.shelf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.controllers.ShelfController;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfRequest;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse200;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse403;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse404;
import nl.tudelft.sem.template.example.services.RestService;
import nl.tudelft.sem.template.example.services.ShelfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class ShelfControllerTest {
    @Mock
    ShelfService shelfService;
    @Mock
    RestService restService;

    ShelfController shelfController;

    @BeforeEach
    public void setup() {
        shelfController = new ShelfController(shelfService, restService);
    }

    @Test
    public void addRespondsWithInternalServerErrorIfServiceEncountered500InternalServerError() {
        when(shelfService.checkBookshelfValidity(123L, 2L, 5L)).thenReturn(null);
        ResponseEntity<ManageBookShelfResponse> response = shelfController.addBookToBookshelf(123L, 2L, 5L);
        assertEquals(response, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @Test
    public void removeRespondsWithInternalServerErrorIfServiceEncountered500InternalServerError() {
        when(shelfService.checkBookshelfValidity(123L, 2L, 5L)).thenReturn(null);
        ResponseEntity<ManageBookShelfResponse> response = shelfController.removeBookFromBookshelf(123L, 2L, 5L);
        assertEquals(response, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @Test
    public void addRespondsWith403ForbiddenIfUserBanned() {
        when(shelfService.checkBookshelfValidity(123L, 2L, 5L)).thenReturn(new ManageBookShelfResponse403("USER_BANNED"));
        ResponseEntity<ManageBookShelfResponse> response = shelfController.addBookToBookshelf(123L, 2L, 5L);
        assertEquals(response, ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @Test
    public void removeRespondsWith403ForbiddenIfUserBanned() {
        when(shelfService.checkBookshelfValidity(123L, 2L, 5L)).thenReturn(new ManageBookShelfResponse403("USER_BANNED"));
        ResponseEntity<ManageBookShelfResponse> response = shelfController.removeBookFromBookshelf(123L, 2L, 5L);
        assertEquals(response, ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @Test
    public void addRespondsWith404NotFoundIfBookOrUserNotFound() {
        when(shelfService.checkBookshelfValidity(123L, 2L, 5L)).thenReturn(new ManageBookShelfResponse404());
        ResponseEntity<ManageBookShelfResponse> response = shelfController.addBookToBookshelf(123L, 2L, 5L);
        assertEquals(response, ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Test
    public void removeRespondsWith404NotFoundIfBookOrUserNotFound() {
        when(shelfService.checkBookshelfValidity(123L, 2L, 5L)).thenReturn(new ManageBookShelfResponse404());
        ResponseEntity<ManageBookShelfResponse> response = shelfController.removeBookFromBookshelf(123L, 2L, 5L);
        assertEquals(response, ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Test
    public void addCallsExternalServiceIfDetailsCorrect() {
        // set expected response
        ManageBookShelfResponse expectedResponse = new ManageBookShelfResponse200(2L, 5L);
        String expectedUrl = "http://localhost:8081/bookshelf/2/book?userId=123&bookshelfId=2";
        // mock the services to be successful, and the other microservice to be successful as well
        when(shelfService.checkBookshelfValidity(123L, 2L, 5L))
            .thenReturn(expectedResponse);
        when(restService.buildBookshelfURL(2L, 123L))
            .thenReturn(expectedUrl);
        when(restService.addToMicroservice(
            any(String.class), any(ManageBookShelfRequest.class)
        )).thenReturn(HttpStatus.CREATED);
        // call the endpoint
        ResponseEntity<ManageBookShelfResponse> response = shelfController
            .addBookToBookshelf(123L, 2L, 5L);
        // verify that the shelf and rest services were called with correct data
        ManageBookShelfRequest expectedData = new ManageBookShelfRequest(5L);
        verify(restService).addToMicroservice(expectedUrl, expectedData);
        verify(shelfService).checkBookshelfValidity(123L, 2L, 5L);
        // and assert that correct result returned
        assertEquals(response, ResponseEntity.status(HttpStatus.OK)
            .body(expectedResponse));
    }

    @Test
    public void removeCallsExternalServiceIfDetailsCorrect() {
        // set expected response
        ManageBookShelfResponse expectedResponse = new ManageBookShelfResponse200(2L, 5L);
        String expectedUrl = "http://localhost:8081/bookshelf/2/book?userId=123&books=5&bookshelfId=2";
        // mock the services to be successful, and the other microservice to be successful as well
        when(shelfService.checkBookshelfValidity(123L, 2L, 5L))
            .thenReturn(expectedResponse);
        when(restService.buildBookshelfRemoveURL(2L, 123L, 5L))
            .thenReturn(expectedUrl);
        when(restService.removeFromMicroservice(
            any(String.class), any(ManageBookShelfRequest.class)
        )).thenReturn(HttpStatus.OK);
        // call the endpoint
        ResponseEntity<ManageBookShelfResponse> response = shelfController
            .removeBookFromBookshelf(123L, 2L, 5L);
        // verify that the shelf and rest services were called with correct data
        ManageBookShelfRequest expectedData = new ManageBookShelfRequest(5L);
        verify(restService).removeFromMicroservice(expectedUrl, expectedData);
        verify(shelfService).checkBookshelfValidity(123L, 2L, 5L);
        // and assert that correct result returned
        assertEquals(response, ResponseEntity.status(HttpStatus.OK)
            .body(expectedResponse));
    }

    @Test
    public void addReturns500InternalServerErrorIfServiceRespondsWithErrorCode() {
        // set expected response
        ManageBookShelfResponse expectedResponse = new ManageBookShelfResponse200(2L, 5L);
        String expectedUrl = "http://localhost:8081/bookshelf/2/book?userId=123&bookshelfId=2";
        // mock the services to be successful, and the other microservice to be failing, with one of the possible 400 codes
        when(shelfService.checkBookshelfValidity(123L, 2L, 5L))
            .thenReturn(expectedResponse);
        when(restService.buildBookshelfURL(2L, 123L))
            .thenReturn(expectedUrl);
        when(restService.addToMicroservice(
            any(String.class), any(ManageBookShelfRequest.class)
        )).thenReturn(HttpStatus.UNAUTHORIZED);
        // call the endpoint
        ResponseEntity<ManageBookShelfResponse> response = shelfController
            .addBookToBookshelf(123L, 2L, 5L);
        // verify that the shelf and rest services were called with correct data
        ManageBookShelfRequest expectedData = new ManageBookShelfRequest(5L);
        verify(restService).addToMicroservice(expectedUrl, expectedData);
        verify(shelfService).checkBookshelfValidity(123L, 2L, 5L);
        // and assert that INTERNAL_SERVER_ERROR returned
        assertEquals(response, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build());
    }

    @Test
    public void removeReturns500InternalServerErrorIfServiceRespondsWithErrorCode() {
        // set expected response
        ManageBookShelfResponse expectedResponse = new ManageBookShelfResponse200(2L, 5L);
        String expectedUrl = "http://localhost:8081/bookshelf/2/book?userId=123&books=5&bookshelfId=2";
        // mock the services to be successful, and the other microservice to be failing, with one of the possible 400 codes
        when(shelfService.checkBookshelfValidity(123L, 2L, 5L))
            .thenReturn(expectedResponse);
        when(restService.buildBookshelfRemoveURL(2L, 123L, 5L))
            .thenReturn(expectedUrl);
        when(restService.removeFromMicroservice(
            any(String.class), any(ManageBookShelfRequest.class)
        )).thenReturn(HttpStatus.UNAUTHORIZED);
        // call the endpoint
        ResponseEntity<ManageBookShelfResponse> response = shelfController
            .removeBookFromBookshelf(123L, 2L, 5L);
        // verify that the shelf and rest services were called with correct data
        ManageBookShelfRequest expectedData = new ManageBookShelfRequest(5L);
        verify(restService).removeFromMicroservice(expectedUrl, expectedData);
        verify(shelfService).checkBookshelfValidity(123L, 2L, 5L);
        // and assert that INTERNAL_SERVER_ERROR returned
        assertEquals(response, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build());
    }
}
