package nl.tudelft.sem.template.example.modules.user.converters;

import javax.persistence.AttributeConverter;
import nl.tudelft.sem.template.example.modules.user.BannedType;

public class BannedConverter implements AttributeConverter<BannedType, Boolean> {
    /**
     * Converts the value object into a string to be stored in the database.
     *
     * @param attribute  the entity attribute value to be converted
     * @return string representation of the object
     */
    @Override
    public Boolean convertToDatabaseColumn(BannedType attribute) {
        return attribute.isBanned();
    }

    /**
     * Creates a new BannedType created from data from the database.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return new BannedType
     */
    @Override
    public BannedType convertToEntityAttribute(Boolean dbData) {
        return new BannedType(dbData);
    }
}
