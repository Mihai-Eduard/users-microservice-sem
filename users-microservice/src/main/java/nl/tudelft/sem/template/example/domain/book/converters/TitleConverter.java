package nl.tudelft.sem.template.example.domain.book.converters;

import javax.persistence.AttributeConverter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.example.domain.book.Title;

@NoArgsConstructor
public class TitleConverter implements AttributeConverter<Title, String> {

    /**
     * Converts a 'Title' object into a String.
     *
     * @param attribute  the entity attribute value to be converted
     * @return the String to be added to the database
     */
    @Override
    public String convertToDatabaseColumn(Title attribute) {
        return attribute.getBookTitle();
    }

    /**
     * Converts a String into a 'Title' object.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return the 'Title' object from the database
     */
    @Override
    public Title convertToEntityAttribute(String dbData) {
        return new Title(dbData);
    }
}
