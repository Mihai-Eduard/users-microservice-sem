package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.dtos.bookshelf.ManageBookShelfRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Builds the URL for the Bookshelf service POST.
     *
     * @param shelfId ID of shelf to which book should be added
     * @param userId ID of user who is trying to add a book
     * @return String URL for the request
     */
    public String buildBookshelfURL(long shelfId, long userId) {
        return "http://localhost:8081/bookshelf/"
            + shelfId
            + "/book" + "?userId="
            + userId
            + "&bookshelfId="
            + shelfId;
    }

    /**
     * Builds the URL for the Bookshelf service DELETE.
     *
     * @param shelfId ID of shelf to which book should be added
     * @param userId ID of user who is trying to add a book
     * @return String URL for the request
     */
    public String buildBookshelfRemoveURL(long shelfId, long userId, long bookId) {
        return "http://localhost:8081/bookshelf/"
            + shelfId
            + "/book?userId="
            + userId
            + "&books="
            + bookId
            + "&bookshelfId="
            + shelfId;
    }

    /**
     * Sends a created HTTP request to targetUrl.
     *
     * @param targetUrl URL of endpoint where to send request
     * @param requestData data to include in the request
     * @return Status code of the request
     */
    public HttpStatus addToMicroservice(
        String targetUrl,
        ManageBookShelfRequest requestData
    ) {
        // create the request that will be sent
        HttpEntity<ManageBookShelfRequest> httpRequest = new HttpEntity<>(requestData, new HttpHeaders());
        // send request, and return response
        try {
            return new RestTemplate()
                .postForEntity(targetUrl, httpRequest, String.class)
                .getStatusCode();
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Sends a DELETE HTTP request to targetUrl.
     *
     * @param targetUrl URL of endpoint where to send request
     * @param requestData data to include in the request
     * @return Status code of the request
     */
    public HttpStatus removeFromMicroservice(
        String targetUrl,
        ManageBookShelfRequest requestData
    ) {
        // create the request that will be sent
        HttpEntity<ManageBookShelfRequest> httpRequest = new HttpEntity<>(new HttpHeaders());
        // send request, and return response
        return new RestTemplate()
            .exchange(targetUrl, HttpMethod.DELETE, httpRequest, String.class)
            .getStatusCode();
    }
}
