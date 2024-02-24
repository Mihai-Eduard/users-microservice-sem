package nl.tudelft.sem.template.example.repositories;

import java.util.List;
import nl.tudelft.sem.template.example.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "SELECT * FROM BOOK JOIN "
            + "(SELECT FAVOURITE_BOOK_ID FROM USERS "
            + "GROUP BY FAVOURITE_BOOK_ID ORDER BY COUNT(FAVOURITE_BOOK_ID) DESC LIMIT 3) ON FAVOURITE_BOOK_ID=BOOK_ID",
            nativeQuery = true)
    public List<Book> getPopularBooks();

    @Query(value = "select * from book where title=:favoriteBook", nativeQuery = true)
    List<Book> findByTitle(String favoriteBook);
}
