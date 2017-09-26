package ir.company.app.repository;

import ir.company.app.domain.entity.League;
import ir.company.app.domain.entity.MarketObject;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface MarketRepository extends JpaRepository<MarketObject, Long> {

    MarketObject findByName(String name);

}
