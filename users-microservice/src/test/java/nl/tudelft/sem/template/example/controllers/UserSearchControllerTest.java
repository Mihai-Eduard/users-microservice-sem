package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.example.dtos.UserDetailResponse;
import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;
import nl.tudelft.sem.template.example.services.UserSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;



class UserSearchControllerTest {
    @Mock
    private UserSearchService userSearchService;

    @InjectMocks
    private UserSearchController userSearchController;

    private User user;

    @BeforeEach
    void setup() {
        userSearchService = Mockito.mock(UserSearchService.class);
        userSearchController = new UserSearchController(userSearchService);
        User u1 = new User(new UsernameType("admin1"),
                new EmailType("email"),
                new PasswordType("password"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("ADMIN"),
                new DetailType(),
                new FollowingType());
        user = new User(new UsernameType("admin"),
                new EmailType("email"),
                new PasswordType("password"),
                new BannedType(false),
                new PrivacyType(false),
                new UserEnumType("ADMIN"),
                new DetailType(),
                new FollowingType(List.of(u1)));
    }

    @Test
    void searchUsers1() throws UserNotFoundException, UserBannedException {
        List<User> searchResults = new ArrayList<>();
        searchResults.add(user);

        when(userSearchService.searchUsers(ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(searchResults);

        List<UserDetailResponse> expectedResults = new ArrayList<>();
        expectedResults.add(new UserDetailResponse(user.getUsername().getUsername(), user.getDetails().getBio(),
                user.getDetails().getLocation(), user.getDetails().getFavouriteBookId(),
                null, user.getDetails().getFavouriteGenres()));

        assertEquals(expectedResults, userSearchController.searchUsers(1L, "username", null, null).getBody());
    }

    @Test
    void searchUsers2() throws UserNotFoundException, UserBannedException {
        when(userSearchService.searchUsers(ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any())).thenThrow(UserNotFoundException.class);

        assertEquals(404, userSearchController.searchUsers(1L, "username", null, null).getStatusCodeValue());
    }

    @Test
    void searchUsers3() throws UserNotFoundException, UserBannedException {
        when(userSearchService.searchUsers(ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any())).thenThrow(UserBannedException.class);

        assertEquals(403, userSearchController.searchUsers(1L, "username", null, null).getStatusCodeValue());
    }

    @Test
    void searchUsers4() throws UserNotFoundException, UserBannedException {
        when(userSearchService.searchUsers(ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any())).thenThrow(NullPointerException.class);

        assertEquals(500, userSearchController.searchUsers(1L, "username", null, null).getStatusCodeValue());
    }
}
