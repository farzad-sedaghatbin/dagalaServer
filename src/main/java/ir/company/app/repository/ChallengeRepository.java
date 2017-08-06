package ir.company.app.repository;

import ir.company.app.domain.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {


}
