package nl.tudelft.sem.template.example.services;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.dtos.book.BookContentResponse;
import nl.tudelft.sem.template.example.dtos.book.BookListResponse;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("PMD.NullAssignment")
public class AccessCollectionService {

    private final transient BookRepository bookRepository;
    private final transient AnalyticsService analyticsService;

    /**
     * Constructor for the AccessCollectionService.
     *
     * @param bookRepository repository used by the service
     */
    public AccessCollectionService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.analyticsService = null;
    }

    /**
     * Constructor for the AccessCollectionService.
     *
     * @param bookRepository repository used by the service
     * @param analyticsService analytics service used by the service
     */
    @Autowired
    public AccessCollectionService(BookRepository bookRepository, AnalyticsService analyticsService) {
        this.bookRepository = bookRepository;
        this.analyticsService = analyticsService;
    }

    /**
     * Retrieves a book from the database by its ID.
     *
     * @param bookId The ID of the book to be returned
     * @return
     *     <ul>
     *         <li>BookContentResponse with the contents of the book with the given ID if it exists in the database</li>
     *         <li>Throws NoSuchElementException if the given bookID is not found in the database</li>
     *     </ul>
     */
    public BookContentResponse getBook(Long bookId) {
        // ANALYTICS: Track the number of times a book is viewed
        if (analyticsService != null) {
            analyticsService.trackBookFetch(bookId);
        }
        return new BookContentResponse(bookRepository.findById(bookId).orElseThrow());
    }

    /**
     * Retrieves a list of all distinct books from the database.
     * The method calls bookRepository.findAll() which returns a List.
     * Then, it uses the returned List to create a Set to automatically eliminate duplicate books.
     *
     * @return
     *     <ul>
     *         <li>BookListResponse with a list of all distinct books in the database</li>
     *     </ul>
     */
    public BookListResponse getAllBooks() {
        return new BookListResponse(new ArrayList<>(bookRepository.findAll()));
    }

    /**
     * Returns a list of books that have the given author.
     *
     * @param author the author of the books to be returned
     * @return a list of books that have the given author
     */
    public BookListResponse getBooksByAuthor(User author) {
        List<Book> books = bookRepository.findAll();
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthors().getListAuthors().contains(author.getDetails().getName())) {
                result.add(book);
            }
        }
        return new BookListResponse(result);
    }
}
