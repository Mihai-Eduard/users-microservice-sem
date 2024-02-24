package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.dtos.AddAdminRequest;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.exceptions.UserNotFoundException;
import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.services.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpgradeToAuthor_AdminUser_Success() {
        when(adminService.isAdmin(anyLong())).thenReturn(true);
        when(adminService.getUserById(anyLong())).thenReturn(new User());

        ResponseEntity<UserStatusResponse> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new UserStatusResponse("Author privileges granted successfully"), response.getBody());

        Mockito.verify(adminService, times(1)).grantAuthorPrivileges(any());
    }

    @Test
    void testUpgradeToAuthorNonAdminUserForbidden() {
        when(adminService.isAdmin(anyLong())).thenReturn(false);

        ResponseEntity<UserStatusResponse> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(new UserStatusResponse("NOT_AN_ADMIN"), response.getBody());

        Mockito.verify(adminService, never()).grantAuthorPrivileges(any());
    }

    @Test
    void testUpgradeToAuthorUserNotFoundNotFound() {
        when(adminService.isAdmin(anyLong())).thenReturn(true);
        when(adminService.getUserById(anyLong())).thenReturn(null);

        ResponseEntity<UserStatusResponse> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new UserStatusResponse("User not found"), response.getBody());

        Mockito.verify(adminService, never()).grantAuthorPrivileges(any());
    }

    @Test
    void testUpgradeToAuthorBannedUserForbidden() {
        when(adminService.isAdmin(anyLong())).thenReturn(true);
        when(adminService.getUserById(anyLong())).thenReturn(new User());
        when(adminService.isBanned(anyLong())).thenReturn(true);

        ResponseEntity<UserStatusResponse> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(new UserStatusResponse("REQUESTED_USER_BANNED"), response.getBody());

        Mockito.verify(adminService, never()).grantAuthorPrivileges(any());
    }

    @Test
    void testUpgradeToAuthorInternalServerError() {
        when(adminService.isAdmin(anyLong())).thenReturn(true);
        when(adminService.getUserById(anyLong())).thenReturn(new User());
        when(adminService.isBanned(anyLong())).thenReturn(false);
        when(adminService.grantAuthorPrivileges(adminService.getUserById(4L))).thenThrow(new RuntimeException());

        ResponseEntity<UserStatusResponse> response = adminController.upgradeToAuthor(1L, 2L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(new UserStatusResponse("Internal Server Error"), response.getBody());
    }

    @Test
    void testBanUser_UserBannedSuccessfully() {
        User wantedUser = new User();

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(true);

        ResponseEntity<UserStatusResponse> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new UserStatusResponse("User Banned Successfully"), response.getBody());

        Mockito.verify(adminService, times(1)).banUser(Mockito.eq(wantedUser));
    }

    @Test
    void testBanUser_UserNotFound() {
        when(adminService.getUserById(anyLong())).thenReturn(null);

        ResponseEntity<UserStatusResponse> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new UserStatusResponse("User Not Found"), response.getBody());

        Mockito.verify(adminService, never()).banUser(any());
    }

    @Test
    void testBanUser_UnauthorizedUser() {
        User wantedUser = new User();

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(false);

        ResponseEntity<UserStatusResponse> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(new UserStatusResponse("Unauthorized User - NOT_AN_ADMIN"), response.getBody());

        Mockito.verify(adminService, never()).banUser(any());
    }

    @Test
    void testBanUser_InternalServerError() {
        User wantedUser = new User();

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(true);
        doThrow(new RuntimeException("(Mocked) Simulated internal server error"))
                .when(adminService).banUser(any());

        ResponseEntity<UserStatusResponse> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(new UserStatusResponse("Internal Server Error"), response.getBody());

        Mockito.verify(adminService, times(1)).banUser(Mockito.eq(wantedUser));
    }

    @Test
    void testUnbanUser_UserUnbannedSuccessfully() {
        User wantedUser = new User();
        wantedUser.setBanned(new BannedType(true));

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(true);

        ResponseEntity<UserStatusResponse> response = adminController.unbanUser(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new UserStatusResponse("User Unbanned Successfully"), response.getBody());

        Mockito.verify(adminService, times(1)).unbanUser(Mockito.eq(wantedUser));
    }

    @Test
    void testUnbanUser_UserNotBanned() {
        User wantedUser = new User();
        wantedUser.setBanned(new BannedType(false));

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(true);

        ResponseEntity<UserStatusResponse> response = adminController.unbanUser(1L, 2L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(new UserStatusResponse("User is not banned"), response.getBody());

        Mockito.verify(adminService, never()).unbanUser(any());
    }

    @Test
    void testUnbanUser_UnauthorizedUser() {
        User wantedUser = new User();
        wantedUser.setBanned(new BannedType(true));

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(false);

        ResponseEntity<UserStatusResponse> response = adminController.unbanUser(1L, 2L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(new UserStatusResponse("Unauthorized User - NOT_AN_ADMIN"), response.getBody());

        Mockito.verify(adminService, never()).unbanUser(any());
    }

    @Test
    void testUnbanUser_UserNotFound() {
        when(adminService.getUserById(anyLong())).thenReturn(null);

        ResponseEntity<UserStatusResponse> response = adminController.unbanUser(1L, 2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new UserStatusResponse("User Not Found"), response.getBody());

        Mockito.verify(adminService, never()).unbanUser(any());
    }


    @Test
    void internalServerError() {
        User wantedUser = new User();
        wantedUser.setBanned(new BannedType(true));

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(true);

        doThrow(new RuntimeException("(Mocked) Simulated internal server error"))
                .when(adminService).unbanUser(any());

        ResponseEntity<UserStatusResponse> response = adminController.unbanUser(1L, 2L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(new UserStatusResponse("Internal Server Error"), response.getBody());

        verify(adminService, times(1)).unbanUser(eq(wantedUser));
    }

    @Test
    void returnsAlreadyAdminMessage() throws UserNotFoundException {
        when(adminService.isAdmin(1L)).thenReturn(true);

        ResponseEntity<UserStatusResponse> response =
                adminController.addAdmin(1L, new AddAdminRequest("password"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new UserStatusResponse("Already an Admin"), response.getBody());
        verify(adminService, never()).addAdmin(1L);
    }

    @Test
    void returnsForbiddenWithUserBannedMessage() throws UserNotFoundException {
        when(adminService.isAdmin(1L)).thenReturn(false);
        when(adminService.isBanned(1L)).thenReturn(true);

        ResponseEntity<UserStatusResponse> response =
                adminController.addAdmin(1L, new AddAdminRequest("password"));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(new UserStatusResponse("Unauthorized User - USER_BANNED"), response.getBody());
        verify(adminService, never()).addAdmin(1L);
    }

    @Test
    void incorrectPassword() throws UserNotFoundException {
        when(adminService.isAdmin(1L)).thenReturn(false);
        when(adminService.isBanned(1L)).thenReturn(false);
        when(adminService.authenticateAdmin("password")).thenReturn(false);

        ResponseEntity<UserStatusResponse> response =
                adminController.addAdmin(1L, new AddAdminRequest("password"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(new UserStatusResponse("Bad request (incorrect password)"), response.getBody());
        verify(adminService, never()).addAdmin(1L);
    }

    @Test
    void successfulAddAdmin() throws UserNotFoundException {
        when(adminService.isAdmin(1L)).thenReturn(false);
        when(adminService.isBanned(1L)).thenReturn(false);
        when(adminService.authenticateAdmin("password")).thenReturn(true);

        ResponseEntity<UserStatusResponse> response =
                adminController.addAdmin(1L, new AddAdminRequest("password"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new UserStatusResponse("User is now an Admin"), response.getBody());
        verify(adminService, times(1)).addAdmin(1L);
    }

    @Test
    void exceptionThrown() throws UserNotFoundException {
        when(adminService.isAdmin(1L)).thenReturn(false);
        when(adminService.isBanned(1L)).thenReturn(false);
        when(adminService.authenticateAdmin("password")).thenThrow(new RuntimeException("Some error"));

        ResponseEntity<UserStatusResponse> response =
                adminController.addAdmin(1L, new AddAdminRequest("password"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(new UserStatusResponse("Internal Server Error"), response.getBody());
        verify(adminService, never()).addAdmin(1L);
    }
}
