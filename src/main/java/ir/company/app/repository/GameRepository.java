package ir.company.app.repository;

import ir.company.app.domain.entity.Game;
import ir.company.app.domain.entity.GameStatus;
import ir.company.app.domain.entity.League;
import ir.company.app.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("select g from Game g where g.gameStatus = ?1 and g.league is null and (g.first=?2 or g.second=?3)")

    List<Game> findByGameStatusAndFirstOrSecondAndLeague(GameStatus gameStatus, User first, User second,  Pageable pageable);

    @Query("select g from Game g where g.gameStatus = ?4 and g.league=?3 and (g.first=?1 or g.second=?2)")

    Game findByFirstOrSecondAndLeagueAndGameStatus(User first,User second, League league, GameStatus gameStatus);



}
