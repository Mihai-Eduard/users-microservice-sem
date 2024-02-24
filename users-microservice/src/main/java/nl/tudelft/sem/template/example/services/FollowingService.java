package nl.tudelft.sem.template.example.services;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FollowingService {

    private final transient UserRepository userRepository;

    /**
     * Constructor of the FollowingService.
     *
     * @param userRepository the injected UserRepository
     */
    public FollowingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Service method for checking if the userIDs exist in the database, and follows the user with wantedID.
     *
     * @param userID ID of the user making the request
     * @param wantedID ID of the user to be followed
     * @return HTTP status code representing the outcome of the operation
     */
    public HttpStatus followUser(long userID, long wantedID) {
        try {
            // both users must exist
            if (!userRepository.existsById(userID) || !userRepository.existsById(wantedID)) {
                return HttpStatus.NOT_FOUND;
            }

            User userRetrieved = userRepository.findById(userID).get();

            // banned users cannot follow others
            if (userRetrieved.getBanned().isBanned()) {
                return HttpStatus.FORBIDDEN;
            }

            // users cannot start following someone they already follow, nor can they follow themselves
            if (userRetrieved.getFollowing().follows(userRepository.findById(wantedID).get()) || wantedID == userID) {
                return HttpStatus.BAD_REQUEST;
            }

            // add the wanted user to the list of users being followed
            userRetrieved.getFollowing().getFollowedUsers().add(userRepository.findById(wantedID).get());

            // persist the user in the database again
            userRepository.save(userRetrieved);

            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Service method for checking if the userIDs exist in the database, and unfollows the user with wantedID.
     *
     * @param userID ID of the user making the request
     * @param wantedID ID of the user to be unfollowed
     * @return HTTP status code representing the outcome of the operation
     */
    public HttpStatus unfollowUser(long userID, long wantedID) {
        try {
            // both users must exist
            if (!userRepository.existsById(userID) || !userRepository.existsById(wantedID)) {
                return HttpStatus.NOT_FOUND;
            }

            User userRetrieved = userRepository.findById(userID).get();

            // banned users cannot follow others
            if (userRetrieved.getBanned().isBanned()) {
                return HttpStatus.FORBIDDEN;
            }

            // users cannot unfollow someone they are not already following
            if (!userRetrieved.getFollowing().follows(userRepository.findById(wantedID).get())) {
                return HttpStatus.BAD_REQUEST;
            }

            // add the wanted user to the list of users being followed
            userRetrieved.getFollowing().getFollowedUsers().remove(userRepository.findById(wantedID).get());

            // persist the user in the database again
            userRepository.save(userRetrieved);

            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }


    /**
     * Returns a list of the IDs of the friends of the given user (where both users follow each other).
     *
     * @param user the user whose friends are to be returned
     * @return a list of the IDs of the friends of the given user
     */
    public List<Long> getFriends(User user) {
        List<Long> friends = new ArrayList<>();

        if (user.getFollowing() == null || user.getFollowing().getFollowedUsers() == null) {
            return friends;
        }

        for (User followedUser : user.getFollowing().getFollowedUsers()) {
            if (followedUser.getFollowing() == null || followedUser.getFollowing().getFollowedUsers() == null) {
                continue;
            }
            for (User followedByFollowedUser : followedUser.getFollowing().getFollowedUsers()) {
                if (followedByFollowedUser.getUserId() == user.getUserId()) {
                    friends.add(followedUser.getUserId());
                    break;
                }
            }
        }
        return friends;
    }
}
