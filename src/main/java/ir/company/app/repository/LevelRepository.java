package ir.company.app.repository;

import ir.company.app.domain.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface LevelRepository extends JpaRepository<Level, String> {
    Level findByLevel(int level);
}
