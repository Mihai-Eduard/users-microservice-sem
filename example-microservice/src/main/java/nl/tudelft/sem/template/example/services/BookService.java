package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.NumPageConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.domain.book.converters.TitleConverter;
import nl.tudelft.sem.template.example.models.BookModel;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {
    private final transient BookRepository bookRepository;

    /**
     * Constructor for the BookService.
     *
     * @param bookRepository repository used by the service
     */
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Adds a book to the database.
     *
     * @param requestBody Json of the book to be added to the database
     * @return book that was added to the database if successful, null otherwise
     */
    public Book insert(BookModel requestBody, Long creatorId) throws IllegalArgumentException {
        if (requestBody == null) {
            throw new IllegalArgumentException();
        }

        Book book = new Book(
                    creatorId,
                    new Title(requestBody.getTitle()),
                    new GenresConverter().convertToEntityAttribute(requestBody.getGenre()),
                    new AuthorsConverter().convertToEntityAttribute(requestBody.getAuthor()),
                    new SeriesConverter().convertToEntityAttribute(requestBody.getSeries()),
                    new NumPage(requestBody.getNumberOfPages()));

        return bookRepository.save(book);
    }

    /**
     * Updates a book in the database.
     *
     * @param newBook the new book details
     * @param book the book to be updated
     * @return the updated book or throws an error if the book does not exist
     */
    @Transactional
    public Book updateBook(Book book, BookModel newBook) {
        try {
            book.setAuthors(new AuthorsConverter().convertToEntityAttribute(newBook.getAuthor()));
        } catch (Exception e) {
            System.out.println("Invalid author when updating book!");
        }

        try {
            book.setGenres(new GenresConverter().convertToEntityAttribute(newBook.getGenre()));
        } catch (Exception e) {
            System.out.println("Invalid genre when updating book!");
        }

        try {
            book.setPageNum(new NumPageConverter().convertToEntityAttribute(newBook.getNumberOfPages()));
        } catch (Exception e) {
            System.out.println("Invalid number of pages when updating book!");
        }

        try {
            book.setTitle(new TitleConverter().convertToEntityAttribute(newBook.getTitle()));
        } catch (Exception e) {
            System.out.println("Invalid title when updating book!");
        }

        return bookRepository.save(book);
    }
}
