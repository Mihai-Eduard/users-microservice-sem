package nl.tudelft.sem.template.example.modules.builders;

import lombok.Setter;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;

@Setter
public class UserBuilder implements UserBuilderInterface {
    private BannedType banned;
    private DetailType details;
    private EmailType email;
    private FollowingType following;
    private PasswordType password;
    private PrivacyType privacy;
    private UserEnumType role;
    private UsernameType username;

    /**
     * Builds and returns a User object constructed with the parameters of this builder.
     *
     * @return the new User instance
     */
    public User build() {
        return new User(username, email, password, banned, privacy, role, details, following);
    }
}
