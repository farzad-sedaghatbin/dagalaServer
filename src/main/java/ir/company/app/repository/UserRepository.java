package ir.company.app.repository;

import ir.company.app.domain.entity.League;
import ir.company.app.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(ZonedDateTime dateTime);

    @Query("select u.avatar,u.score,u.login,u.id from User u ")
    Page<Object[]> topPlayer(Pageable pageable);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmail(String email);

    User findOneByLogin(String login);

    User findOneByGuestId(String guestId);

    User findByLeagues(League league);

    @Override
    void delete(User t);

}
