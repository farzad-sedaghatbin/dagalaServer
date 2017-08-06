package ir.company.app.service.migmig;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.company.app.domain.entity.AbstractGame;
import ir.company.app.domain.entity.Challenge;
import ir.company.app.domain.entity.Game;
import ir.company.app.domain.entity.GameStatus;
import ir.company.app.repository.*;
import ir.company.app.security.SecurityUtils;
import ir.company.app.service.UserService;
import ir.company.app.service.dto.GameRedisDTO;
import ir.company.app.service.util.RedisUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BusinessService {
    @Inject
    private UserRepository userRepository;
    @Inject
    private ChallengeRepository challengeRepository;
    @Inject
    private UserService userService;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private GameRepository gameRepository;
    @Inject
    private AbstractGameRepository abstractGameRepository;
    @PersistenceContext
    private EntityManager em;


    @RequestMapping(value = "/1/requestGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> requestGame(HttpServletResponse response) throws JsonProcessingException {
        GameRedisDTO gameRedisDTO;
        if (RedisUtil.sizeOfMap("half") == 0) {
            Game game = new Game();
            game.setGameStatus(GameStatus.INVISIBLE);
            game.setFirst(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
            gameRepository.save(game);
            gameRedisDTO = new GameRedisDTO();
            gameRedisDTO.first = game.getFirst().getFirstName();
            gameRedisDTO.first = game.getFirst().getAvatar();
            gameRedisDTO.gameId = game.getId();
            RedisUtil.addHashItem("invisible", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));

        } else {
            gameRedisDTO = RedisUtil.getHashItem("half", RedisUtil.getFields("half"));
            Game game = gameRepository.findOne(gameRedisDTO.gameId);
            game.setSecond(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
            gameRedisDTO.second = SecurityUtils.getCurrentUserLogin();
            gameRedisDTO.second = game.getSecond().getAvatar();
            RedisUtil.removeItem("half", game.getId().toString());
            RedisUtil.addHashItem("full", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));

        }

        return ResponseEntity.ok(new ObjectMapper().writeValueAsString(gameRedisDTO));
    }


    @RequestMapping(value = "/1/createGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> createGame(@RequestBody String data, HttpServletResponse response) throws JsonProcessingException {

        String[] s = data.split(",");
        Game game = gameRepository.findOne(Long.valueOf(s[0]));
        AbstractGame abstractGame = abstractGameRepository.findOne(Long.valueOf(s[1]));
        List<Challenge> challengeList = game.getChallenges();
        Challenge challenge = new Challenge();
        challenge.setIcon(abstractGame.getIcon());
        challenge.setName(abstractGame.getName());
        GameRedisDTO gameRedisDTO = new GameRedisDTO();
        if (challengeList.size() % 2 == 0) {
            challenge.setFirstScore("-1");
            gameRedisDTO.first = game.getFirst().getLogin();
            RedisUtil.removeItem("invisible", game.getId().toString());
        } else {
            challenge.setSecondScore("-1");
            gameRedisDTO.second = game.getSecond().getLogin();
        }
        challengeRepository.save(challenge);
        game.setChallenges(challengeList);
        gameRedisDTO.gameId = game.getId();
        gameRedisDTO.challengeList = challengeList;
        gameRepository.save(game);
        RedisUtil.addHashItem("half", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));


        return ResponseEntity.ok("200");
    }


    @RequestMapping(value = "/1/joinGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> joinGame(@RequestBody String data, HttpServletResponse response) throws JsonProcessingException {

        String[] s = data.split(",");
        Game game = gameRepository.findOne(Long.valueOf(s[0]));
        AbstractGame abstractGame = abstractGameRepository.findOne(Long.valueOf(s[1]));

        final Challenge[] challenge = {new Challenge()};
        List<Challenge> challengeList = game.getChallenges();
        challengeList.forEach(c -> {
            if (c.getName().equalsIgnoreCase(abstractGame.getName())) {
                challenge[0] = c;
            }
        });
        GameRedisDTO gameRedisDTO = new GameRedisDTO();
        if (!challenge[0].getSecondScore().isEmpty()) {
            challenge[0].setFirstScore("-1");
            gameRedisDTO.first = game.getFirst().getLogin();
        } else {
            challenge[0].setSecondScore("-1");
            gameRedisDTO.second = game.getSecond().getLogin();
        }
        challengeRepository.save(challenge[0]);
        game.setChallenges(challengeList);
        gameRedisDTO.gameId = game.getId();
        gameRedisDTO.challengeList = challengeList;
        gameRepository.save(game);
        RedisUtil.addHashItem("half", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));


        return ResponseEntity.ok("200");
    }


    @RequestMapping(value = "/1/requestLeague", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> requestLeague(HttpServletResponse response) {

        //join game

        return ResponseEntity.ok("200");
    }

    @RequestMapping(value = "/1/availableLeague", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> availableLeague(HttpServletResponse response) {
        //true false
        return ResponseEntity.ok("true");
    }

    @RequestMapping(value = "/1/category", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> category(HttpServletResponse response) throws JsonProcessingException {
        //true false
        return ResponseEntity.ok(new ObjectMapper().writeValueAsString(categoryRepository.findAll()));
    }

    @RequestMapping(value = "/1/games", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> games(@RequestBody long catId, HttpServletResponse response) throws JsonProcessingException {


        return ResponseEntity.ok(new ObjectMapper().writeValueAsString(abstractGameRepository.findByGameCategory(categoryRepository.findOne(catId))));
    }


    @RequestMapping(value = "/1/gameById", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> gameById(@RequestBody long gameId, HttpServletResponse response) throws JsonProcessingException {


        return ResponseEntity.ok(new ObjectMapper().writeValueAsString(abstractGameRepository.findOne(gameId)));
    }

    @RequestMapping(value = "/1/thirdGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> thirdGame(@RequestBody long gameId, HttpServletResponse response) {
        return ResponseEntity.ok(gameRepository.findOne(1l));
    }

    @RequestMapping(value = "/1/detailGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> detailGame(@RequestBody long gameId, HttpServletResponse response) throws JsonProcessingException {
        //returnGame
        return ResponseEntity.ok(new ObjectMapper().writeValueAsString(gameRepository.findOne(gameId).getChallenges()));
    }

    @RequestMapping(value = "/1/detailLeague", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> detailLeague(@RequestBody long league, HttpServletResponse response) {
        //returnGame
        return ResponseEntity.ok("true");
    }


    @RequestMapping(value = "/1/endGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> endGame(@RequestBody String data, HttpServletResponse response) throws JsonProcessingException {
        String[] s = data.split(",");
        Game game = gameRepository.findOne(Long.valueOf(s[0]));
        for (Challenge challenge : game.getChallenges()) {
            if (challenge.getId() == Long.valueOf(s[1])) {
                if (SecurityUtils.getCurrentUserLogin() == game.getFirst().getLogin())
                    challenge.setFirstScore(s[2]);
                else
                    challenge.setSecondScore(s[2]);
                challengeRepository.save(challenge);
            }
        }
        gameRepository.save(game);
        GameRedisDTO gameRedisDTO = new GameRedisDTO();
        gameRedisDTO.first = game.getFirst().getLogin();
        if(game.getSecond()!=null) {
            gameRedisDTO.second = game.getSecond().getLogin();
        }
        gameRedisDTO.gameId = game.getId();
        gameRedisDTO.challengeList = game.getChallenges();
        RedisUtil.addHashItem("full", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));
        return ResponseEntity.ok("200");
    }

}
