package nl.tudelft.sem.template.example.controllers.collection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.example.dtos.review.ReviewDetailsResponse;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.services.ModifyCollectionService;
import nl.tudelft.sem.template.example.services.RestDeleteReviewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;



public class ModifyCollectionControllerTest {

    private ModifyCollectionService modifyCollectionService;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private RestDeleteReviewsService restDeleteReviewsService;

    private ModifyCollectionController modifyCollectionController;

    /**
     * Setup method which is run before
     * the other tests for creating a testable
     * environment through mocks.
     */
    @BeforeEach
    public void setup() {
        modifyCollectionService = Mockito.mock(ModifyCollectionService.class);
        restDeleteReviewsService = Mockito.mock(RestDeleteReviewsService.class);
        bookRepository = Mockito.mock(BookRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        modifyCollectionController = new ModifyCollectionController(
                modifyCollectionService, bookRepository,
                userRepository, restDeleteReviewsService);
    }

    @Test
    public void test1() {
        Long bookId = 123L;
        ReviewDetailsResponse review1 = new ReviewDetailsResponse(1L, bookId, 2L, 4.5F, List.of(1L, 2L),
                List.of(789L), List.of(101L, 102L), "2023-12-18", "2023-12-19",
                List.of(201L, 202L), "test.");
        ReviewDetailsResponse review2 = new ReviewDetailsResponse(2L, bookId, 1L, 3.8F, List.of(1L, 2L),
                List.of(333L), List.of(444L, 555L), "2023-12-18", "2023-12-19",
                List.of(666L, 777L), "aaaa.");
        List<ReviewDetailsResponse> reviews = Arrays.asList(review1, review2);

        when(restDeleteReviewsService.getReviewsFromMicroservice()).thenReturn(reviews);
        when(restDeleteReviewsService.deleteReviewFromMicroservice(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(HttpStatus.OK);

        boolean result = modifyCollectionController.deleteReviewFromMicroservice(bookId);

        assertTrue(result);
    }

    @Test
    public void test2() {
        Long bookId = 123L;
        ReviewDetailsResponse review1 = new ReviewDetailsResponse(1L, bookId, 456L, 4.5F, List.of(123L, 456L),
                List.of(789L), List.of(101L, 102L), "2023-12-08", "2023-12-09",
                List.of(201L, 202L), "This is a review text.");
        ReviewDetailsResponse review2 = new ReviewDetailsResponse(2L, bookId, 789L, 3.8F, List.of(111L, 222L),
                List.of(333L), List.of(444L, 555L), "2023-12-08", "2023-12-09",
                List.of(666L, 777L), "aaaa.");

        List<ReviewDetailsResponse> reviews = Arrays.asList(review1, review2);

        when(restDeleteReviewsService.getReviewsFromMicroservice()).thenReturn(reviews);
        when(restDeleteReviewsService.deleteReviewFromMicroservice(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        boolean result = modifyCollectionController.deleteReviewFromMicroservice(bookId);

        assertFalse(result);
    }

    @Test
    public void test3() {
        Long bookId = 123L;

        when(restDeleteReviewsService.getReviewsFromMicroservice()).thenThrow(new RuntimeException());

        boolean result = modifyCollectionController.deleteReviewFromMicroservice(bookId);

        assertFalse(result);
    }
}
