package nl.tudelft.sem.template.example.modules.analytics;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "bookanalytic")
public class BookAnalytic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long bookId;
    private long timestamp;

    /**
     * No argument constructor, needed for JPA.
     */
    public BookAnalytic() {
    }

    /**
     * Constructor for BookAnalytic.
     *
     * @param bookId the id of the book
     * @param timestamp the timestamp of the book fetch
     */
    public BookAnalytic(long bookId, long timestamp) {
        this.bookId = bookId;
        this.timestamp = timestamp;
    }
}
