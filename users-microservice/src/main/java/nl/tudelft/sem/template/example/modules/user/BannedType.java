package nl.tudelft.sem.template.example.modules.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BannedType {
    private boolean isBanned;

    /**
     * Constructor of the BannedType value object.
     *
     * @param isBanned flag indicating whether the user is banned
     */
    public BannedType(boolean isBanned) {
        this.isBanned = isBanned;
    }
}
