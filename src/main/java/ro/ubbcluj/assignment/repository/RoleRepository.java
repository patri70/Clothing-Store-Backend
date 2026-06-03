package ro.ubbcluj.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ubbcluj.assignment.model.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
