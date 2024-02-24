package nl.tudelft.sem.template.example.integration.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import nl.tudelft.sem.template.example.repositories.BookAnalyticsRepository;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.LoginAnalyticsRepository;
import nl.tudelft.sem.template.example.services.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AnalyticsServiceTest {

    private AnalyticsService analyticsService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookAnalyticsRepository bookAnalyticsRepository;

    @Autowired
    private LoginAnalyticsRepository loginAnalyticsRepository;

    @Mock
    private LoginAnalyticsRepository mockLoginAnalyticsRepository;

    @BeforeEach
    public void setup() {
        analyticsService = new AnalyticsService(bookAnalyticsRepository, loginAnalyticsRepository, bookRepository);
    }

    @Test
    public void testTrackLogin() {
        var analytics = analyticsService.getAnalytics();
        assertEquals(0, analytics.getLoginActivity());

        analyticsService.trackLogin(1);

        analytics = analyticsService.getAnalytics();
        assertEquals(1, analytics.getLoginActivity());
    }

    @Test
    public void testTrackBookFetch() {
        var analytics = analyticsService.getAnalytics();
        assertEquals(0, analytics.getUserEngagement());

        analyticsService.trackBookFetch(1);

        analytics = analyticsService.getAnalytics();
        assertEquals(1, analytics.getUserEngagement());
    }

    @Test
    public void testPurgeUserData() {
        var mockService = new AnalyticsService(bookAnalyticsRepository, mockLoginAnalyticsRepository, bookRepository);
        mockService.purgeUserData(1L);
        verify(mockLoginAnalyticsRepository).deleteAllByUserId(1L);
    }
}
