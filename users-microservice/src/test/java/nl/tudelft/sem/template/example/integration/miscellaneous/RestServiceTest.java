package nl.tudelft.sem.template.example.integration.miscellaneous;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.example.services.RestService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RestServiceTest {
    @Test
    public void correctlyBuildsBookshelfUrl() {
        RestService restService = new RestService();
        String expected = "http://localhost:8081/bookshelf/23/book?userId=2&bookshelfId=23";
        assertEquals(restService.buildBookshelfURL(23, 2), expected);
    }

    @Test
    public void correctlyBuildsBookshelfRemoveUrl() {
        RestService restService = new RestService();
        String expected = "http://localhost:8081/bookshelf/23/book?userId=2&books=10&bookshelfId=23";
        assertEquals(restService.buildBookshelfRemoveURL(23, 2, 10), expected);
    }
}
