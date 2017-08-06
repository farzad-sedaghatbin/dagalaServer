package ir.company.app.repository;

import ir.company.app.domain.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Status entity.
 */
@SuppressWarnings("unused")
public interface TripRepository extends JpaRepository<Trip,Long> {

    public Trip findByUID(String uid);

}
