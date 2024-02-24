package nl.tudelft.sem.template.example.services;

import java.util.List;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.dtos.AnalyticsResponse;
import nl.tudelft.sem.template.example.modules.analytics.BookAnalytic;
import nl.tudelft.sem.template.example.modules.analytics.LoginAnalytic;
import nl.tudelft.sem.template.example.repositories.BookAnalyticsRepository;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.LoginAnalyticsRepository;
import org.springframework.stereotype.Service;


@Service
public class AnalyticsService {
    // The analytics service keeps track of the following statistics:
    // - Popular genres (most favorite genres from users)
    // - Popular books (most favorite books from users)
    // - Login activity (how many times there have been a login in the past 24 hours)
    // - User engagement (how many times books have been fetched in the past 24 hours)

    private final transient BookAnalyticsRepository bookAnalyticsRepository;
    private final transient LoginAnalyticsRepository loginAnalyticsRepository;
    private final transient BookRepository bookRepository;

    /**
     * Constructor for the analytics service.
     *
     * @param bookAnalyticsRepository the book analytics repository
     * @param loginAnalyticsRepository the login analytics repository
     * @param bookRepository the book repository
     */
    public AnalyticsService(BookAnalyticsRepository bookAnalyticsRepository,
                            LoginAnalyticsRepository loginAnalyticsRepository,
                            BookRepository bookRepository) {
        this.bookAnalyticsRepository = bookAnalyticsRepository;
        this.loginAnalyticsRepository = loginAnalyticsRepository;
        this.bookRepository = bookRepository;
    }

    public void trackLogin(long userId) {
        LoginAnalytic analytic = new LoginAnalytic(userId, System.currentTimeMillis());
        loginAnalyticsRepository.save(analytic);
    }

    public void trackBookFetch(long bookId) {
        BookAnalytic analytic = new BookAnalytic(bookId, System.currentTimeMillis());
        bookAnalyticsRepository.save(analytic);
    }

    public void purgeUserData(long userId) {
        loginAnalyticsRepository.deleteAllByUserId(userId);
    }

    /**
     * Get the analytics.
     *
     * @return the analytics
     */
    public AnalyticsResponse getAnalytics() {
        List<String> popularGenres = bookAnalyticsRepository.getPopularGenres();
        List<Book> popularBooks = bookRepository.getPopularBooks();
        List<BookAnalytic> bookAnalytics = bookAnalyticsRepository
                .findAllByTimestampGreaterThanEqual(System.currentTimeMillis() - 24 * 3600 * 1000);
        List<LoginAnalytic> loginAnalytics = loginAnalyticsRepository
                .findAllByTimestampGreaterThanEqual(System.currentTimeMillis() - 24 * 3600 * 1000);

        long loginActivity = loginAnalytics.size();
        long userEngagement = bookAnalytics.size();

        return new AnalyticsResponse(popularGenres, popularBooks, loginActivity, userEngagement);
    }
}
