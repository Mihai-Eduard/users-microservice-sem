package nl.tudelft.sem.template.example.services;

import java.util.List;
import nl.tudelft.sem.template.example.dtos.review.ReviewDetailsResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestDeleteReviewsService {

    private transient String microServiceURL = "http://localhost:8082/";

    /**
     * Builds the URL for the getting the reviews for a book.
     *
     * @return The URL for the request of getting all the reviews for a provided book
     */
    public String buildGetReviewsURL() {
        return microServiceURL + "getAllReviews/";
    }

    /**
     * Builds the URL for the deleting a review.
     *
     * @param reviewId ID of the review
     * @param userId ID of the user who wrote the review
     * @return The URL for the request of deleting a review
     */
    public String buildDeleteReviewURL(Long reviewId, Long userId) {
        return microServiceURL + "review/delete/" + reviewId + "/" + userId;
    }

    /**
     * Sends a created HTTP request to targetUrl.
     *
     * @return The list of reviews for the book
     */
    public List<ReviewDetailsResponse> getReviewsFromMicroservice() {
        ParameterizedTypeReference<List<ReviewDetailsResponse>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<ReviewDetailsResponse>> response = new RestTemplate().exchange(
              buildGetReviewsURL(),
              HttpMethod.GET,
              null,
              responseType);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        throw new IllegalStateException("Could not get reviews from microservice.");
    }

    /**
     * Sends a DELETE HTTP to delete a review.
     *
     * @param reviewId ID of the review
     * @param userId ID of the user who wrote the review
     * @return Status code of the request
     */
    public HttpStatus deleteReviewFromMicroservice(Long reviewId, Long userId) {
        return new RestTemplate()
                .exchange(buildDeleteReviewURL(reviewId, userId),
                        HttpMethod.DELETE,
                        null,
                        String.class)
                .getStatusCode();
    }

    public void setMicroServiceURL(String s) {
        this.microServiceURL = s;
    }
}
