package nl.tudelft.sem.template.example.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.modules.user.User;

@Getter
@EqualsAndHashCode
public class UserResponse implements GenericResponse {
    private User userEntity;

    public UserResponse(User userEntity) {
        this.userEntity = userEntity;
    }
}
