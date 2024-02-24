package nl.tudelft.sem.template.example.dtos.bookshelf;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode
public class ManageBookShelfResponse200 implements ManageBookShelfResponse {

    @SuppressWarnings("")
    private long shelfID;
    @SuppressWarnings("")
    private long bookID;

    public ManageBookShelfResponse200(long shelfID, long bookID) {
        this.shelfID = shelfID;
        this.bookID = bookID;
    }
}
