package ir.company.app.service;

import ir.company.app.config.Constants;
import ir.company.app.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service class for managing users.
 */
@ApplicationScope
@Service
public class Initializer {

    private final Logger log = LoggerFactory.getLogger(Initializer.class);

    @Inject
    private UserRepository userRepository;

    public Initializer() {
        Constants.index = new AtomicLong(userRepository.count());
    }
}
