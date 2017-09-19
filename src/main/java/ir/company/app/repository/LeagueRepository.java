package ir.company.app.repository;

import ir.company.app.domain.entity.GameCategory;
import ir.company.app.domain.entity.League;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface LeagueRepository extends JpaRepository<League, Long> {


}
