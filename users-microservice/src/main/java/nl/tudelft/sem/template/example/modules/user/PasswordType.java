package nl.tudelft.sem.template.example.modules.user;

import javax.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PasswordType {
    private String password;

    /**
     * Constructor for the PasswordType value object.
     *
     * @param password password
     * @throws IllegalArgumentException if the password is invalid
     */
    public PasswordType(String password) throws IllegalArgumentException {
        if (!password.isEmpty()) {
            this.password = password;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
