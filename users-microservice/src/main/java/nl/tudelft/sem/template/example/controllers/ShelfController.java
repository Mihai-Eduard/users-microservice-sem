package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfRequest;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse200;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse403;
import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfResponse404;
import nl.tudelft.sem.template.example.services.RestService;
import nl.tudelft.sem.template.example.services.ShelfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ShelfController {

    private final transient ShelfService shelfService;
    private final transient RestService restService;

    public ShelfController(ShelfService shelfService, RestService restService) {
        this.shelfService = shelfService;
        this.restService = restService;
    }

    /**
     * Endpoint that allows users to add books to their collections.
     * Validates a request and passes it to the BookService API s.t. the relation can be stored.
     *
     * @param userId ID of user for whom the book is to be added
     * @param shelfId ID of shelf to which the book should be added
     * @param bookId ID of book that should be added
     * @return Response (200, 403, 404 xor 500) depending on status
     */
    @PostMapping("/shelf")
    @ResponseBody
    public ResponseEntity<ManageBookShelfResponse> addBookToBookshelf(
        @RequestParam("userID") long userId,
        @RequestParam("shelfID") long shelfId,
        @RequestParam("bookID") long bookId
    ) {
        // pass the parameters to the lower layer (service)
        ManageBookShelfResponse
            detailsOrStatus = shelfService.checkBookshelfValidity(userId, shelfId, bookId);
        // check if server error thrown
        if (detailsOrStatus == null) {
            // server error encountered
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // check if user banned/disallowed
        if (detailsOrStatus instanceof ManageBookShelfResponse403) {
            // user not allowed
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // check if book/user DNE
        if (detailsOrStatus instanceof ManageBookShelfResponse404) {
            // book or user do not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // check if validated by user
        if (detailsOrStatus instanceof ManageBookShelfResponse200) {
            // gather request data
            ManageBookShelfRequest requestData =
                new ManageBookShelfRequest(((ManageBookShelfResponse200) detailsOrStatus).getBookID());
            // build URL
            String targetUrl = restService.buildBookshelfURL(shelfId, userId);
            // send request via RestService
            HttpStatus status = restService.addToMicroservice(targetUrl, requestData);
            // check response
            if (status == HttpStatus.CREATED) { // all okay on both our and other microservice's side: confirm OK
                return ResponseEntity.status(HttpStatus.OK).body(detailsOrStatus);
            } // issue not on our side; return 500 Server Error
        }
        // final return statement: if all fails, INTERNAL_SERVER_ERROR
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Endpoint that allows users to remove books from their collections.
     * Validates a request and passes it to the BookService API s.t. the relation can be updated.
     *
     * @param userId ID of user for whom the book is to be removed
     * @param shelfId ID of shelf from which the book should be removed
     * @param bookId ID of book that should be removed
     * @return Response (200, 403, 404 xor 500) depending on status
     */
    @DeleteMapping("/shelf")
    @ResponseBody
    public ResponseEntity<ManageBookShelfResponse> removeBookFromBookshelf(
        @RequestParam("userID") long userId,
        @RequestParam("shelfID") long shelfId,
        @RequestParam("bookID") long bookId
    ) {
        // pass the parameters to the lower layer (service)
        ManageBookShelfResponse
            detailsOrStatus = shelfService.checkBookshelfValidity(userId, shelfId, bookId);
        // check if server error thrown
        if (detailsOrStatus == null) {
            // server error encountered
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // check if user banned/disallowed
        if (detailsOrStatus instanceof ManageBookShelfResponse403) {
            // user not allowed
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // check if book/user DNE
        if (detailsOrStatus instanceof ManageBookShelfResponse404) {
            // book or user do not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // check if validated by user
        if (detailsOrStatus instanceof ManageBookShelfResponse200) {
            // gather request data
            ManageBookShelfRequest requestData =
                new ManageBookShelfRequest(((ManageBookShelfResponse200) detailsOrStatus).getBookID());
            // build URL
            String targetUrl = restService.buildBookshelfRemoveURL(shelfId, userId, bookId);
            // send request via RestService
            HttpStatus status = restService.removeFromMicroservice(targetUrl, requestData);
            // check response
            if (status == HttpStatus.OK) { // all okay on both our and other microservice's side: confirm OK
                return ResponseEntity.status(HttpStatus.OK).body(detailsOrStatus);
            } // issue not on our side; return 500 Server Error
        }
        // final return statement: if all fails, INTERNAL_SERVER_ERROR
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
