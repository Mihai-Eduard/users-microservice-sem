package nl.tudelft.sem.template.example.services;

import java.util.NoSuchElementException;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.domain.book.NumPage;
import nl.tudelft.sem.template.example.domain.book.Title;
import nl.tudelft.sem.template.example.domain.book.converters.AuthorsConverter;
import nl.tudelft.sem.template.example.domain.book.converters.GenresConverter;
import nl.tudelft.sem.template.example.domain.book.converters.NumPageConverter;
import nl.tudelft.sem.template.example.domain.book.converters.SeriesConverter;
import nl.tudelft.sem.template.example.domain.book.converters.TitleConverter;
import nl.tudelft.sem.template.example.dtos.book.BookRequest;
import nl.tudelft.sem.template.example.dtos.book.BookResponse;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModifyCollectionService {
    private final transient BookRepository bookRepository;

    /**
     * Constructor for the BookService.
     *
     * @param bookRepository repository used by the service
     */
    public ModifyCollectionService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Adds a book to the database.
     *
     * @param requestBody Json of the book to be added to the database
     * @return
     *     <ul>
     *         <li>BookResponse with the ID of the book if it was added successfully</li>
     *         <li>throws IllegalArgumentException if the given book request or creator ID are null</li>
     *     </ul>
     */
    public BookResponse addBook(Long creatorId, BookRequest requestBody) throws IllegalArgumentException {
        if (requestBody == null || creatorId == null) {
            throw new IllegalArgumentException();
        }

        Book book = new Book(
                creatorId,
                new Title(requestBody.getTitle()),
                new GenresConverter().convertToEntityAttribute(requestBody.getGenre()),
                new AuthorsConverter().convertToEntityAttribute(requestBody.getAuthor()),
                new SeriesConverter().convertToEntityAttribute(requestBody.getSeries()),
                new NumPage(requestBody.getNumberOfPages()));

        Book savedBook = bookRepository.save(book);
        return new BookResponse(savedBook.getBookId());
    }

    /**
     * Updates a book from the database. From the provided fields in the body,
     * only the ones that are not null and have a valid format will be updated.
     *
     * @param bookId the id of the book to be updated
     * @return
     *     <ul>
     *         <li>BookResponse with the ID of the updated book if the update was successful</li>
     *         <li>BookResponse with a null ID if the book does not exist</li>
     *         <li>null if the book was not updated successfully (e.g. invalid id, database error)</li>
     *     </ul>
     */
    @Transactional
    public BookResponse updateBook(Long bookId, BookRequest newBook) {
        try {
            Book book = bookRepository.findById(bookId).orElseThrow();
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
            try {
                book.setSeries(new SeriesConverter().convertToEntityAttribute(newBook.getSeries()));
            } catch (Exception e) {
                System.out.println("Invalid series when updating book!");
            }
            bookRepository.save(book);
            return new BookResponse(bookId);
        } catch (NoSuchElementException e) {
            System.out.println("Book does not exist!");
            return new BookResponse(null);
        } catch (Exception e) {
            System.out.println("Error when updating book!");
            return null;
        }
    }

    /**
     * Deletes a book from the database.
     *
     * @param bookId the id of the book to be deleted
     * @return
     *     <ul>
     *         <li>BookResponse with the ID of the deleted book if the deletion was successful</li>
     *         <li>BookResponse with a null ID if the book does not exist</li>
     *         <li>null if the book was not deleted successfully (e.g. invalid id, database error)</li>
     *     </ul>
     */
    public BookResponse deleteBook(Long bookId) {
        try {
            if (bookRepository.findById(bookId).isEmpty()) {
                return new BookResponse(null);
            }
            bookRepository.deleteById(bookId);
            return new BookResponse(bookId);
        } catch (Exception e) {
            System.out.println("Error when deleting book!");
            return null;
        }
    }
}