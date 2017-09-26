package ir.company.app.repository;

import ir.company.app.domain.entity.Factor;
import ir.company.app.domain.entity.MarketObject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Status entity.
 */
@SuppressWarnings("unused")
public interface FactorRepository extends JpaRepository<Factor, Long> {

    public Factor findByUID(String uid);

    public List<Factor> findByMarketObject(MarketObject marketObject);

}
