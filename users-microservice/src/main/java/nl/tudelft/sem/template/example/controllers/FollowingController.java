package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse403;
import nl.tudelft.sem.template.example.services.FollowingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FollowingController {

    private final transient FollowingService followingService;

    /**
     * Constructor of the FollowingController.
     *
     * @param followingService the injected instance of FollowingService
     */
    public FollowingController(FollowingService followingService) {
        this.followingService = followingService;
    }

    /**
     * Endpoint allowing the user with userID to follow the user with wantedID.
     *
     * @param wantedID ID of the user to be followed
     * @param userID ID of the user making the request
     * @return HTTP Response with optional extra information
     */
    @PutMapping("/follow/{wantedID}")
    @ResponseBody
    public ResponseEntity<GenericResponse> follow(
            @PathVariable long wantedID,
            @RequestParam("userID") long userID) {

        // pass the data onto the service layer, and get the status of the result
        HttpStatus responseStatus = followingService.followUser(userID, wantedID);

        // if the user is banned, a response body is needed
        // reuse Karol's ChangePasswordResponse403
        if (responseStatus == HttpStatus.FORBIDDEN) {
            return ResponseEntity.status(responseStatus).body(new ChangePasswordResponse403("USER_BANNED"));
        }

        // otherwise, return the response
        return ResponseEntity.status(responseStatus).build();
    }

    /**
     * Endpoint allowing the user with userID to unfollow the user with wantedID.
     *
     * @param wantedID ID of the user to be unfollowed
     * @param userID ID of the user making the request
     * @return HTTP Response with optional extra information
     */
    @PutMapping("/unfollow/{wantedID}")
    @ResponseBody
    public ResponseEntity<GenericResponse> unfollow(
            @PathVariable long wantedID,
            @RequestParam("userID") long userID) {

        // pass the data onto the service layer, and get the status of the result
        HttpStatus responseStatus = followingService.unfollowUser(userID, wantedID);

        // if the user is banned, a response body is needed
        // reuse Karol's ChangePasswordResponse403
        if (responseStatus == HttpStatus.FORBIDDEN) {
            return ResponseEntity.status(responseStatus).body(new ChangePasswordResponse403("USER_BANNED"));
        }

        // otherwise, return the response
        return ResponseEntity.status(responseStatus).build();
    }
}
