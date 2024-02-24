package nl.tudelft.sem.template.example.controllers;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.example.dtos.UserDetailResponse;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.services.UserSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserSearchController {

    private final transient UserSearchService userSearchService;

    public UserSearchController(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }

    /**
     * Handles the GET request for searching users based on given parameters.
     * (Only one optional parameter should be used - rest of them should be null)
     *
     * @param userId          The ID of the user making the request (required).
     * @param username        The username of the user to be found (optional).
     * @param favoriteBook    The name of the favorite book of the user to be found (optional).
     * @param friendUsername  The username of a friend of the user being searched (optional).
     * @return                ResponseEntity with a list of users matching the search criteria if found.
     *                        Returns 404 if no users match the criteria, and 500 for internal server errors.
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserDetailResponse>> searchUsers(
            @RequestParam("userID") long userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String favoriteBook,
            @RequestParam(required = false) String friendUsername) {
        try {
            List<User> searchResults = userSearchService.searchUsers(userId, username, favoriteBook, friendUsername);

            List<UserDetailResponse> results = new ArrayList<>();
            for (User user : searchResults) {
                results.add(new UserDetailResponse(user.getUsername().getUsername(), user.getDetails().getBio(),
                        user.getDetails().getLocation(), user.getDetails().getFavouriteBookId(),
                        null, user.getDetails().getFavouriteGenres()));
            }

            if (results.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok(results);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (UserBannedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
