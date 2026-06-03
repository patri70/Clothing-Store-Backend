package ro.ubbcluj.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ubbcluj.assignment.model.AuditLog;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUsername(String username);
    long countByUsernameAndActionAndTimestampAfter(String username, String action, java.time.LocalDateTime timestamp);
}
