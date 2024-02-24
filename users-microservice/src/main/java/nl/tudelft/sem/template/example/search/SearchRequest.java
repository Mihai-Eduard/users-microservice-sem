package nl.tudelft.sem.template.example.search;

import lombok.Data;

// Request object containing search parameters
@Data
public class SearchRequest {
    private final Long userId;
    private final String username;
    private final String favoriteBook;
    private final String friendUsername;
}