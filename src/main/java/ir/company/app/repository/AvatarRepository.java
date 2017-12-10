package ir.company.app.repository;

import ir.company.app.domain.entity.Avatar;
import ir.company.app.domain.entity.MarketObject;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

        public Avatar findByIcon(String icon);

}
