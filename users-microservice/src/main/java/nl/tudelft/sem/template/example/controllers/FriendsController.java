package nl.tudelft.sem.template.example.controllers;

import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.FollowingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FriendsController {
    private final transient FollowingService followingService;
    private final transient UserRepository userRepository;

    /**
     * Constructor of the FriendsController.
     *
     * @param followingService the injected FollowingService
     * @param userRepository the injected UserRepository
     */
    public FriendsController(FollowingService followingService, UserRepository userRepository) {
        this.followingService = followingService;
        this.userRepository = userRepository;
    }

    /**
     * Get all friends of a user (where both users follow each other).
     *
     * @param userID the ID of the user making the request
     * @param wantedID the ID of the user whose friends are being retrieved
     * @return
     *     <ul>
     *         <li>ResponseEntity with code 200 if successful, along with the list of specific friends</li>
     *         <li>ResponseEntity with code 403 if the user that made a request is banned</li>
     *         <li>ResponseEntity with code 404 if the user that made the request or the user for whom
     *         the friends are wanted do not exist</li>
     *         <li>ResponseEntity with code 500 if other error occurred (e.g., server error, database error)</li>
     *     </ul>
     */
    @GetMapping("/friends/{wantedID}")
    public ResponseEntity<Object> getFriends(@RequestParam("userID") Long userID,
                                             @PathVariable Long wantedID) {
        if (userID == null || wantedID == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            User user = userRepository.findById(userID).orElseThrow();
            if (user.getBanned().isBanned()) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("USER_BANNED"));
            }

            User wantedUser = userRepository.findById(wantedID).orElseThrow();
            List<Long> friends = followingService.getFriends(wantedUser);
            return ResponseEntity.ok(friends);

        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
