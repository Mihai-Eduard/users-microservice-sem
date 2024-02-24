package nl.tudelft.sem.template.example.modules.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailType {
    private String email;

    /**
     * Constructor for the EmailType value object.
     *
     * @param email email
     * @throws IllegalArgumentException if the email is invalid
     */
    public EmailType(String email) throws IllegalArgumentException {
        if (!email.isEmpty()) {
            this.email = email;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
