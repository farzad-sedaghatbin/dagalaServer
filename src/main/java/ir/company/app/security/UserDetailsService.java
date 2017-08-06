package ir.company.app.security;

import ir.company.app.domain.entity.User;
import ir.company.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Inject
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        Optional<User> userFromDatabase = userRepository.findOneByLogin(lowercaseLogin);
        return userFromDatabase.map(user -> {
            if (!user.isActivated()) {
                throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
            }

            return new org.springframework.security.core.userdetails.User(lowercaseLogin,
                user.getPassword(), new ArrayList<GrantedAuthority>());
        }).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " +
            "database"));

    }
}
