package nl.tudelft.sem.template.example.dtos;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;

/**
 * The DTO (Data Transfer Object) used for returning the status of the user
 * after an action requested by it was forbidden.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class UserStatusResponse implements GenericResponse {
    private String role;
}

