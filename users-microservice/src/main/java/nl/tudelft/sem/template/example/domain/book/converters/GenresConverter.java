package nl.tudelft.sem.template.example.domain.book.converters;

import java.util.ArrayList;
import java.util.Locale;
import javax.persistence.AttributeConverter;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.example.domain.book.Genre;
import nl.tudelft.sem.template.example.domain.book.Genres;

@NoArgsConstructor
public class GenresConverter implements AttributeConverter<Genres, String> {
    /**
     * Converts a 'Genres' object into a String of genres.
     *
     * @param attribute  the entity attribute value to be converted
     * @return the String of genres to be added to the database
     */
    @Override
    public String convertToDatabaseColumn(Genres attribute) {
        if (attribute.getGenresList() == null) {
            return null;
        }

        ArrayList<String> genres = new ArrayList<>();

        for (Genre elem : attribute.getGenresList()) {
            genres.add(elem.toString());
        }

        return String.join(",", genres);
    }

    /**
     * Converts a String of genres to a 'Genres' object.
     *
     * @param dbData  the data from the database column to be
     *                converted
     * @return the 'Genres' object from the database
     */
    @Override
    public Genres convertToEntityAttribute(String dbData) {
        ArrayList<Genre> genres = new ArrayList<>();

        //TODO handle the genres from the review-microservice
        for (String elem : dbData.split(",")) {
            genres.add(Genre.valueOf(elem.toUpperCase(Locale.getDefault())));
        }

        return new Genres(genres);
    }
}
