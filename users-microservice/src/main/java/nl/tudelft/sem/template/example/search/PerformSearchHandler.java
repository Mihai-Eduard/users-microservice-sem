package nl.tudelft.sem.template.example.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nl.tudelft.sem.template.example.domain.book.Book;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.BookRepository;
import nl.tudelft.sem.template.example.repositories.UserRepository;


public class PerformSearchHandler implements SearchHandler {

    private final transient UserRepository userRepository;
    private final transient BookRepository bookRepository;

    public PerformSearchHandler(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<User> handleSearch(SearchRequest request) {
        if (request.getUsername() != null) {
            return userRepository.findByUsername(request.getUsername());
        } else if (request.getFriendUsername() != null) {
            // We are fetching friends of the user with the provided friend username
            List<User> users = userRepository.findByUsername(request.getFriendUsername());
            if (users != null) {
                List<User> friends = new ArrayList<>();
                for (User user : users) {
                    List<User> followed = user.getFollowing().getFollowedUsers();
                    for (User follower : followed) {
                        if (follower.getFollowing().follows(user)) {
                            friends.add(follower);
                        }
                    }
                }

                return friends;
            } else {
                return List.of(); // User not found, we'll return an empty list
            }
        } else if (request.getFavoriteBook() != null) {
            List<Book> books = bookRepository.findByTitle(request.getFavoriteBook());
            Set<User> users = new HashSet<>();
            for (Book book : books) {
                users.addAll(userRepository.findByFavoriteBook(book.getBookId()));
            }
            return new ArrayList<>(users);
        }

        return List.of(); // No valid search criteria provided
    }
}
