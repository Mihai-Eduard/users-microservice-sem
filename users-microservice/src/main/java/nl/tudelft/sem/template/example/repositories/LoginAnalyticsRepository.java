package nl.tudelft.sem.template.example.repositories;

import java.util.List;
import nl.tudelft.sem.template.example.modules.analytics.LoginAnalytic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAnalyticsRepository extends JpaRepository<LoginAnalytic, Long> {
    List<LoginAnalytic> findAllByTimestampGreaterThanEqual(long timestamp);

    void deleteAllByUserId(long userId);
}
