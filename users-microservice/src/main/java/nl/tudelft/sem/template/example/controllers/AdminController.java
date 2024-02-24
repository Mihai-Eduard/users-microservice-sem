package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.dtos.AddAdminRequest;
import nl.tudelft.sem.template.example.dtos.UserResponse;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.converters.BannedConverter;
import nl.tudelft.sem.template.example.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class AdminController {

    private final transient String internalServerError = "Internal Server Error";

    @Autowired
    private transient AdminService adminService;

    private final transient String userIdString = "userID";

    /**
     * PUT request to upgrade a normal user to author privileges
     * When a user is an author, they can add books and access endpoints related to this.
     *
     * @param wantedId the user that will be granted author privileges
     * @param userId the user that does the request and wantedId
     * @return the new user
     */
    @PutMapping("/addAuthor/{wantedId}")
    public ResponseEntity<UserStatusResponse> upgradeToAuthor(
            @PathVariable Long wantedId,
            @RequestParam(userIdString) Long userId) {
        try {

            // Step 1: Check if userId has admin privileges
            if (!adminService.isAdmin(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("NOT_AN_ADMIN"));
            }

            // Step 2: Check if wantedId exists
            User wantedUser = adminService.getUserById(wantedId);
            if (wantedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new UserStatusResponse("User not found"));
            }

            // Step 3: Check if wantedId is banned
            if (adminService.isBanned(wantedId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("REQUESTED_USER_BANNED"));
            }

            adminService.grantAuthorPrivileges(wantedUser);

            return ResponseEntity.ok(new UserStatusResponse("Author privileges granted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserStatusResponse(internalServerError));
        }
    }

    /**
     * PUT request to set the ban flag for a certain user as banned.
     *
     * @param wantedId the user that will be banned
     * @param userId   the user that does the request
     * @return ResponseEntity indicating the result of the ban operation
     */
    @PutMapping("/banUser/{wantedId}")
    public ResponseEntity<UserStatusResponse> banUser(
            @PathVariable Long wantedId,
            @RequestParam(userIdString) Long userId) {
        try {
            // Step 1: Check if the wantedId exists
            User wantedUser = adminService.getUserById(wantedId);
            if (wantedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new UserStatusResponse("User Not Found"));
            }

            // Step 2: Check if the userId is an admin
            if (!adminService.isAdmin(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("Unauthorized User - NOT_AN_ADMIN"));
            }

            // Step 3: Set the ban flag for the wantedId user
            adminService.banUser(wantedUser);

            return ResponseEntity.ok(new UserStatusResponse("User Banned Successfully"));
        } catch (Exception e) {
            // Step 4: Handle internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserStatusResponse(internalServerError));
        }
    }

    /**
     * PUT request to set the ban flag for a certain user as unbanned.
     *
     * @param wantedId the user to unban
     * @param userId   the user making the request
     * @return ResponseEntity indicating the result of the unban operation
     */
    @PutMapping("/unbanUser/{wantedId}")
    public ResponseEntity<UserStatusResponse> unbanUser(
            @PathVariable Long wantedId,
            @RequestParam(userIdString) Long userId) {
        try {
            // Step 1: Check if the wantedId exists
            User wantedUser = adminService.getUserById(wantedId);
            if (wantedUser == null) {
                throw new UserNotFoundException();
            }

            // Step 2: Check if the userId is an admin
            if (!adminService.isAdmin(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new UserStatusResponse("Unauthorized User - NOT_AN_ADMIN"));
            }

            // Step 3: Check if the user is banned
            if (!new BannedConverter().convertToDatabaseColumn(wantedUser.getBanned())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserStatusResponse("User is not banned"));
            }

            // Step 4: Unban the user
            adminService.unbanUser(wantedUser);

            return ResponseEntity.ok(new UserStatusResponse("User Unbanned Successfully"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new UserStatusResponse("User Not Found"));
        } catch (Exception e) {
            // Handle internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserStatusResponse(internalServerError));
        }
    }

    /**
     * addAdmin endpoint is handled.
     * Knowing the secret password a user may add himself admin
     * privilege by calling this endpoint
     *
     * @param userId the user that will update its rights
     * @param passwordRequest the password sent
     * @return a response entity according to how the processs went
     */
    @PostMapping("/addAdmin")
    public ResponseEntity<UserStatusResponse> addAdmin(
            @RequestParam(userIdString) Long userId,
            @RequestBody AddAdminRequest passwordRequest) {
        try {
            if (adminService.isAdmin(userId)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new UserStatusResponse("Already an Admin"));
            }

            if (adminService.isBanned(userId)) {
                throw new UserBannedException();
            }

            if (!adminService.authenticateAdmin(passwordRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new UserStatusResponse("Bad request (incorrect password)"));
            }
            adminService.addAdmin(userId);

            return ResponseEntity.ok(new UserStatusResponse("User is now an Admin"));
        } catch (UserBannedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new UserStatusResponse("Unauthorized User - USER_BANNED"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UserStatusResponse(internalServerError));
        }
    }
}