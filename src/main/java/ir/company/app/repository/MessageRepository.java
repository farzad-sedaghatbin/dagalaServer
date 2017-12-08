package ir.company.app.repository;

import ir.company.app.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    public Message findByIcon(String icon);

}
