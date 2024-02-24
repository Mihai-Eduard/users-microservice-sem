package nl.tudelft.sem.template.example.domain.book;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for Genre value object.
 */
@Data
@NoArgsConstructor
public class Genres {
    private List<Genre> genresList;

    /**
     * Constructor for the Genres class.
     *
     * @param genres the list of genres
     * @throws IllegalArgumentException in case the list of genres is empty or null
     */
    public Genres(List<Genre> genres) throws IllegalArgumentException {
        if (genres != null && !genres.isEmpty()) {
            this.genresList = genres;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
