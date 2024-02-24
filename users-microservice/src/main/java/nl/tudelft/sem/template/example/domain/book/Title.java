package nl.tudelft.sem.template.example.domain.book;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for Title value object.
 */
@Data
@NoArgsConstructor
public class Title {
    private String bookTitle;

    /**
     * Constructor for the Title class.
     *
     * @param title the title of the book
     * @throws IllegalArgumentException in case the title is empty or null
     */
    public Title(String title) throws IllegalArgumentException {
        if (title != null && !title.isEmpty()) {
            this.bookTitle = title;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
