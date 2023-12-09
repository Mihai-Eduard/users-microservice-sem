package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.converters.BannedConverter;
import nl.tudelft.sem.template.example.modules.user.converters.UserEnumConverter;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
    private final transient UserRepository userRepository;

    /**
     * Constructor for the UserService.
     *
     * @param userRepository repository used by the service
     */
    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Checks for a user if it is an admin.
     *
     * @param userId the id of the input user
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin(Long userId) {
        return userRepository.findById(userId).map(user ->
                new UserEnumConverter().convertToDatabaseColumn(user.getRole())
                        .equals("ADMIN")).orElse(false);
    }

    public boolean isBanned(Long wantedId) {
        return userRepository.findById(wantedId).map(user ->
                new BannedConverter().convertToDatabaseColumn(user.getBanned())).orElse(false);
    }

    public User getUserById(Long wantedId) {
        return userRepository.findById(wantedId).orElse(null);
    }

    /**
     * give author privileges to wantedUser.
     *
     * @param wantedUser provided user that will be given author privileges
     * @return the new user with privileges
     */
    @Transactional
    public User grantAuthorPrivileges(User wantedUser) {
        UserEnumType role = new UserEnumType();
        role.setUserRole("AUTHOR");

        // Check if the wantedUser is an ADMIN
        // if yes we should not downgrade it to AUTHOR
        if (new UserEnumConverter().convertToDatabaseColumn(wantedUser.getRole())
                .equals("ADMIN")) {
            return wantedUser;
        }

        if (new UserEnumConverter().convertToDatabaseColumn(wantedUser.getRole())
                .equals("AUTHOR")) {
            return wantedUser;
        }
        wantedUser.setRole(role);
        return userRepository.save(wantedUser);
    }

    @Transactional
    public User banUser(User wantedUser) {
        wantedUser.setBanned(new BannedType(true));
        return userRepository.save(wantedUser);
    }

    @Transactional
    public User unbanUser(User wantedUser) {
        wantedUser.setBanned(new BannedType(false));
        return userRepository.save(wantedUser);
    }
}
