package nl.tudelft.sem.template.example.builders;

import java.util.List;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.Genre;

public interface BookBuilderInterface {
    BookBuilderInterface setCreator(long creatorId);

    BookBuilderInterface setTitle(String title);

    BookBuilderInterface setGenres(List<Genre> genres);

    BookBuilderInterface setAuthors(List<String> authors);

    BookBuilderInterface setSeries(List<String> series);

    BookBuilderInterface setNumPages(int numPages);

    Book build();
}
