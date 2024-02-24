package nl.tudelft.sem.template.example.repositories;

import java.util.List;
import nl.tudelft.sem.template.example.modules.analytics.BookAnalytic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookAnalyticsRepository extends JpaRepository<BookAnalytic, Long>  {
    @Query(value = "SELECT FAVOURITE_GENRES FROM USER_FAVOURITE_GENRES "
            + "GROUP BY FAVOURITE_GENRES ORDER BY COUNT(FAVOURITE_GENRES) DESC LIMIT 3",
            nativeQuery = true)
    List<String> getPopularGenres();

    List<BookAnalytic> findAllByTimestampGreaterThanEqual(long timestamp);
}
