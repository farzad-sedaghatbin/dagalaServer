package ir.company.app.repository;

import ir.company.app.domain.entity.Game;
import ir.company.app.domain.entity.GameStatus;
import ir.company.app.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByGameStatusAndFirst(GameStatus gameStatus, User user, Pageable pageable);
    List<Game> findByGameStatusAndSecond(GameStatus gameStatus, User user, Pageable pageable);


}
