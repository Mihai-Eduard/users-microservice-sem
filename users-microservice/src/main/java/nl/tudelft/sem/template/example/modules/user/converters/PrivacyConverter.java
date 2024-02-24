package nl.tudelft.sem.template.example.modules.user.converters;

import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;

public class PrivacyConverter implements AttributeConverter<PrivacyType, Boolean> {
    /**
     * Converts the value object into a string to be stored in the database.
     *
     * @param attribute the entity attribute value to be converted
     * @return boolean representation of the object
     */
    @Override
    public Boolean convertToDatabaseColumn(PrivacyType attribute) {
        return attribute.isEnableCollection();
    }

    /**
     * Creates a new PrivacyType created from data from the database.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return new PrivacyType
     */
    @Override
    public PrivacyType convertToEntityAttribute(Boolean dbData) {
        return new PrivacyType(dbData);
    }
}
