package nl.tudelft.sem.template.example.dtos.bookshelf;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ManageBookShelfResponse403 implements ManageBookShelfResponse {
    private String role;

    public ManageBookShelfResponse403(String role) {
        this.role = role;
    }
}
