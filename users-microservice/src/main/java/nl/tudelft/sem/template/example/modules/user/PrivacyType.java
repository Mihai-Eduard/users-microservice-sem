package nl.tudelft.sem.template.example.modules.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PrivacyType {
    private boolean enableCollection;

    /**
     * Constructor of the PrivacyType value object.
     *
     * @param enableCollection flag indicating whether the collection of analytics is enabled for this account
     */
    public PrivacyType(boolean enableCollection) {
        this.enableCollection = enableCollection;
    }
}
