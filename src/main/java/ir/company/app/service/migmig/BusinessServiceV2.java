package ir.company.app.service.migmig;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import ir.company.app.repository.*;
import ir.company.app.service.GameService;
import ir.company.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by farzad on 12/1/2017.
 */
@RestController
@RequestMapping("/api")
public class BusinessServiceV2 {

    @Inject
    GameService gameService;
    private final UserRepository userRepository;
    private final LeagueUserRepository leagueUserRepository;
    private final ChallengeRepository challengeRepository;
    private final RecordRepository recordRepository;
    private final LevelRepository levelRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final GameRepository gameRepository;
    private final CategoryUserRepository categoryUserRepository;
    private final PolicyRepository policyRepository;
    private final LeagueRepository leagueRepository;
    private final AbstractGameRepository abstractGameRepository;
    private final FactorRepository factorRepository;
    private final MarketRepository marketRepository;
    private final MessageRepository messageRepository;
    @PersistenceContext
    private EntityManager em;

    @Inject
    public BusinessServiceV2(LevelRepository levelRepository, UserRepository userRepository, LeagueUserRepository leagueUserRepository, ChallengeRepository challengeRepository, RecordRepository recordRepository, UserService userService, CategoryRepository categoryRepository, GameRepository gameRepository, AbstractGameRepository abstractGameRepository, CategoryUserRepository categoryUserRepository, PolicyRepository policyRepository, LeagueRepository leagueRepository, FactorRepository factorRepository, MarketRepository marketRepository, MessageRepository messageRepository) {
        this.leagueUserRepository = leagueUserRepository;
        this.categoryUserRepository = categoryUserRepository;
        this.policyRepository = policyRepository;
        this.levelRepository = levelRepository;
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.recordRepository = recordRepository;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.gameRepository = gameRepository;
        this.abstractGameRepository = abstractGameRepository;
        this.leagueRepository = leagueRepository;
        this.factorRepository = factorRepository;
        this.marketRepository = marketRepository;
        this.messageRepository = messageRepository;
    }
    @RequestMapping(value = "/2/refresh", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> refresh(@RequestBody String username) throws JsonProcessingException {

        return ResponseEntity.ok(userService.refreshV2(false, username));

    }

}
