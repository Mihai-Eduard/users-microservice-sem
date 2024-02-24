package nl.tudelft.sem.template.example.integration.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.controllers.AnalyticsController;
import nl.tudelft.sem.template.example.dtos.AnalyticsResponse;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.services.AdminService;
import nl.tudelft.sem.template.example.services.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class AnalyticsControllerTest {

    @Mock
    private AnalyticsService analyticsService;
    @Mock
    private AdminService adminService;

    private AnalyticsController controller;

    @BeforeEach
    public void setUp() {
        controller = new AnalyticsController(analyticsService, adminService);
    }

    @Test
    public void testGetAnalyticsAsAdmin() {
        when(adminService.isAdmin(1L)).thenReturn(true);

        var analytics = new AnalyticsResponse(null, null, 1L, 2L);

        when(analyticsService.getAnalytics()).thenReturn(analytics);
        ResponseEntity<GenericResponse> response = controller.getAnalytics(1L);

        var expected = ResponseEntity.ok(analytics);
        assertEquals(expected, response);
    }

    @Test
    public void testGetAnalyticsAsNotAnAdmin() {
        when(adminService.isAdmin(1L)).thenReturn(false);
        ResponseEntity<GenericResponse> response = controller.getAnalytics(1L);

        var expected = ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UserStatusResponse("NOT_AN_ADMIN"));
        assertEquals(expected, response);
    }
}
