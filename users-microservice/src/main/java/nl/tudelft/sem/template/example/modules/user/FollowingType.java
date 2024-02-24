package nl.tudelft.sem.template.example.modules.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Embeddable
public class FollowingType {
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<User> followedUsers;

    public FollowingType() {
        this.followedUsers = new ArrayList<>();
    }

    /**
     * Constructor of the FollowingType.
     *
     * @param followedUsers list of userIDs of the users this account follows
     */
    public FollowingType(List<User> followedUsers) {
        this.followedUsers = followedUsers;
    }

    /**
     * Checks whether the list of followed users contains the provided ID.
     *
     * @param wantedUser user to check for
     * @return whether the user with wantedID is already being followed
     */
    public boolean follows(User wantedUser) {
        return this.followedUsers.contains(wantedUser);
    }
}
