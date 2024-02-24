package nl.tudelft.sem.template.example.dtos;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class PrivacySettingResponse implements GenericResponse {
    private final boolean enableCollection;
}
