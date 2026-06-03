package ro.ubbcluj.assignment.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suspicious_users")
public class SuspiciousUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String reason;
    private int actionCount;
    private LocalDateTime detectedAt;

    public SuspiciousUser() {}

    public SuspiciousUser(String username, String reason, int actionCount) {
        this.username = username;
        this.reason = reason;
        this.actionCount = actionCount;
        this.detectedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public int getActionCount() { return actionCount; }
    public void setActionCount(int actionCount) { this.actionCount = actionCount; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
}
