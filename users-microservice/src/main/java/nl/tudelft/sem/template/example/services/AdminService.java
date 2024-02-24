package nl.tudelft.sem.template.example.services;


import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.converters.BannedConverter;
import nl.tudelft.sem.template.example.modules.user.converters.UserEnumConverter;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
    private final transient UserRepository userRepository;

    private final transient String admin = "ADMIN";

    @Value("${adminPass}")
    private transient String adminPassword;

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
                        .equals(admin)).orElse(false);
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
                .equals(admin)) {
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

    /**
     * Converts a given user to admin.
     *
     * @param userId the id of the user
     * @throws UserNotFoundException if the user with the given id does not exist
     */
    public void addAdmin(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (new UserEnumConverter().convertToDatabaseColumn(user.getRole()).equals(admin)) {
            return;
        }

        UserEnumType adminRole = new UserEnumType();
        adminRole.setUserRole(admin);

        user.setRole(adminRole);
        userRepository.save(user);
    }

    public boolean authenticateAdmin(String passwordRequest) {
        return passwordRequest.equals(adminPassword);
    }

}
