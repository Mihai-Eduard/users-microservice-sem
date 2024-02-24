package nl.tudelft.sem.template.example.modules.user.converters;

import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.example.modules.user.EmailType;

public class EmailConverter implements AttributeConverter<EmailType, String> {
    /**
     * Converts the value object into a string to be stored in the database.
     *
     * @param attribute  the entity attribute value to be converted
     * @return string representation of the object
     */
    @Override
    public String convertToDatabaseColumn(EmailType attribute) {
        return attribute.getEmail();
    }

    /**
     * Creates a new EmailType created from data from the database.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return new EmailType
     */
    @Override
    public EmailType convertToEntityAttribute(String dbData) {
        return new EmailType(dbData);
    }
}
