package nl.tudelft.sem.template.example.modules.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEnumType {
    private String userRole;

    /**
     * Constructor of the UserEnumType value object.
     *
     * @param userRole role of the user, must be either USER, AUTHOR or ADMIN
     * @throws IllegalArgumentException if the user role is invalid
     */
    public UserEnumType(String userRole) throws IllegalArgumentException {
        if (userRole.equals("USER") || userRole.equals("AUTHOR") || userRole.equals("ADMIN")) {
            this.userRole = userRole;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
