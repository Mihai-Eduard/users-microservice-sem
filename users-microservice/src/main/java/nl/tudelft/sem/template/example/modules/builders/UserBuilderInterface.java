package nl.tudelft.sem.template.example.modules.builders;

import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;


public interface UserBuilderInterface {

    void setBanned(BannedType bannedType);

    void setDetails(DetailType detailType);

    void setEmail(EmailType emailType);

    void setFollowing(FollowingType followingType);

    void setPassword(PasswordType passwordType);

    void setPrivacy(PrivacyType privacyType);

    void setRole(UserEnumType role);

    void setUsername(UsernameType usernameType);

    User build();
}
