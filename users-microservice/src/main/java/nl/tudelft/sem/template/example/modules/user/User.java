package nl.tudelft.sem.template.example.modules.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.example.modules.user.converters.BannedConverter;
import nl.tudelft.sem.template.example.modules.user.converters.EmailConverter;
import nl.tudelft.sem.template.example.modules.user.converters.PasswordConverter;
import nl.tudelft.sem.template.example.modules.user.converters.PrivacyConverter;
import nl.tudelft.sem.template.example.modules.user.converters.UserEnumConverter;
import nl.tudelft.sem.template.example.modules.user.converters.UsernameConverter;

@Data
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;

    @Column(name = "username")
    @Convert(converter = UsernameConverter.class)
    private UsernameType username;

    @Column(name = "email")
    @Convert(converter = EmailConverter.class)
    private EmailType email;

    @Column(name = "password")
    @Convert(converter = PasswordConverter.class)
    private PasswordType password;

    @Column(name = "banned")
    @Convert(converter = BannedConverter.class)
    private BannedType banned;

    @Column(name = "privacy")
    @Convert(converter = PrivacyConverter.class)
    private PrivacyType privacy;

    @Column(name = "role")
    @Convert(converter = UserEnumConverter.class)
    private UserEnumType role;

    @Embedded
    private DetailType details;

    @Embedded
    @JsonIgnore
    private FollowingType following;

    /**
     * No argument constructor, needed for JPA.
     */
    public User() {}

    /**
     * Constructor of the User entity.
     *
     * @param username username value object
     * @param email email value object
     * @param password password value object
     * @param banned banned flag value object
     * @param privacy privacy flag value object
     * @param role role enum value object
     * @param details profile details value object
     * @param following following value object
     */
    public User(UsernameType username,
                EmailType email,
                PasswordType password,
                BannedType banned,
                PrivacyType privacy,
                UserEnumType role,
                DetailType details,
                FollowingType following) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.banned = banned;
        this.privacy = privacy;
        this.role = role;
        this.details = details;
        this.following = following;
    }

    @Override
    public String toString() {
        return "User{"
                +
                "userId=" + userId
                +
                ", username=" + username
                +
                ", email=" + email
                +
                ", password=" + password
                +
                ", banned=" + banned
                +
                ", privacy=" + privacy
                +
                ", role=" + role
                +
                ", details=" + details
                +
                '}';
    }
}
