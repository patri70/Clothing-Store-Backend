package ro.ubbcluj.assignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.ubbcluj.assignment.model.AuditLog;
import ro.ubbcluj.assignment.model.SuspiciousUser;
import ro.ubbcluj.assignment.service.SecurityService;

import java.util.List;

@RestController
@RequestMapping("/api/security")
@CrossOrigin(origins = "*")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @GetMapping("/logs")
    public List<AuditLog> getLogs() {
        return securityService.getAllLogs();
    }

    @GetMapping("/suspicious")
    public List<SuspiciousUser> getSuspiciousUsers() {
        return securityService.getSuspiciousUsers();
    }

    @PostMapping("/log")
    public void logAction(
            @RequestParam String username,
            @RequestParam String role,
            @RequestParam String action,
            @RequestParam String method,
            @RequestParam String url,
            @RequestBody(required = false) String details) {
        securityService.logAction(username, role, action, method, url, details);
    }
}
