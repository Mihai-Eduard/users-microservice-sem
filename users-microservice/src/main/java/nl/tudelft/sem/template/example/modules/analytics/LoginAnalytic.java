package nl.tudelft.sem.template.example.modules.analytics;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "loginanalytic")
public class LoginAnalytic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long userId;
    private long timestamp;

    /**
     * No argument constructor, needed for JPA.
     */
    public LoginAnalytic() {
    }

    /**
     * Constructor for LoginAnalytic.
     *
     * @param userId the id of the user
     * @param timestamp the timestamp of the login
     */
    public LoginAnalytic(long userId, long timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }
}
