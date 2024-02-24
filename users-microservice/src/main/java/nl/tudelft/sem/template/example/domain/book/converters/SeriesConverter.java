package nl.tudelft.sem.template.example.domain.book.converters;

import java.util.ArrayList;
import javax.persistence.AttributeConverter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.example.domain.book.Series;

@NoArgsConstructor
public class SeriesConverter implements AttributeConverter<Series, String> {

    /**
     * Converts a 'Series' object into a string, delimited by ','.
     *
     * @param attribute  the entity attribute value to be converted
     * @return the string to be added to the database
     */
    @Override
    public String convertToDatabaseColumn(Series attribute) {
        return String.join(",", attribute.getListSeries());
    }

    /**
     * Converts a string into a 'Series' object.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return the 'Series' object
     */
    @Override
    public Series convertToEntityAttribute(String dbData) {
        ArrayList<String> series = new ArrayList<>();

        for (String elem : dbData.split(",")) {
            series.add(elem);
        }

        return new Series(series);
    }
}
