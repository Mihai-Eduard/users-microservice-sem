package nl.tudelft.sem.template.example.integration.following;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.controllers.FollowingController;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.dtos.security.ChangePasswordResponse403;
import nl.tudelft.sem.template.example.services.FollowingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class UnfollowingControllerTest {

    @Mock
    FollowingService followingService;

    FollowingController followingController;

    @BeforeEach
    public void setup() {
        this.followingController = new FollowingController(followingService);
    }

    @Test
    public void test200OK() {
        when(followingService.unfollowUser(1L, 2L)).thenReturn(HttpStatus.OK);

        ResponseEntity<GenericResponse> result = followingController.unfollow(2L, 1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void test400Bad_Request() {
        when(followingService.unfollowUser(1L, 2L)).thenReturn(HttpStatus.BAD_REQUEST);

        ResponseEntity<GenericResponse> result = followingController.unfollow(2L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void test403Forbidden() {
        when(followingService.unfollowUser(1L, 2L)).thenReturn(HttpStatus.FORBIDDEN);

        ResponseEntity<GenericResponse> result = followingController.unfollow(2L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals("USER_BANNED", ((ChangePasswordResponse403) result.getBody()).getRole());
    }

    @Test
    public void test500Internal_Server_Error() {
        when(followingService.unfollowUser(1L, 2L)).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);

        ResponseEntity<GenericResponse> result = followingController.unfollow(2L, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
}
