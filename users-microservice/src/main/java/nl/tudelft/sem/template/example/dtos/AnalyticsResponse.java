package nl.tudelft.sem.template.example.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;

/** Analytics response object.
 * The analytics response object contains the following information:
 * - Top 3 Popular genres (most favorite genres from users)
 * - Top 3 Popular books (most favorite books from users)
 * - Login activity (how many times there have been a login in the past 24 hours)
 * - User engagement (how many times books have been fetched in the past 24 hours)
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class AnalyticsResponse implements GenericResponse {
    private final List<String> popularGenres;
    private final List<Book> popularBooks;
    private final long loginActivity;
    private final long userEngagement;
}
