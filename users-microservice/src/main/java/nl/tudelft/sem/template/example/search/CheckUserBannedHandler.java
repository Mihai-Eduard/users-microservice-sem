package nl.tudelft.sem.template.example.search;

import java.util.List;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.converters.BannedConverter;
import nl.tudelft.sem.template.example.repositories.UserRepository;

// Concrete handler for checking if the user is not banned
public class CheckUserBannedHandler implements SearchHandler {
    private final transient SearchHandler nextHandler;

    private final transient UserRepository userRepository;

    public CheckUserBannedHandler(UserRepository userRepository, SearchHandler nextHandler) {
        this.userRepository = userRepository;
        this.nextHandler = nextHandler;
    }

    @Override
    public List<User> handleSearch(SearchRequest request) throws UserBannedException, UserNotFoundException {
        if (!userIsBanned(request.getUserId())) {
            return nextHandler.handleSearch(request);
        }
        throw new UserBannedException("User is banned");
    }

    private boolean userIsBanned(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new BannedConverter().convertToDatabaseColumn(user.getBanned()))
                .orElse(false);
    }
}