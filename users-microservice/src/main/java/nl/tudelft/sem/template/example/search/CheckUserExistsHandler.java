package nl.tudelft.sem.template.example.search;

import java.util.List;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.UserRepository;

public class CheckUserExistsHandler implements SearchHandler {
    private final transient SearchHandler nextHandler;
    private final transient UserRepository userRepository;


    public CheckUserExistsHandler(UserRepository userRepository, SearchHandler nextHandler) {
        this.userRepository = userRepository;
        this.nextHandler = nextHandler;
    }

    @Override
    public List<User> handleSearch(SearchRequest request) throws UserNotFoundException, UserBannedException {
        if (userExists(request.getUserId())) {
            return nextHandler.handleSearch(request);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    private boolean userExists(Long userId) {
        return userRepository.findById(userId).isPresent();
    }
}