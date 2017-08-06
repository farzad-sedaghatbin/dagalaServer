package ir.company.app.repository;

import ir.company.app.domain.entity.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface CategoryRepository extends JpaRepository<GameCategory, Long> {


}
