package nl.tudelft.sem.template.example.modules.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsernameType {
    private String username;

    /**
     * Constructor for the UsernameType value object.
     *
     * @param username username
     * @throws IllegalArgumentException if the username is invalid
     */
    public UsernameType(String username) throws IllegalArgumentException {

        if (username.isBlank() || username.isEmpty()) {
            throw new IllegalArgumentException();
        } else if (!username.matches("[a-zA-z0-9]+")) {
            throw new IllegalArgumentException();
        } else if (!(username.charAt(0) + "").matches("[a-zA-z]")) {
            throw new IllegalArgumentException();
        } else {
            this.username = username;
        }
    }
}
