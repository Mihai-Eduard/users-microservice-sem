package nl.tudelft.sem.template.example.services;

import java.util.List;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import nl.tudelft.sem.template.example.search.CheckUserBannedHandler;
import nl.tudelft.sem.template.example.search.CheckUserExistsHandler;
import nl.tudelft.sem.template.example.search.PerformSearchHandler;
import nl.tudelft.sem.template.example.search.SearchHandler;
import nl.tudelft.sem.template.example.search.SearchRequest;
import org.springframework.stereotype.Service;

@Service
public class UserSearchService {
    private final transient UserRepository userRepository;
    private final transient BookRepository bookRepository;

    public UserSearchService(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Searches for users based on the provided parameters.
     *
     * @param userId          The ID of the user making the search request.
     * @param username        The username of the user to be found.
     * @param favoriteBook    The name of the favorite book of the user to be found.
     * @param friendUsername  The username of a friend of the user being searched.
     * @return                List of users matching the search criteria.
     * @throws UserNotFoundException  If the user making the request or the specified user is not found.
     * @throws UserBannedException     If the user making the request is banned.
     */
    public List<User> searchUsers(long userId, String username, String favoriteBook,
                                  String friendUsername) throws UserNotFoundException, UserBannedException {

        SearchRequest searchRequest = new SearchRequest(userId, username, favoriteBook, friendUsername);

        SearchHandler searchHandler =
                new CheckUserExistsHandler(userRepository,
                        new CheckUserBannedHandler(userRepository,
                                new PerformSearchHandler(userRepository, bookRepository)));
        // Execute the search chain of responsibility
        return searchHandler.handleSearch(searchRequest);
    }
}
