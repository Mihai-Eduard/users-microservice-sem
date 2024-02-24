package nl.tudelft.sem.template.example.modules.user;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class DetailType {
    @Column(name = "bio")
    private String bio;
    @Column(name = "name")
    private String name;
    @Column(name = "location")
    private String location;
    @Column(name = "favouriteBookId")
    private long favouriteBookId;
    @ElementCollection
    private List<String> favouriteGenres;

    public DetailType() {
        this.favouriteGenres = new ArrayList<>();
    }

    /**
     * Constructor of the DetailsType value object.
     *
     * @param bio biography of the user
     * @param name name of the user
     * @param location location of the user
     * @param favouriteBookId Id of the user's favourite book
     * @param favouriteGenres list of the user's favourite genres
     * @throws IllegalArgumentException if any of the input values are invalid
     */
    public DetailType(String bio, String name, String location, long favouriteBookId, List<String> favouriteGenres)
        throws IllegalArgumentException {
        if (bio.isEmpty() || name.isEmpty() || location.isEmpty() || favouriteBookId < 0) {
            throw new IllegalArgumentException();
        } else {
            this.bio = bio;
            this.name = name;
            this.location = location;
            this.favouriteBookId = favouriteBookId;
            this.favouriteGenres = favouriteGenres;
        }
    }
}
