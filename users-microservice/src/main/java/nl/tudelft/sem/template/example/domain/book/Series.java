package nl.tudelft.sem.template.example.domain.book;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for Series value object.
 */
@Data
@NoArgsConstructor
public class Series {
    private List<String> listSeries;

    /**
     * Constructor for the Series class.
     *
     * @param listSeries A list of series
     * @throws IllegalArgumentException In case the list is empty or null
     */
    public Series(List<String> listSeries) throws IllegalArgumentException {
        if (listSeries != null && !listSeries.isEmpty()) {
            this.listSeries = listSeries;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
