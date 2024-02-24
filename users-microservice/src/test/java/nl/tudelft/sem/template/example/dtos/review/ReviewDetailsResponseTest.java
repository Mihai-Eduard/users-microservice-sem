package nl.tudelft.sem.template.example.dtos.review;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReviewDetailsResponseTest {

    @Test
    public void test1() {
        Long reviewId = 1L;
        Long bookId = 123L;
        Long userId = 456L;
        Float rating = 4.5F;
        List<Long> upvoteUsers = List.of(789L, 987L);
        List<Long> downvoteUsers = List.of(654L);
        List<Long> comments = List.of(321L, 543L);
        String dateCreated = "test";
        String dateEdited = "test";
        List<Long> reports = List.of(111L, 222L);
        String text = "test";

        ReviewDetailsResponse review = new ReviewDetailsResponse(
                reviewId, bookId, userId, rating, upvoteUsers, downvoteUsers,
                comments, dateCreated, dateEdited, reports, text);

        Assertions.assertEquals(reviewId, review.getReviewId());
        Assertions.assertEquals(bookId, review.getBookId());
        Assertions.assertEquals(userId, review.getUserId());
        Assertions.assertEquals(rating, review.getRating());
        Assertions.assertEquals(upvoteUsers, review.getUpvoteUsers());
        Assertions.assertEquals(downvoteUsers, review.getDownvoteUsers());
        Assertions.assertEquals(comments, review.getComments());
        Assertions.assertEquals(dateCreated, review.getDateCreated());
        Assertions.assertEquals(dateEdited, review.getDateEdited());
        Assertions.assertEquals(reports, review.getReports());
        Assertions.assertEquals(text, review.getText());
    }

    @Test
    public void test2() {
        ReviewDetailsResponse review = new ReviewDetailsResponse();

        Assertions.assertNull(review.getReviewId());
        Assertions.assertNull(review.getBookId());
        Assertions.assertNull(review.getUserId());
        Assertions.assertNull(review.getRating());
        Assertions.assertNull(review.getUpvoteUsers());
        Assertions.assertNull(review.getDownvoteUsers());
        Assertions.assertNull(review.getComments());
        Assertions.assertNull(review.getDateCreated());
        Assertions.assertNull(review.getDateEdited());
        Assertions.assertNull(review.getReports());
        Assertions.assertNull(review.getText());
    }
}