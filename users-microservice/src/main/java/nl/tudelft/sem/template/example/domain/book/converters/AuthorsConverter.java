package nl.tudelft.sem.template.example.domain.book.converters;

import java.util.ArrayList;
import javax.persistence.AttributeConverter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.example.domain.book.Authors;

@NoArgsConstructor
public class AuthorsConverter implements AttributeConverter<Authors, String> {
    /**
     * Converts an 'Authors' object into a string, delimited by ','.
     *
     * @param attribute  the entity attribute value to be converted
     * @return A string of authors delimited by ','
     */
    @Override
    public String convertToDatabaseColumn(Authors attribute) {
        if (attribute.getListAuthors() == null) {
            return null;
        }

        return String.join(",", attribute.getListAuthors());
    }

    /**
     * Converts a string into an 'Authors' object.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return the 'Authors' object
     */
    @Override
    public Authors convertToEntityAttribute(String dbData) {
        ArrayList<String> authors = new ArrayList<>();

        for (String elem : dbData.split(",")) {
            authors.add(elem);
        }

        return new Authors(authors);
    }
}
