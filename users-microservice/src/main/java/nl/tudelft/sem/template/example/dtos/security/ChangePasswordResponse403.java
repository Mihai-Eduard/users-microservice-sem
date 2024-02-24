package nl.tudelft.sem.template.example.dtos.security;

import lombok.Data;

@Data
public class ChangePasswordResponse403 implements ChangePasswordResponse {
    private String role;

    public ChangePasswordResponse403(String role) {
        this.role = role;
    }
}
