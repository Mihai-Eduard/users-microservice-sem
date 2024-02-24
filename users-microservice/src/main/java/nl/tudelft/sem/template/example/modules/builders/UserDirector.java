package nl.tudelft.sem.template.example.modules.builders;

import java.util.ArrayList;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;

public class UserDirector {
    private final transient UserBuilderInterface userBuilder;

    /**
     * Constructor of the UserDirector class.
     *
     * @param userBuilder the user builder to be used
     */
    public UserDirector(UserBuilderInterface userBuilder) {
        this.userBuilder = userBuilder;
    }

    /**
     * Constructs a new User.
     */
    public void constructValidUser() {
        setCommonFields();
        userBuilder.setBanned(new BannedType(false));
        userBuilder.setRole(new UserEnumType("USER"));
        userBuilder.setUsername(new UsernameType("exampleUser"));
    }

    /**
     * Constructs a new banned User.
     */
    public void constructBannedUser() {
        setCommonFields();
        userBuilder.setBanned(new BannedType(true));
        userBuilder.setRole(new UserEnumType("USER"));
        userBuilder.setUsername(new UsernameType("exampleBannedUser"));
    }

    /**
     * Constructs a new author.
     */
    public void constructAuthor() {
        setCommonFields();
        userBuilder.setBanned(new BannedType(false));
        userBuilder.setRole(new UserEnumType("AUTHOR"));
        userBuilder.setUsername(new UsernameType("exampleAuthor"));
    }

    /**
     * Constructs a new admin.
     */
    public void constructAdmin() {
        setCommonFields();
        userBuilder.setBanned(new BannedType(false));
        userBuilder.setRole(new UserEnumType("ADMIN"));
        userBuilder.setUsername(new UsernameType("exampleAdmin"));
    }

    /**
     * Sets the fields of the Builder which are common to all the constructed User objects.
     */
    private void setCommonFields() {
        userBuilder.setDetails(new DetailType());
        userBuilder.setEmail(new EmailType("exampleEmail@foo.com"));
        userBuilder.setFollowing(new FollowingType(new ArrayList<>()));
        userBuilder.setPassword(new PasswordType("exampleHashedPassword"));
        userBuilder.setPrivacy(new PrivacyType(false));
    }
}
