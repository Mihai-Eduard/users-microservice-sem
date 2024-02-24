package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.dtos.LoginUserRequest;
import nl.tudelft.sem.template.example.dtos.RegisterUserRequest;
import nl.tudelft.sem.template.example.dtos.UserIdResponse;
import nl.tudelft.sem.template.example.dtos.UserProfileRequest;
import nl.tudelft.sem.template.example.dtos.UserResponse;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.generic.DoesNotExistResponse404;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.dtos.generic.InternalServerErrorResponse;
import nl.tudelft.sem.template.example.dtos.generic.UserBannedResponse;
import nl.tudelft.sem.template.example.dtos.generic.UserNotFoundResponse;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse403;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse404;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.converters.BannedConverter;
import nl.tudelft.sem.template.example.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

// Because of the request parameter userId being used so much this warning is triggered
// So we need to suppress it because it is incorrect
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@RestController
@RequestMapping("/api")
public class UserController {
    private final transient UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint allowing the registration of a user.
     * Passes the request body to the service running the DB functionality.
     *
     * @param userRequest DTO containing the fields of user creation request
     * @return HTTP Response with a DTO containing the fields of user creation response
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<UserIdResponse> registerNewUser(@RequestBody RegisterUserRequest userRequest) {
        // pass the DTO to the lower layer (services) & attempt to register user
        UserIdResponse userOrStatus = userService.registerUser(userRequest);
        // if registration unsuccessful, return error response
        if (userOrStatus == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // otherwise, build the result DTO and add to response
        UserIdResponse response = new UserIdResponse(userOrStatus.getUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint allowing the login of a user.
     *
     * @param userRequest DTO containing the fields of user login request
     * @return Appropriate response entity
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> loginUser(@RequestBody LoginUserRequest userRequest) {
        try {
            // check if user can successfully login
            final User user = userService.loginUser(userRequest);

            // if user is not found, either due to no user existing or wrong password or email, return 401 unauthorized
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password and/or email address");
            }

            // if user is banned return 403 forbidden
            if (new BannedConverter().convertToDatabaseColumn(user.getBanned())) {
                final UserStatusResponse role = new UserStatusResponse("USER_BANNED");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(role);
            }

            UserIdResponse response = new UserIdResponse(user.getUserId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // An illegal argument was passed somewhere which means a bad request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            // Step 4: Handle internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint allowing a user to change their password.
     *
     * @param requestedPassword plaintext password
     * @param userId ID of user who is requesting the change
     * @return HTTP Response with optional extra information
     */
    @PutMapping("/security/password")
    @ResponseBody
    public ResponseEntity<GenericResponse> changePassword(
        @RequestBody String requestedPassword,
        @RequestParam("userID") long userId
    ) {
        // pass data to lower layer (services)
        GenericResponse response = userService.changeUserPassword(requestedPassword, userId);
        // return status
        if (response instanceof ChangePasswordResponse403) { // user forbidden
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        if (response instanceof ChangePasswordResponse404) { // user not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (response instanceof InternalServerErrorResponse) { // internal server response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint allowing a user to change their profile.
     *
     * @param request DTO containing the fields of user profile change request
     * @param userId ID of user who is requesting the change
     * @return HTTP Response with optional extra information
     */
    @PutMapping("/profile")
    @ResponseBody
    public ResponseEntity<GenericResponse> changeProfile(@RequestBody UserProfileRequest request,
                                                         @RequestParam("userID") long userId) {
        try {
            GenericResponse response = userService.editUserProfile(request, userId);

            if (response instanceof UserNotFoundResponse) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            if (response instanceof UserBannedResponse) {
                final UserStatusResponse role = new UserStatusResponse("USER_BANNED");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(role);
            }

            if (response instanceof InternalServerErrorResponse) {
                throw new RuntimeException("Internal server error");
            }

            return ResponseEntity.ok(new UserIdResponse(userId));
        } catch (IllegalArgumentException e) { // Illegal argument indicates bad request input
            return ResponseEntity.badRequest().build();
        } catch (Exception e) { // Any other exception is caused by a server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint that allows querying for users given a user ID.
     *
     * @param userId ID of user being queried
     * @return 404 if not found, else 200 with body of User
     */
    @GetMapping("/user/{wantedId}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@RequestParam("userID") long userId,
                                            @PathVariable Long wantedId) {

        // call lower layer (service)
        GenericResponse response = userService.getUserById(userId);
        // check if user exists
        if (response instanceof DoesNotExistResponse404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // check if user banned
        if (response instanceof UserBannedResponse) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        GenericResponse responseWanted = userService.getUserById(wantedId);

        if (responseWanted instanceof DoesNotExistResponse404) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(((UserResponse) responseWanted).getUserEntity());
    }

    /**
     * Endpoint that allows toggling the privacy settings of a user.
     *
     * @param userId ID of user whose privacy settings are being toggled
     * @return 404 if not found, else 200 with body of User
     */
    @PutMapping("/changePrivacySettings")
    public ResponseEntity<GenericResponse> changeUserPrivacySettings(@RequestParam("userID") long userId) {

        // Toggle the user's privacy setting and return the newly set privacy setting
        GenericResponse response = userService.changeUserPrivacySettings(userId);

        if (response instanceof UserNotFoundResponse) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (response instanceof UserBannedResponse) {
            final UserStatusResponse role = new UserStatusResponse("USER_BANNED");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(role);
        }

        if (response instanceof InternalServerErrorResponse) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(response);
    }

}

