package nl.tudelft.sem.template.example.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class RegisterUserRequest {
    private final String email;
    private final String password;
    private final String username;

    /**
     * Returns the email of the user creation request.
     *
     * @return String email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the password of the user creation request.
     *
     * @return String password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username of the user creation request.
     *
     * @return String username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Serves as the Data Transfer Object for accepting a request for registering a user.
     *
     * @param email email of the user being added
     * @param password password of the user being added
     * @param username username of the user being added
     */
    public RegisterUserRequest(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }
}