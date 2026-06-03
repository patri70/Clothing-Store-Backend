package ro.ubbcluj.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ubbcluj.assignment.model.SuspiciousUser;
import java.util.Optional;

public interface SuspiciousUserRepository extends JpaRepository<SuspiciousUser, Long> {
    Optional<SuspiciousUser> findByUsername(String username);
}
