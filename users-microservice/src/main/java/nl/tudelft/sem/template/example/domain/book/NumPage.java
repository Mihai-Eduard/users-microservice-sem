package nl.tudelft.sem.template.example.domain.book;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for NumPage (number of pages) value object.
 */
@Data
@NoArgsConstructor
public class NumPage {
    private int pageNum;

    /**
     * Constructor for NumPage class.
     *
     * @param pages the number of pages
     * @throws IllegalArgumentException in case the number provided is less than or equal to 0
     */
    public NumPage(int pages) throws IllegalArgumentException {
        if (pages > 0) {
            this.pageNum = pages;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
