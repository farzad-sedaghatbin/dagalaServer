package ir.company.app.repository;

import ir.company.app.domain.entity.LeagueUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface LeagueUserRepository extends JpaRepository<LeagueUser, Long> {
    @Query("select u from LeagueUser u where u.league.id=:id and u.ranking <> -1 order by u.ranking desc ")
    List<LeagueUser> topPlayer(@Param("id") Long id);

}
