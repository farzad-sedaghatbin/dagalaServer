package ir.company.app.repository;

import ir.company.app.domain.entity.StartupModal;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface ModalRepository extends JpaRepository<StartupModal, Long> {


}
