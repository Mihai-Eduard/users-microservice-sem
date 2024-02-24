package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.dtos.UserResponse;
import nl.tudelft.sem.template.example.dtos.VerifyResponse;
import nl.tudelft.sem.template.example.dtos.generic.DoesNotExistResponse404;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify")
public class VerifyUserController {

    private final transient UserService userService;

    public VerifyUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the role of a user based on the given userID.
     *
     * @param userId The ID of the user for whom the role is to be retrieved.
     * @return ResponseEntity containing the user's role if found, or 404 if the user is not found.
     */
    @GetMapping("/{userID}")
    public ResponseEntity<VerifyResponse> verifyUserRole(@PathVariable("userID") long userId) {
        try {

            GenericResponse userResponse = userService.getUserById(userId);
            if (userResponse instanceof DoesNotExistResponse404) {
                throw new UserNotFoundException();
            }

            User user = ((UserResponse) userResponse).getUserEntity();


            String role = user.getRole().getUserRole();
            // We'll check if the role is one of the expected values
            String userString = "USER";
            String adminString = "ADMIN";
            String authorString = "AUTHOR";

            if (userString.equals(role) || adminString.equals(role) || authorString.equals(role)) {
                return ResponseEntity.ok(new VerifyResponse(role));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new VerifyResponse("Invalid user role"));
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new VerifyResponse("User Not Found"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new VerifyResponse("Error retrieving user role"));
        }
    }
}
