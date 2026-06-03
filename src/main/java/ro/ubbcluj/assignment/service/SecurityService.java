package ro.ubbcluj.assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.ubbcluj.assignment.model.AuditLog;
import ro.ubbcluj.assignment.model.SuspiciousUser;
import ro.ubbcluj.assignment.repository.AuditLogRepository;
import ro.ubbcluj.assignment.repository.SuspiciousUserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SecurityService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private SuspiciousUserRepository suspiciousUserRepository;

    public void logAction(String username, String role, String action, String method, String url, String details) {
        AuditLog log = new AuditLog(username, role, action, method, url, details);
        auditLogRepository.save(log);
        
        detectMaliciousBehavior(username, action);
    }

    private void detectMaliciousBehavior(String username, String action) {
        if (username.equals("Anonymous")) return;

        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        
        // Rule 1: Mass deletion ( >= 3 deletes in 1 min)
        if (action.contains("DELETE")) {
            long deleteCount = auditLogRepository.countByUsernameAndActionAndTimestampAfter(username, action, oneMinuteAgo);
            if (deleteCount >= 3) {
                markAsSuspicious(username, "Mass deletion attempt (>= 3 in 1 min)", (int) deleteCount);
            }
        }

        // Rule 2: Rapid adding ( >= 5 additions in 1 min)
        if (action.contains("CREATE")) {
            long addCount = auditLogRepository.countByUsernameAndActionAndTimestampAfter(username, action, oneMinuteAgo);
            if (addCount >= 5) {
                markAsSuspicious(username, "Rapid product creation (>= 5 in 1 min)", (int) addCount);
            }
        }
    }

    private void markAsSuspicious(String username, String reason, int count) {
        SuspiciousUser suspiciousUser = suspiciousUserRepository.findByUsername(username)
                .orElse(new SuspiciousUser(username, reason, count));
        
        suspiciousUser.setReason(reason);
        suspiciousUser.setActionCount(count);
        suspiciousUser.setDetectedAt(LocalDateTime.now());
        
        suspiciousUserRepository.save(suspiciousUser);
    }

    public List<SuspiciousUser> getSuspiciousUsers() {
        return suspiciousUserRepository.findAll();
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}
