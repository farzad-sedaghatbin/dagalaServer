package ir.company.app.service.migmig;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.company.app.config.Constants;
import ir.company.app.domain.entity.*;
import ir.company.app.repository.*;
import ir.company.app.security.SecurityUtils;
import ir.company.app.service.UserService;
import ir.company.app.service.dto.DetailDTO;
import ir.company.app.service.dto.GameRedisDTO;
import ir.company.app.service.util.RedisUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.String.valueOf;

@RestController
@RequestMapping("/api")
public class BusinessService {
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final RecordRepository recordRepository;
    private final LevelRepository levelRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final GameRepository gameRepository;
    private final CategoryUserRepository categoryUserRepository;
    private final PolicyRepository policyRepository;
    private final AbstractGameRepository abstractGameRepository;
    @PersistenceContext
    private EntityManager em;

    @Inject
    public BusinessService(LevelRepository levelRepository, UserRepository userRepository, ChallengeRepository challengeRepository, RecordRepository recordRepository, UserService userService, CategoryRepository categoryRepository, GameRepository gameRepository, AbstractGameRepository abstractGameRepository, CategoryUserRepository categoryUserRepository, PolicyRepository policyRepository) {
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
    }


    @RequestMapping(value = "/1/records", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> records(@RequestBody String data) throws JsonProcessingException {

        AbstractGame abstractGame = abstractGameRepository.findOne(Long.valueOf(data));
        Page<Record> records = recordRepository.findByAbstractGame(abstractGame, new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "score")));
        List<RecordDTO.User> recordDTOS = new ArrayList<>();
        final int[] i;
        i = new int[]{0};
        for (Record record1 : records.getContent()) {

            RecordDTO.User recordDTO = new RecordDTO.User();
            recordDTO.avatar = record1.getUser().getAvatar();
            recordDTO.score = record1.getScore();
            recordDTO.index = i[0]++ % 4;
            recordDTO.user = record1.getUser().getLogin();
            recordDTOS.add(recordDTO);
        }
        RecordDTO recordDTO = new RecordDTO();
        User u = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

