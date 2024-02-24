package nl.tudelft.sem.template.example.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import nl.tudelft.sem.template.example.modules.builders.UserBuilder;
import nl.tudelft.sem.template.example.modules.builders.UserBuilderInterface;
import nl.tudelft.sem.template.example.modules.builders.UserDirector;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserBuilderTest {

    UserDirector userDirector;
    UserBuilderInterface userBuilder;

    /**
     * Setting up the components under test before each test.
     */
    @BeforeEach
    public void setup() {
        this.userBuilder = new UserBuilder();
        this.userDirector = new UserDirector(this.userBuilder);
    }

    @Test
    public void testNormalUser() {
        userDirector.constructValidUser();
        User constructed = userBuilder.build();

        assertEquals(new User(
                new UsernameType("exampleUser"),
                new EmailType("exampleEmail@foo.com"),
                new PasswordType("exampleHashedPassword"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType(new ArrayList<>())
                ), constructed);
    }

    @Test
    public void testBannedUser() {
        userDirector.constructBannedUser();
        User constructed = userBuilder.build();

        assertEquals(new User(
                new UsernameType("exampleBannedUser"),
                new EmailType("exampleEmail@foo.com"),
                new PasswordType("exampleHashedPassword"),
                new BannedType(true),
                new PrivacyType(false),
                new UserEnumType("USER"),
                new DetailType(),
                new FollowingType(new ArrayList<>())
        ), constructed);
    }

    @Test
    public void testAuthor() {
        userDirector.constructAuthor();
        User constructed = userBuilder.build();

        assertEquals(new User(
                new UsernameType("exampleAuthor"),
                new EmailType("exampleEmail@foo.com"),
                new PasswordType("exampleHashedPassword"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("AUTHOR"),
                new DetailType(),
                new FollowingType(new ArrayList<>())
        ), constructed);
    }

    @Test
    public void testAdmin() {
        userDirector.constructAdmin();
        User constructed = userBuilder.build();

        assertEquals(new User(
                new UsernameType("exampleAdmin"),
                new EmailType("exampleEmail@foo.com"),
                new PasswordType("exampleHashedPassword"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("ADMIN"),
                new DetailType(),
                new FollowingType(new ArrayList<>())
        ), constructed);
    }
}
