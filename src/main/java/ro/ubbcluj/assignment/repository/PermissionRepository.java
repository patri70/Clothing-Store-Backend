package ro.ubbcluj.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ubbcluj.assignment.model.Permission;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}
