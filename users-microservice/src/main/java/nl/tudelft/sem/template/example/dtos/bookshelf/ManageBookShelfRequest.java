package nl.tudelft.sem.template.example.dtos.bookshelf;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ManageBookShelfRequest {

    private final long bookId;

    public ManageBookShelfRequest(long bookId) {
        this.bookId = bookId;
    }
}
