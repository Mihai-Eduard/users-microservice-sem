package nl.tudelft.sem.template.example.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class LoginUserRequest {
    private final String email;
    private final String password;

    /**
     * Serves as the Data Transfer Object for accepting a request for a user login in.
     *
     * @param email email of the user being added
     * @param password password of the user being added
     */
    public LoginUserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
