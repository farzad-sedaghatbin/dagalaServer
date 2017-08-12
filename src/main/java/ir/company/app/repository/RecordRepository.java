package ir.company.app.repository;

import ir.company.app.domain.entity.AbstractGame;
import ir.company.app.domain.entity.Record;
import ir.company.app.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Authority entity.
 */
public interface RecordRepository extends JpaRepository<Record, String> {
    Record findByAbstractGameAndUser(AbstractGame abstractGame, User u);

    Page<Record> findByAbstractGame(AbstractGame abstractGame, Pageable pageable);
}
