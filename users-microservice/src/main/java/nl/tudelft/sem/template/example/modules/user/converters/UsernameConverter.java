package nl.tudelft.sem.template.example.modules.user.converters;

import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.example.modules.user.UsernameType;

public class UsernameConverter implements AttributeConverter<UsernameType, String> {

    /**
     * Converts the value object into a string to be stored in the database.
     *
     * @param attribute  the entity attribute value to be converted
     * @return string representation of the object
     */
    @Override
    public String convertToDatabaseColumn(UsernameType attribute) {
        return attribute.getUsername();
    }

    /**
     * Creates a new UsernameType created from data from the database.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return new UsernameType
     */
    @Override
    public UsernameType convertToEntityAttribute(String dbData) {
        return new UsernameType(dbData);
    }
}
