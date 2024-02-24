package nl.tudelft.sem.template.example.controllers;


import nl.tudelft.sem.template.example.dtos.AnalyticsResponse;
import nl.tudelft.sem.template.example.dtos.UserStatusResponse;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;
import nl.tudelft.sem.template.example.services.AdminService;
import nl.tudelft.sem.template.example.services.AnalyticsService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final transient AnalyticsService analyticsService;
    private final transient AdminService adminService;

    public AnalyticsController(AnalyticsService analyticsService, AdminService adminService) {
        this.analyticsService = analyticsService;
        this.adminService = adminService;
    }

    /**
     * Returns the analytics of the system.
     *
     * @param userId the id of the user requesting the analytics
     * @return the analytics
     */
    @GetMapping("/analytics")
    public ResponseEntity<GenericResponse> getAnalytics(@RequestParam("userID") long userId) {

        // Only admins are able to access this particular endpoint
        if (!adminService.isAdmin(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UserStatusResponse("NOT_AN_ADMIN"));
        }

        AnalyticsResponse analyticsResponse = analyticsService.getAnalytics();

        return ResponseEntity.ok(analyticsResponse);
    }
}
