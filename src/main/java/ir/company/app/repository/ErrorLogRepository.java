package ir.company.app.repository;

import ir.company.app.domain.Authority;
import ir.company.app.domain.entity.ErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface ErrorLogRepository extends JpaRepository<ErrorLog, String> {

}
