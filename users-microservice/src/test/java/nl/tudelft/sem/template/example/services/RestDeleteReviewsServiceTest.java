package nl.tudelft.sem.template.example.services;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.util.List;
import nl.tudelft.sem.template.example.dtos.review.ReviewDetailsResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpServerErrorException;

class RestDeleteReviewsServiceTest {

    private static RestDeleteReviewsService restDeleteReviewsService;

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setUp() {
        WireMockConfiguration config = WireMockConfiguration.options().dynamicPort();
        wireMockServer = new WireMockServer(config);
        wireMockServer.start();

        // Log the assigned port
        System.out.println("WireMock server started on port: " + wireMockServer.port());

        configureFor(wireMockServer.port());

        restDeleteReviewsService = new RestDeleteReviewsService();
        restDeleteReviewsService.setMicroServiceURL("http://localhost:" + wireMockServer.port() + "/");
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testBuildGetReviewsURL() {
        String expectedURL = "http://localhost:" + wireMockServer.port() + "/getAllReviews/";
        assertEquals(expectedURL, restDeleteReviewsService.buildGetReviewsURL());
    }

    @Test
    void testBuildDeleteReviewURL() {
        Long reviewId = 1223L;
        Long userId = 416L;
        String expectedURL = "http://localhost:" + wireMockServer.port() + "/review/delete/1223/416";
        assertEquals(expectedURL, restDeleteReviewsService.buildDeleteReviewURL(reviewId, userId));
    }

    @Test
    void testGetReviewsFromMicroserviceFailure() {
        wireMockServer.stubFor(get(urlEqualTo("/getAllReviews/"))
                .willReturn(aResponse().withStatus(500)));

        assertThrows(HttpServerErrorException.InternalServerError.class,
                () -> restDeleteReviewsService.getReviewsFromMicroservice());
    }

    @Test
    public void testGetReviewsFromMicroserviceSuccess() throws JsonProcessingException {
        // We are stubbing WireMock to return a list of reviews
        Long reviewId = 1L;
        Long bookId = 1L;
        Long userId = 1L;
        Float rating = 4.5F;
        List<Long> upvoteUsers = List.of(5L, 6L);
        List<Long> downvoteUsers = List.of(12L);
        List<Long> comments = List.of(321L, 543L);
        String dateCreated = "test";
        String dateEdited = "test";
        List<Long> reports = List.of(111L, 222L);
        String text = "test";

        ReviewDetailsResponse review = new ReviewDetailsResponse(
                reviewId, bookId, userId, rating, upvoteUsers, downvoteUsers,
                comments, dateCreated, dateEdited, reports, text);

        List<ReviewDetailsResponse> expectedReviews = List.of(
                review
        );

        stubFor(get(urlEqualTo("/getAllReviews/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new ObjectMapper().writeValueAsString(expectedReviews))));

        List<ReviewDetailsResponse> actualReviews = restDeleteReviewsService.getReviewsFromMicroservice();


        assertEquals(expectedReviews, actualReviews);
    }

    @Test
    void testDeleteReviewFromMicroservice() {
        Long reviewId = 1L;
        Long userId = 1L;

        wireMockServer.stubFor(delete(urlEqualTo("/review/delete/1/1"))
                .willReturn(aResponse().withStatus(204))); // We are simulating successful deletion

        HttpStatus status = restDeleteReviewsService.deleteReviewFromMicroservice(reviewId, userId);
        assertEquals(HttpStatus.NO_CONTENT, status);
    }

    @Test
    void testDeleteReviewFromMicroserviceFailure() {
        Long reviewId = 1L;
        Long userId = 1L;

        wireMockServer.stubFor(delete(urlEqualTo("/review/delete/1/1"))
                .willReturn(aResponse().withStatus(500))); // We are simulating internal server error

        assertThrows(HttpServerErrorException.InternalServerError.class,
                () -> restDeleteReviewsService.deleteReviewFromMicroservice(reviewId, userId));
    }

}