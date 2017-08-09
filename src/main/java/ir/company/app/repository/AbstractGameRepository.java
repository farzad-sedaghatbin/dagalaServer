package ir.company.app.repository;

import ir.company.app.domain.entity.AbstractGame;
import ir.company.app.domain.entity.GameCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface AbstractGameRepository extends JpaRepository<AbstractGame, Long> {

    List<AbstractGame> findByGameCategory(GameCategory gameCategory);
    AbstractGame findByName(String name);
}