        Record record = recordRepository.findByAbstractGameAndUser(abstractGame, u);
        if (record != null) {
            recordDTO.score = record.getScore();


            Query q = em.createNativeQuery("SELECT * FROM (SELECT id,rank() OVER (ORDER BY score DESC) FROM tb_record ) as gr WHERE  id =?");
            q.setParameter(1, u.getId());
            Object[] o = (Object[]) q.getSingleResult();
            recordDTO.rank = valueOf(o[1]);
            recordDTO.users = recordDTOS;
        } else {
            recordDTO.score = 0l;
            recordDTO.rank = "-";

        }
        return ResponseEntity.ok(recordDTO);
    }


    @RequestMapping(value = "/1/topPlayer", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> topPlayer() throws JsonProcessingException {


        Page<Object[]> topPlayers = userRepository.topPlayer(new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "score")));
        List<RecordDTO.User> recordDTOS = new ArrayList<>();
        final int[] i = {0};
        for (Object[] topPlayer : topPlayers.getContent()) {

            RecordDTO.User recordDTO = new RecordDTO.User();
            recordDTO.avatar = valueOf(topPlayer[0]);
            recordDTO.score = Long.parseLong(valueOf(topPlayer[1]));
            recordDTO.index = i[0]++ % 4;
            recordDTO.user = valueOf(topPlayer[2]);
            recordDTOS.add(recordDTO);
        }
        RecordDTO recordDTO = new RecordDTO();
        User u = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

        recordDTO.score = u.getScore();


        Query q = em.createNativeQuery("SELECT * FROM (SELECT id,rank() OVER (ORDER BY score DESC) FROM jhi_user ) as gr WHERE  id =?");
        q.setParameter(1, u.getId());
        Object[] o = (Object[]) q.getSingleResult();
        recordDTO.rank = valueOf(o[1]);
        recordDTO.users = recordDTOS;
        return ResponseEntity.ok(recordDTO);
    }


    @RequestMapping(value = "/1/cancelRequest", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> cancelRequest(@RequestBody Long data) throws JsonProcessingException {
        gameRepository.delete(data);
        return ResponseEntity.ok("200");
    }

    @RequestMapping(value = "/1/requestGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> requestGame() throws JsonProcessingException {
        GameRedisDTO gameRedisDTO = new GameRedisDTO();
        if (RedisUtil.sizeOfMap("half") == 0) {
            Game game = new Game();
            GameRedisDTO.User first = new GameRedisDTO.User();
            gameRedisDTO.first = first;
            game.setGameStatus(GameStatus.INVISIBLE);
            game.setFirst(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
            gameRepository.save(game);
            first.user = game.getFirst().getLogin();
            first.avatar = game.getFirst().getAvatar();
            User u = game.getFirst();
            u.setCoin(u.getCoin() - Constants.perGame);
            userRepository.save(u);
            gameRedisDTO.gameId = game.getId();
            RedisUtil.addHashItem("invisible", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));

        } else {
            gameRedisDTO = RedisUtil.getHashItem("half", RedisUtil.getFields("half"));
            GameRedisDTO.User second = new GameRedisDTO.User();
            gameRedisDTO.second = second;
            Game game = gameRepository.findOne(gameRedisDTO.gameId);
            game.setSecond(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
            second.user = SecurityUtils.getCurrentUserLogin();
            second.avatar = game.getSecond().getAvatar();
            gameRepository.save(game);
            User u = game.getSecond();
            u.setCoin(u.getCoin() - Constants.perGame);
            userRepository.save(u);
            RedisUtil.removeItem("half", game.getId().toString());
            RedisUtil.addHashItem("full", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));

        }

        return ResponseEntity.ok(gameRedisDTO);
    }


    @RequestMapping(value = "/1/createGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> createGame(@RequestBody String data) throws JsonProcessingException {

        String[] s = data.split(",");
        Game game = gameRepository.findOne(Long.valueOf(s[0]));
        AbstractGame abstractGame = abstractGameRepository.findOne(Long.valueOf(s[1]));
        List<Challenge> challengeList = game.getChallenges();
        Challenge challenge = new Challenge();
        challenge.setIcon(abstractGame.getIcon());
        challenge.setName(abstractGame.getName());
        challenge.setUrl(abstractGame.getUrl());
        GameRedisDTO gameRedisDTO = new GameRedisDTO();
        gameRedisDTO.first = new GameRedisDTO.User();
        if (challengeList.size() == 0) {
            challenge.setFirstScore("-1");
            gameRedisDTO.first.user = game.getFirst().getLogin();
            gameRedisDTO.first.avatar = game.getFirst().getAvatar();


        } else if (challengeList.size() == 1) {
            gameRedisDTO.second = new GameRedisDTO.User();
            challenge.setSecondScore("-1");
            gameRedisDTO.second.user = game.getSecond().getLogin();
            gameRedisDTO.second.avatar = game.getSecond().getAvatar();

        }
        if (challengeList == null) {
            challengeList = new ArrayList<>();
        }
        challengeList.add(challenge);
        challengeRepository.save(challenge);
        game.setChallenges(challengeList);
        gameRedisDTO.gameId = game.getId();
        gameRedisDTO.challengeList = challengeList;
        if (challengeList.size() == 1 && challenge.getSecondScore() == null) {
            RedisUtil.addHashItem("half", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));
            RedisUtil.removeItem("invisible", game.getId().toString());
            game.setGameStatus(GameStatus.HALF);

        } else
            RedisUtil.addHashItem("full", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));
        game.setGameStatus(GameStatus.FULL);

        gameRepository.save(game);

        return ResponseEntity.ok(challenge.getId());
    }


    @RequestMapping(value = "/1/joinGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> joinGame(@RequestBody String data) throws JsonProcessingException {

        String[] s = data.split(",");
        Game game = gameRepository.findOne(Long.valueOf(s[0]));


        List<Challenge> challengeList = game.getChallenges();
        if (challengeList.size() > 1) {
            GameRedisDTO gameRedisDTO = new GameRedisDTO();
            gameRedisDTO.first = new GameRedisDTO.User();
            gameRedisDTO.second = new GameRedisDTO.User();
            gameRedisDTO.first.user = game.getFirst().getLogin();
            gameRedisDTO.first.avatar = game.getFirst().getAvatar();
            gameRedisDTO.second.avatar = game.getSecond().getAvatar();
            gameRedisDTO.second.user = game.getSecond().getAvatar();
            Challenge challenge;
            if (challengeList.size() == 2) {
                challenge = game.getChallenges().get(1);


                if (SecurityUtils.getCurrentUserLogin().equalsIgnoreCase(game.getFirst().getLogin())) {
                    challenge.setFirstScore("-1");
                } else {
                    challenge.setSecondScore("-1");
                }
            } else {
                challenge = game.getChallenges().get(2);

                if (SecurityUtils.getCurrentUserLogin().equalsIgnoreCase(game.getFirst().getLogin())) {
                    challenge.setFirstScore("-1");
                } else {
                    challenge.setSecondScore("-1");
                }
            }

            challengeRepository.save(challenge);
            gameRepository.save(game);
            JoinResult joinResult = new JoinResult();
            joinResult.lastUrl = challenge.getUrl();
            joinResult.challengeId = challenge.getId();
            return ResponseEntity.ok(joinResult);

        } else {
            final Challenge[] challenge = {new Challenge()};

            challengeList.forEach(c -> {
                if (c.getId().equals(Long.valueOf(s[1]))) {
                    challenge[0] = c;
                }
            });
            GameRedisDTO gameRedisDTO = new GameRedisDTO();
            gameRedisDTO.first = new GameRedisDTO.User();
            gameRedisDTO.second = new GameRedisDTO.User();
            challenge[0].setFirstScore(challenge[0].getFirstScore());
            gameRedisDTO.first.user = game.getFirst().getLogin();
            gameRedisDTO.first.avatar = game.getFirst().getAvatar();
            challenge[0].setSecondScore("-1");
            if (game.getSecond() != null) {
                gameRedisDTO.second.user = game.getSecond().getLogin();
                gameRedisDTO.second.avatar = game.getSecond().getAvatar();
            } else {
                User u = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
                gameRedisDTO.second.user = u.getLogin();
                gameRedisDTO.second.avatar = u.getAvatar();
            }
            challengeRepository.save(challenge[0]);
            game.setChallenges(challengeList);
            gameRedisDTO.gameId = game.getId();
            gameRedisDTO.challengeList = challengeList;
            game.setDateTime(ZonedDateTime.now().plusDays(1));
            gameRepository.save(game);
            RedisUtil.addHashItem("full", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));

            JoinResult joinResult = new JoinResult();
            joinResult.lastUrl = challenge[0].getUrl();
            joinResult.challengeId = challenge[0].getId();
            return ResponseEntity.ok(joinResult);
        }
    }


    @RequestMapping(value = "/1/requestLeague", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> requestLeague() {

        //join game

        return ResponseEntity.ok("200");
    }

    @RequestMapping(value = "/1/availableLeague", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> availableLeague() {
        //true false
        return ResponseEntity.ok("true");
    }

    @RequestMapping(value = "/1/category", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> category() throws JsonProcessingException {
        //true false
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @RequestMapping(value = "/1/games", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> games(@RequestBody long catId) throws JsonProcessingException {
        List<MenuDTO> menuDTOS = new ArrayList<>();
        List<AbstractGame> menus = abstractGameRepository.findByGameCategory(categoryRepository.findOne(catId));
        menus.forEach(menu -> {
            MenuDTO menuDTO = new MenuDTO();
            menuDTO.adr = menu.getUrl();
            menuDTO.id = menu.getId();
            menuDTO.menuicon = menu.getIcon();
            menuDTO.style = "{\"font-size\": \"large\"}";
            menuDTO.text = "";
            menuDTOS.add(menuDTO);

        });
        return ResponseEntity.ok(menuDTOS);

    }


    @RequestMapping(value = "/1/gameById", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> gameById(@RequestBody long gameId) throws JsonProcessingException {


        return ResponseEntity.ok(abstractGameRepository.findOne(gameId));
    }


    public AbstractGame thirdGame(long gameId) throws JsonProcessingException {
        List<AbstractGame> abstractGames = abstractGameRepository.findAll();
        Game game = gameRepository.findOne(gameId);
        List<AbstractGame> longSet = new ArrayList<>();
        for (AbstractGame abstractGame : abstractGames) {
            longSet.add(abstractGame);
        }
        for (AbstractGame abstractGame : longSet) {
            for (Challenge game1 : game.getChallenges()) {
                if (game1.getId() == abstractGame.getId()) {
                    abstractGames.remove(abstractGame);
                }
            }
        }
        Random r = new Random();
        return abstractGames.get(r.nextInt(abstractGames.size() + 1));
    }

    @RequestMapping(value = "/1/detailGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> detailGame(@RequestBody long gameId) throws JsonProcessingException {
        Game game = gameRepository.findOne(gameId);
        DetailDTO detailDTO = new DetailDTO();
        detailDTO.gameId = game.getId();
        DetailDTO.User secondUser = new DetailDTO.User();
        detailDTO.user = secondUser;
        if (SecurityUtils.getCurrentUserLogin().equalsIgnoreCase(game.getFirst().getLogin())) {
            if (game.getSecond() != null) {
                secondUser.user = game.getSecond().getLogin();
                secondUser.avatar = game.getSecond().getAvatar();
            }
        } else {
            secondUser.user = game.getFirst().getLogin();
            secondUser.avatar = game.getFirst().getAvatar();
        }
        final int[] first = {0};
        final int[] second = {0};
        if (game.getDateTime() != null)
            detailDTO.timeLeft = (game.getDateTime().toInstant().toEpochMilli() - ZonedDateTime.now().toInstant().toEpochMilli()) / 1000;

        game.getChallenges().forEach(challenge -> {
            DetailDTO.GameDTO gameDTO = new DetailDTO.GameDTO();
            gameDTO.icon = challenge.getIcon();
            if (game.getFirst().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
                if (challenge.getFirstScore() != null && challenge.getSecondScore() != null) {
                    gameDTO.myScore = challenge.getFirstScore();
                    gameDTO.secondScore = challenge.getSecondScore();
                } else if ((challenge.getFirstScore() != null && challenge.getSecondScore() == null)) {
                    gameDTO.myScore = challenge.getFirstScore();
                    gameDTO.secondScore = "???";
                } else {
                    gameDTO.secondScore = "???";
                    gameDTO.myScore = "???";
                }

            } else {

                if (challenge.getFirstScore() != null && challenge.getSecondScore() != null) {
                    gameDTO.myScore = challenge.getSecondScore();
                    gameDTO.secondScore = challenge.getFirstScore();

                } else if ((challenge.getFirstScore() != null && challenge.getSecondScore() == null)) {
                    gameDTO.secondScore = "???";
                    gameDTO.myScore = "???";
                } else {
                    gameDTO.secondScore = "???";
                    gameDTO.myScore = challenge.getSecondScore();
                }

            }
            detailDTO.gameDTOS.add(gameDTO);
            calculateScore(first, second, challenge);

            gameDTO.challengeId = challenge.getId();

            if (challenge.getSecondScore() != null && !challenge.getSecondScore().isEmpty() && (challenge.getFirstScore() == null || challenge.getFirstScore().isEmpty()) && game.getFirst().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
                detailDTO.status = "1";
                detailDTO.url = challenge.getUrl();
            }
            if (challenge.getSecondScore() != null && !challenge.getSecondScore().isEmpty() && (challenge.getFirstScore() == null || challenge.getFirstScore().isEmpty()) && !game.getFirst().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
                detailDTO.status = "2";
            }

            if (challenge.getFirstScore() != null && !challenge.getFirstScore().isEmpty() && (challenge.getSecondScore() == null || challenge.getSecondScore().isEmpty()) && (game.getSecond() != null) && game.getSecond().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
                detailDTO.status = "1";
                if (game.getChallenges().size() == 1) {
                    detailDTO.url = challenge.getUrl();
                }
            }
            if (challenge.getFirstScore() != null && !challenge.getFirstScore().isEmpty() && (challenge.getSecondScore() == null || challenge.getSecondScore().isEmpty()) && (game.getSecond() != null) && !game.getSecond().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
                detailDTO.status = "2";
            }

        });
        if (game.getChallenges().size() == 1 && (detailDTO.status == null || detailDTO.status.isEmpty())) {
            if ((game.getSecond() != null) && game.getSecond().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin()))
                detailDTO.status = "1";
            else {
                detailDTO.status = "2";

            }
        }
        if (game.getChallenges().size() == 2 && (detailDTO.status == null || detailDTO.status.isEmpty())) {
            detailDTO.status = "3";
            AbstractGame abstractGame = thirdGame(gameId);
            detailDTO.url = abstractGame.getUrl();
            List<Challenge> challengeList = game.getChallenges();
            Challenge challenge = new Challenge();
            challenge.setIcon(abstractGame.getIcon());
            challenge.setName(abstractGame.getName());
            challenge.setUrl(abstractGame.getUrl());
            challengeRepository.save(challenge);
            challengeList.add(challenge);
            gameRepository.save(game);

        }
        if (game.getChallenges().size() == 3 && (detailDTO.status == null || detailDTO.status.isEmpty())) {
            detailDTO.url = game.getChallenges().get(2).getUrl();
            detailDTO.status = "3";

        }
        if (game.getFirst().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
            detailDTO.score = first[0];

            secondUser.score = second[0];
        } else {
            detailDTO.score = second[0];

            secondUser.score = first[0];
        }

        return ResponseEntity.ok(detailDTO);
    }

    @RequestMapping(value = "/1/detailLeague", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> detailLeague(@RequestBody long league) {
        //returnGame
        return ResponseEntity.ok("true");
    }


    @RequestMapping(value = "/1/stopGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> stopGame(@RequestBody String data) throws JsonProcessingException {

        Game game = gameRepository.findOne(Long.valueOf(data));

        User firsUser = game.getFirst();
        User secondUser = game.getSecond();
        boolean newLevel;

        if (SecurityUtils.getCurrentUserLogin().equalsIgnoreCase(game.getFirst().getLogin())) {
            game.setWinner(2);
            secondUser.setScore(secondUser.getScore() + 10);
            secondUser.setCoin(secondUser.getCoin() + 200);
        } else {
            game.setWinner(1);
            firsUser.setScore(firsUser.getScore() + 10);
            firsUser.setCoin(firsUser.getCoin() + 200);

        }

        userRepository.save(firsUser);
        userRepository.save(secondUser);
        newLevel = isNewLevel(firsUser, secondUser);
        game.setGameStatus(GameStatus.FINISHED);

        RedisUtil.removeItem("full", game.getId().toString());

        gameRepository.save(game);


        return ResponseEntity.ok(userService.refresh(newLevel));
    }

    @RequestMapping(value = "/1/timeOut", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> timeOut(@RequestBody String data) throws JsonProcessingException {

        Game game = gameRepository.findOne(Long.valueOf(data));

        User firsUser = game.getFirst();
        User secondUser = game.getSecond();
        final boolean[] hasWinner = {false};

        game.getChallenges().forEach(challenge -> {
            if (challenge.getFirstScore() == null || challenge.getFirstScore().isEmpty()) {

                game.setWinner(2);
                secondUser.setScore(secondUser.getScore() + 10);
                secondUser.setCoin(secondUser.getCoin() + 200);
                hasWinner[0] = true;
            } else if (challenge.getSecondScore() == null || challenge.getSecondScore().isEmpty()) {
                game.setWinner(1);
                firsUser.setScore(firsUser.getScore() + 10);
                firsUser.setCoin(firsUser.getCoin() + 200);
                hasWinner[0] = true;
            }
        });
        if (!hasWinner[0] && game.getChallenges().size() == 1) {
            game.setWinner(1);
            firsUser.setScore(firsUser.getScore() + 10);
            firsUser.setCoin(firsUser.getCoin() + 200);
        }

        userRepository.save(firsUser);
        userRepository.save(secondUser);
        boolean newLevel = isNewLevel(firsUser, secondUser);
        game.setGameStatus(GameStatus.FINISHED);

        RedisUtil.removeItem("full", game.getId().toString());

        gameRepository.save(game);


        return ResponseEntity.ok(userService.refresh(newLevel));
    }

    private boolean isNewLevel(User firsUser, User secondUser) {
        if (SecurityUtils.getCurrentUserLogin().equalsIgnoreCase(firsUser.getLogin())) {
            Level level = levelRepository.findByLevel(firsUser.getLevel());
            if (firsUser.getScore() > level.getThreshold()) {
                return true;
            }
        } else {
            Level level = levelRepository.findByLevel(secondUser.getLevel());
            if (secondUser.getScore() > level.getThreshold()) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping(value = "/1/endGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")


    public ResponseEntity<?> endGame(@RequestBody String data) throws JsonProcessingException {
        String[] s = data.split(",");
        Game game = gameRepository.findOne(Long.valueOf(s[0]));
        int index = 1;
        for (Challenge challenge : game.getChallenges()) {
            if (challenge.getId().equals(Long.valueOf(s[1]))) {
                if (SecurityUtils.getCurrentUserLogin().equalsIgnoreCase(game.getFirst().getLogin())) {
                    challenge.setFirstScore(s[2]);
                    AbstractGame abstractGame = abstractGameRepository.findByName(challenge.getName());
                    Record record = recordRepository.findByAbstractGameAndUser(abstractGame, game.getFirst());
                    if (record == null) {
                        record = new Record();
                        record.setUser(game.getFirst());
                        record.setAbstractGame(abstractGame);
                        record.setScore(Long.parseLong(s[2]));
                        recordRepository.save(record);

                    }
                    if ((record != null) && (record.getScore() < Long.valueOf(challenge.getFirstScore()))) {
                        record.setScore(Long.valueOf(challenge.getFirstScore()));
                        record.setUser(game.getFirst());
                        record.setAbstractGame(abstractGame);
                        recordRepository.save(record);
                    }
                } else {
                    challenge.setSecondScore(s[2]);
                    AbstractGame abstractGame = abstractGameRepository.findByName(challenge.getName());
                    Record record;
                    record = recordRepository.findByAbstractGameAndUser(abstractGame, game.getSecond());
                    if (record == null) {
                        record = new Record();
                        record.setUser(game.getSecond());
                        record.setAbstractGame(abstractGame);
                        record.setScore(Long.parseLong(s[2]));
                        recordRepository.save(record);
                    }
                    if (record != null && record.getScore() < Long.valueOf(challenge.getSecondScore())) {
                        record.setScore(Long.valueOf(challenge.getSecondScore()));
                        record.setUser(game.getSecond());
                        record.setAbstractGame(abstractGame);
                        recordRepository.save(record);
                    }
                }
                challengeRepository.save(challenge);

            }
            if (challenge.getFirstScore() != null && challenge.getSecondScore() != null && index == game.getChallenges().size()) {
                if (Long.valueOf(challenge.getFirstScore()) > Long.valueOf(challenge.getSecondScore())) {
                    game.setFirstScore(game.getFirstScore() + 1);
                } else if (Long.valueOf(challenge.getFirstScore()) < Long.valueOf(challenge.getSecondScore())) {
                    game.setSecondScore(game.getSecondScore() + 1);
                }
            }
            index++;
        }
        gameRepository.save(game);
        GameRedisDTO gameRedisDTO = new GameRedisDTO();
        gameRedisDTO.first = new GameRedisDTO.User();
        gameRedisDTO.first.user = game.getFirst().getLogin();
        gameRedisDTO.first.avatar = game.getFirst().getAvatar();
        if (game.getSecond() != null) {
            gameRedisDTO.second = new GameRedisDTO.User();
            gameRedisDTO.second.user = game.getSecond().getLogin();
            gameRedisDTO.second.avatar = game.getSecond().getAvatar();
        }
        boolean newLevel = false;
        gameRedisDTO.gameId = game.getId();
        gameRedisDTO.challengeList = game.getChallenges();
        if (game.getChallenges().size() == 3 && game.getChallenges().get(2).getFirstScore() != null && !game.getChallenges().get(2).getFirstScore().isEmpty() && game.getChallenges().get(2).getSecondScore() != null && !game.getChallenges().get(2).getSecondScore().isEmpty()) {

            User firstUser = game.getFirst();
            User secondUser = game.getSecond();
            int[] first = new int[0];
            int[] second = new int[0];
            game.getChallenges().forEach(challenge -> calculateScore(first, second, challenge));
            if (first[0] > second[0]) {
                game.setWinner(1);

            } else if (first[0] < second[0]) {
                game.setWinner(2);

            } else {
                game.setWinner(0);


            }

            if (game.getWinner() == 1) {
                firstUser.setWin(firstUser.getWin() + 1);
                firstUser.setWinInRow(firstUser.getWinInRow() + 1);
                if (firstUser.getWinInRow() > firstUser.getMaxWinInRow()) {
                    firstUser.setMaxWinInRow(firstUser.getWinInRow());
                }

                secondUser.setLose(secondUser.getLose() + 1);
                secondUser.setWinInRow(0);

                if (game.getFirstScore() - game.getSecondScore() > 1) {
                    firstUser.setScore(firstUser.getScore() + Constants.doubleWinEXP);
                    firstUser.setCoin(firstUser.getCoin() + Constants.doubleWinPrize);
                } else {
                    firstUser.setScore(firstUser.getScore() + Constants.winEXP);
                    secondUser.setScore(secondUser.getScore() + Constants.loseEXP);
                    firstUser.setCoin(firstUser.getCoin() + Constants.winPrize);
                    secondUser.setCoin(secondUser.getCoin() + Constants.losePrize);
                }
            } else if (game.getWinner() == 2) {
                secondUser.setWin(secondUser.getWin() + 1);
                secondUser.setWinInRow(secondUser.getWinInRow() + 1);
                if (secondUser.getWinInRow() > secondUser.getMaxWinInRow()) {
                    secondUser.setMaxWinInRow(secondUser.getWinInRow());
                }

                firstUser.setLose(firstUser.getLose() + 1);
                firstUser.setWinInRow(0);

                if (game.getSecondScore() - game.getFirstScore() > 1) {
                    secondUser.setScore(secondUser.getScore() + Constants.doubleLoseEXP);
                    secondUser.setCoin(secondUser.getCoin() + Constants.doubleWinPrize);
                } else {
                    firstUser.setScore(firstUser.getScore() + Constants.loseEXP);
                    secondUser.setScore(secondUser.getScore() + Constants.winEXP);
                    firstUser.setCoin(firstUser.getCoin() + Constants.losePrize);
                    secondUser.setCoin(secondUser.getCoin() + Constants.winPrize);
                }
            } else {

                secondUser.setDraw(secondUser.getDraw() + 1);
                secondUser.setWinInRow(0);
                firstUser.setDraw(firstUser.getDraw() + 1);
                firstUser.setWinInRow(0);
                firstUser.setScore(firstUser.getScore() + Constants.drawEXP);
                secondUser.setScore(secondUser.getScore() + Constants.drawEXP);
                firstUser.setCoin(firstUser.getCoin() + Constants.drawPrize);
                secondUser.setCoin(secondUser.getCoin() + Constants.drawPrize);
            }

            userRepository.save(firstUser);
            userRepository.save(secondUser);
            newLevel = isNewLevel(firstUser, secondUser);

            game.setGameStatus(GameStatus.FINISHED);
            gameRepository.save(game);

            RedisUtil.removeItem("full", game.getId().toString());

        }

        if (game.getChallenges().size() == 1 && game.getChallenges().get(0).getSecondScore() == null) {
            RedisUtil.addHashItem("half", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));
        } else {
            RedisUtil.addHashItem("full", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));
        }

        return ResponseEntity.ok(userService.refresh(newLevel));
    }

    private void calculateScore(int[] first, int[] second, Challenge challenge) {
        if (challenge.getSecondScore() != null && !challenge.getSecondScore().isEmpty() && challenge.getFirstScore() != null && !challenge.getFirstScore().isEmpty()) {
            if (Long.valueOf(challenge.getFirstScore()) > Long.valueOf(challenge.getSecondScore())) {
                first[0]++;
            } else if (Long.valueOf(challenge.getFirstScore()) < Long.valueOf(challenge.getSecondScore())) {
                second[0]++;
            } else {
                first[0]++;
                second[0]++;
            }
        }
    }


    @RequestMapping(value = "/1/submitRecord", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")


    public ResponseEntity<?> submitRecord(@RequestBody InputDTO data) throws
        JsonProcessingException {
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();

        AbstractGame abstractGame = abstractGameRepository.findOne(Long.valueOf(data.gameId));
        Record record = recordRepository.findByAbstractGameAndUser(abstractGame, user);
        if (record == null) {
            record = new Record();
            record.setUser(user);
            record.setAbstractGame(abstractGame);
            record.setScore(data.score);
            recordRepository.save(record);
        }
        if (record != null && record.getScore() < data.score) {
            record.setScore(data.score);
            record.setUser(user);
            record.setAbstractGame(abstractGame);
            recordRepository.save(record);
        }

        return ResponseEntity.ok("200");
    }

    @RequestMapping(value = "/1/refreshPolicy", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> refreshPolicy() {

        Constants.perGame = policyRepository.findByEPolicy(EPolicy.PER_GAME).getValue();
        Constants.newUser = policyRepository.findByEPolicy(EPolicy.NEW_USER).getValue();
        Constants.invite = policyRepository.findByEPolicy(EPolicy.INVITE).getValue();
        Constants.invited = policyRepository.findByEPolicy(EPolicy.INVITED).getValue();
        Constants.video = policyRepository.findByEPolicy(EPolicy.VIDEO).getValue();
        Constants.winEXP = policyRepository.findByEPolicy(EPolicy.WIN_EXP).getValue();
        Constants.loseEXP = policyRepository.findByEPolicy(EPolicy.LOSE_EXP).getValue();
        Constants.drawEXP = policyRepository.findByEPolicy(EPolicy.DRAW_EXP).getValue();
        Constants.doubleWinEXP = policyRepository.findByEPolicy(EPolicy.DOUBLE_WIN_EXP).getValue();
        Constants.doubleLoseEXP = policyRepository.findByEPolicy(EPolicy.DOUBLE_LOSE_EXP).getValue();
        Constants.winPrize = policyRepository.findByEPolicy(EPolicy.WIN_PRIZE).getValue();
        Constants.doubleWinPrize = policyRepository.findByEPolicy(EPolicy.DOUBLE_WIN_PRIZE).getValue();
        Constants.losePrize = policyRepository.findByEPolicy(EPolicy.LOSE_PRIZE).getValue();
        Constants.doubleLosePrize = policyRepository.findByEPolicy(EPolicy.DOUBLE_LOSE_PRIZE).getValue();
        Constants.drawPrize = policyRepository.findByEPolicy(EPolicy.DRAW_PRIZE).getValue();
        return ResponseEntity.ok("200");
    }
}
