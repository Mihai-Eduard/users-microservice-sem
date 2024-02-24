package nl.tudelft.sem.template.example.modules.user.converters;

import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;

public class UserEnumConverter implements AttributeConverter<UserEnumType, String> {

    /**
     * Converts the value object into a string to be stored in the database.
     *
     * @param attribute  the entity attribute value to be converted
     * @return string representation of the object
     */
    @Override
    public String convertToDatabaseColumn(UserEnumType attribute) {
        return attribute.getUserRole();
    }

    /**
     * Creates a new UserEnumType created from data from the database.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return new UserEnumType
     */
    @Override
    public UserEnumType convertToEntityAttribute(String dbData) {
        return new UserEnumType(dbData);
    }
}
