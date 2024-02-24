package nl.tudelft.sem.template.example.modules.user.converters;

import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.example.modules.user.PasswordType;

public class PasswordConverter implements AttributeConverter<PasswordType, String> {
    /**
     * Converts the value object into a string to be stored in the database.
     *
     * @param attribute  the entity attribute value to be converted
     * @return string representation of the object
     */
    @Override
    public String convertToDatabaseColumn(PasswordType attribute) {
        return attribute.getPassword();
    }

    /**
     * Creates a new PasswordType created from data from the database.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return new PasswordType
     */
    @Override
    public PasswordType convertToEntityAttribute(String dbData) {
        return new PasswordType(dbData);
    }
}
