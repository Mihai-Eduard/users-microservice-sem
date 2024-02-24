package nl.tudelft.sem.template.example.domain.book.converters;

import javax.persistence.AttributeConverter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.example.domain.book.NumPage;

@NoArgsConstructor
public class NumPageConverter implements AttributeConverter<NumPage, Integer> {

    /**
     * Converts a 'NumPage' object to an Integer.
     *
     * @param attribute  the entity attribute value to be converted
     * @return the Integer to be added to the database
     */
    @Override
    public Integer convertToDatabaseColumn(NumPage attribute) {
        return attribute.getPageNum();
    }

    /**
     * Converts an Integer into a 'NumPage' object.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return the 'NumPage' object from the database
     */
    @Override
    public NumPage convertToEntityAttribute(Integer dbData) {
        return new NumPage(dbData);
    }
}
