package nl.tudelft.sem.template.example.search;

import java.util.List;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.User;

public interface SearchHandler {
    List<User> handleSearch(SearchRequest request) throws UserNotFoundException, UserBannedException;
}