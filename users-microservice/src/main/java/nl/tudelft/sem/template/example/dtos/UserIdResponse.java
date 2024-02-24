package nl.tudelft.sem.template.example.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;

@Getter
@EqualsAndHashCode
public class UserIdResponse implements GenericResponse {
    private final long userId;

    public UserIdResponse(long userId) {
        this.userId = userId;
    }
}

