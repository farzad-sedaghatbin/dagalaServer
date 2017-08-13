package ir.company.app.repository;

import ir.company.app.domain.entity.EPolicy;
import ir.company.app.domain.entity.Level;
import ir.company.app.domain.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface PolicyRepository extends JpaRepository<Policy, String> {
    Policy findByEPolicy(EPolicy ePolicy);
}
