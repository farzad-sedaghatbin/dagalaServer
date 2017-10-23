package ir.company.app.service.migmig;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.company.app.config.Constants;
import ir.company.app.domain.entity.*;
import ir.company.app.repository.*;
import ir.company.app.service.GameService;
import ir.company.app.service.UserService;
import ir.company.app.service.dto.DetailDTO;
import ir.company.app.service.dto.GameRedisDTO;
import ir.company.app.service.util.CalendarUtil;
import ir.company.app.service.util.RedisUtil;
import ir.company.app.service.wsdl.PaymentIFBindingLocator;
import ir.company.app.service.wsdl.PaymentIFBindingSoap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

@RestController
@RequestMapping("/api")
public class BusinessService {

    @Inject
    GameService gameService;
    private final UserRepository userRepository;
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
    @PersistenceContext
    private EntityManager em;

    @Inject
    public BusinessService(LevelRepository levelRepository, UserRepository userRepository, ChallengeRepository challengeRepository, RecordRepository recordRepository, UserService userService, CategoryRepository categoryRepository, GameRepository gameRepository, AbstractGameRepository abstractGameRepository, CategoryUserRepository categoryUserRepository, PolicyRepository policyRepository, LeagueRepository leagueRepository, FactorRepository factorRepository, MarketRepository marketRepository) {
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
    }


    @RequestMapping(value = "/1/records", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> records(@RequestBody String data) throws JsonProcessingException {

        String[] s = data.split(",");
        AbstractGame abstractGame = abstractGameRepository.findOne(Long.valueOf(s[0]));
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
        User u = userRepository.findOneByLogin(s[1].toLowerCase()).get();

        Record record = recordRepository.findByAbstractGameAndUser(abstractGame, u);
        if (record != null) {
            recordDTO.score = record.getScore();


            Query q = em.createNativeQuery("SELECT * FROM (SELECT id,user_id,abstract_game_id,rank() OVER (ORDER BY score DESC) FROM tb_record WHERE  abstract_game_id = ?) AS gr WHERE  user_id =? ");
            q.setParameter(2, u.getId());
            q.setParameter(1, Long.valueOf(s[0]));
            Object[] o = (Object[]) q.getSingleResult();
            recordDTO.rank = valueOf(o[3]);
        } else {
            recordDTO.score = 0l;
            recordDTO.rank = "-";

        }
        recordDTO.users = recordDTOS;
        return ResponseEntity.ok(recordDTO);
    }


    @RequestMapping(value = "/1/topPlayer", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> topPlayer(@RequestBody String user) throws JsonProcessingException {


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
        User u = userRepository.findOneByLogin(user.toLowerCase()).get();

        recordDTO.score = u.getScore();


        Query q = em.createNativeQuery("SELECT * FROM (SELECT id,rank() OVER (ORDER BY score DESC) FROM jhi_user ) AS gr WHERE  id =?");
        q.setParameter(1, u.getId());
        Object[] o = (Object[]) q.getSingleResult();
        recordDTO.rank = valueOf(o[1]);
        recordDTO.users = recordDTOS;
        return ResponseEntity.ok(recordDTO);
    }


    @RequestMapping(value = "/1/cancelRequest", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> cancelRequest(@RequestBody String data) throws JsonProcessingException {
        gameRepository.delete(Long.valueOf(data));
        RedisUtil.removeItem("invisible", data);
        return ResponseEntity.ok("200");
    }

    @RequestMapping(value = "/1/requestGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> requestGame(@RequestBody String username) throws JsonProcessingException {
        GameRedisDTO gameRedisDTO = new GameRedisDTO();
        User user = userRepository.findOneByLogin(username.toLowerCase()).get();
        if (RedisUtil.sizeOfMap("half") == 0) {
            Game game = new Game();
            GameRedisDTO.User first = new GameRedisDTO.User();
            gameRedisDTO.first = first;
            game.setGameStatus(GameStatus.INVISIBLE);
            game.setFirst(user);
            gameRepository.save(game);
            first.user = game.getFirst().getLogin();
            first.avatar = game.getFirst().getAvatar();
            User u = game.getFirst();
            u.setCoin(u.getCoin() - Constants.perGame);
            userRepository.save(u);
            gameRedisDTO.gameId = game.getId();
            RedisUtil.addHashItem("invisible", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));

        } else {
            gameRedisDTO = gameService.requestGame(user);
            if (gameRedisDTO == null) {
                gameRedisDTO = new GameRedisDTO();
                Game game = new Game();
                GameRedisDTO.User first = new GameRedisDTO.User();
                gameRedisDTO.first = first;
                game.setGameStatus(GameStatus.INVISIBLE);
                game.setFirst(user);
                gameRepository.save(game);
                first.user = game.getFirst().getLogin();
                first.avatar = game.getFirst().getAvatar();
                User u = game.getFirst();
                u.setCoin(u.getCoin() - Constants.perGame);
                userRepository.save(u);
                gameRedisDTO.gameId = game.getId();
                RedisUtil.addHashItem("invisible", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));
                return ResponseEntity.ok(gameRedisDTO);
            } else {
                GameRedisDTO.User second = new GameRedisDTO.User();
                gameRedisDTO.second = second;
                Game game = gameRepository.findOne(gameRedisDTO.gameId);
                game.setSecond(user);
                second.user = user.getLogin();
                second.avatar = game.getSecond().getAvatar();
                game.setGameStatus(GameStatus.FULL);
                gameRepository.save(game);
                RedisUtil.addHashItem("full", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));
                user.setCoin(user.getCoin() - Constants.perGame);
                userRepository.save(user);
            }

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
            challenge.setFirstScore("0");
            gameRedisDTO.first.user = game.getFirst().getLogin();
            gameRedisDTO.first.avatar = game.getFirst().getAvatar();


        } else if (challengeList.size() == 1) {
            gameRedisDTO.second = new GameRedisDTO.User();
            challenge.setSecondScore("0");
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

        } else {
            RedisUtil.addHashItem("full", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));
            game.setGameStatus(GameStatus.FULL);
        }
        gameRepository.save(game);

        return ResponseEntity.ok(challenge.getId());
    }


    @RequestMapping(value = "/1/createLeagueGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> createLeagueGame(@RequestBody String data) throws JsonProcessingException {

        String[] s = data.split(",");
        User user = userRepository.findOneByLogin(s[2].toLowerCase()).get();
        League league = leagueRepository.findOne(Long.valueOf(s[0]));
        Game game = gameRepository.findByFirstOrSecondAndLeagueAndGameStatus(user, user, league, GameStatus.FULL);

        AbstractGame abstractGame = abstractGameRepository.findOne(Long.valueOf(s[1]));
        List<Challenge> challengeList = game.getChallenges();
        Challenge challenge = new Challenge();
        challenge.setIcon(abstractGame.getIcon());
        challenge.setName(abstractGame.getName());
        challenge.setUrl(abstractGame.getUrl());
        if (challengeList.size() == 0) {
            challenge.setFirstScore("0");

        } else if (challengeList.size() == 1) {
            challenge.setSecondScore("0");

        }
        if (challengeList == null) {
            challengeList = new ArrayList<>();
        }
        challengeList.add(challenge);
        challengeRepository.save(challenge);
        game.setChallenges(challengeList);

        gameRepository.save(game);

        return ResponseEntity.ok(challenge.getId());
    }


    @RequestMapping(value = "/1/joinGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> joinGame(@RequestBody String data) throws JsonProcessingException {

        String[] s = data.split(",");
        Game game = gameRepository.findOne(Long.valueOf(s[0]));


        List<Challenge> challengeList = game.getChallenges().stream().sorted(Comparator.comparingLong(Challenge::getId)).collect(Collectors.toList());
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
                challenge = challengeList.get(1);


                if (s[2].equalsIgnoreCase(game.getFirst().getLogin())) {
                    challenge.setFirstScore("0");
                } else {
                    challenge.setSecondScore("0");
                }
            } else {

                if (challengeList.size() == 3 && challengeList.get(2).getFirstScore() != null && challengeList.get(2).getSecondScore() != null) {
                    JoinResult joinResult = new JoinResult();
                    joinResult.lastUrl = "";
                    joinResult.challengeId = null;
                    return ResponseEntity.ok(joinResult);
                }
                challenge = challengeList.get(2);

                if (s[2].equalsIgnoreCase(game.getFirst().getLogin())) {
                    challenge.setFirstScore("0");
                } else {
                    challenge.setSecondScore("0");
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
            challenge[0].setSecondScore("0");
            if (game.getSecond() != null) {
                gameRedisDTO.second.user = game.getSecond().getLogin();
                gameRedisDTO.second.avatar = game.getSecond().getAvatar();
            } else {
                User u = userRepository.findOneByLogin(s[2].toLowerCase()).get();
                gameRedisDTO.second.user = u.getLogin();
                gameRedisDTO.second.avatar = u.getAvatar();
            }
            challengeRepository.save(challenge[0]);
            game.setChallenges(challengeList);
            gameRedisDTO.gameId = game.getId();
            gameRedisDTO.challengeList = challengeList;
            game.setDateTime(ZonedDateTime.now().plusDays(1));
            game.setGameStatus(GameStatus.FULL);
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

    public ResponseEntity<?> requestLeague(@RequestBody String data) {

        String[] s = data.split(",");
        User user = userRepository.findOneByLogin(s[1].toLowerCase()).get();

        League league = leagueRepository.findOne(Long.valueOf(s[0]));
        if (league.getCapacity() - league.getFill() != 0) {
            league.setFill(league.getFill() + 1);
            league.getUserList().add(user);
            user.getLeagues().add(league);
            user.setGem(user.getGem() - league.getCost());
            userRepository.save(user);
            leagueRepository.save(league);
            return ResponseEntity.ok("200");
        } else if (league.getStatus().equals(StatusEnum.STARTED)) {
            return ResponseEntity.ok("202");
        }

        return ResponseEntity.ok("201");
    }


    @RequestMapping(value = "/1/availableLeague", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> availableLeague(@RequestBody String username) {
        User user = userRepository.findOneByLogin(username.toLowerCase()).get();

        List<League> list = leagueRepository.findAll();
        List<LeagueDTO> leagueDTOS = new ArrayList<>();
        int i = 0;
        for (League league : list) {
            LeagueDTO leagueDTO = new LeagueDTO();

            leagueDTO.capacity = league.getCapacity();
            leagueDTO.size = league.getFill();
            leagueDTO.minLevel = league.getMinLevel();
            leagueDTO.left = league.getCapacity() - league.getFill();
            leagueDTO.index = i % 4;
            leagueDTO.cost = league.getCost();
            leagueDTO.name = league.getName();
            leagueDTO.id = league.getId();
            switch (league.getStatus()) {
                case INIT:
                    boolean b = false;
                    for (League league1 : user.getLeagues()) {
                        if (league.getId() == league1.getId()) {
                            leagueDTO.status = 1;
                            b = true;
                        }
                    }
                    if (!b) {
                        leagueDTO.status = 0;
                    }
                    break;
                case STARTED:
                    boolean c = false;
                    for (League league1 : user.getLeagues()) {
                        if (league.getId() == league1.getId()) {
                            leagueDTO.status = 3;
                            c = true;
                        }
                    }
                    if (!c) {
                        leagueDTO.status = 2;
                    }
                    break;
                case FINISHED:
                    leagueDTO.status = 4;
                    break;
            }
            leagueDTO.startDate = CalendarUtil.getFormattedDate(league.getStartDate().toLocalDate(), "yyyy/MM/dd");
            leagueDTO.timeLeft = (league.getStartDate().toInstant().toEpochMilli() - ZonedDateTime.now().toInstant().toEpochMilli()) / 1000;
            for (PrizeLeague prizeLeague : league.getPrizeLeagues()) {
                leagueDTO.prizes.add(new LeagueDTO.Prize(prizeLeague.getIndex(), prizeLeague.getDescription()));
            }

            leagueDTOS.add(leagueDTO);
            i++;

        }

        return ResponseEntity.ok(leagueDTOS);
    }

    @RequestMapping(value = "/1/category", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> category() throws JsonProcessingException {
        //true false
        return ResponseEntity.ok(categoryRepository.findAll());
    }


    @RequestMapping(value = "/1/factor", method = RequestMethod.POST, produces = "text/plain")
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> factor(@Valid @RequestBody String data, HttpServletResponse response) {
        String[] s = data.split(",");
        User user = userRepository.findOneByLogin(s[1].toLowerCase()).get();
        Factor factor = new Factor();
        factor.setUser(user);
        factor.setMarketObject(marketRepository.findOne(Long.valueOf(s[2])));
        factor.setAmount(Long.valueOf(s[0]));
        factorRepository.save(factor);
        factor.setuID("DAG" + Integer.toHexString((System.identityHashCode(factor.getId()))).toUpperCase());
        factorRepository.save(factor);
        return ResponseEntity.ok(factor.getuID());
    }


    @RequestMapping(value = "/1/donePeyment", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    @Timed
    @CrossOrigin(origins = "*")

    public void donePeyment(@Valid @RequestBody String data, HttpServletResponse response) {

        try {
            String[] s = URLDecoder.decode(data, StandardCharsets.UTF_8.toString()).split("\\&");
            if (s[1].split("=")[1].equalsIgnoreCase("ok")) {
                String ss = s[6].split("=")[1];
                PaymentIFBindingLocator paymentIFBindingSoapStub = new PaymentIFBindingLocator();
                PaymentIFBindingSoap paymentIFBinding = null;
                try {
                    paymentIFBinding = (PaymentIFBindingSoap) paymentIFBindingSoapStub.getPort(PaymentIFBindingSoap.class);
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
                double d = paymentIFBinding.verifyTransaction(ss, "10822833");
                if (d < 0) {
                    response.sendRedirect("http://dagala.ir/error.html?code=" + d);

                } else {
                    Factor factor = factorRepository.findByUID(s[0]);
                    User user = factor.getUser();
                    MarketObject marketObject = factor.getMarketObject();
                    if (s[0].contains("gem")) {
                        user.setGem(user.getGem() + (int) marketObject.getAmount());
                    } else if (s[0].contains("exp")) {
                        user.setExpireExp(ZonedDateTime.now().plusDays((int) marketObject.getAmount()));
                        user.setExpRatio(Double.parseDouble(marketObject.getDescription()));
                    } else {
                        user.setCoin(user.getCoin() + (int) marketObject.getAmount());
                    }
                    userRepository.save(user);
                    response.sendRedirect("http://dagala.ir/payment.html?code=" + ss);

                }

            } else {
                response.sendRedirect("http://dagala.ir/error.html?code=" + s[1].split("=")[1]);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/1/inventory", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> inventory(@RequestBody String data) throws JsonProcessingException {
        String[] s = data.split(",");
        User user = userRepository.findOneByLogin(s[1].toLowerCase()).get();


        MarketObject marketObject = marketRepository.findByName(s[0]);

        if (marketObject.isCoin()) {
            user.setCoin(user.getCoin() - (Integer.valueOf(marketObject.getPrice())));
        }
        if (s[0].contains("gem")) {
            user.setGem(user.getGem() + (int) marketObject.getAmount());
        } else if (s[0].contains("exp")) {
            user.setExpireExp(ZonedDateTime.now().plusDays((int) marketObject.getAmount()));
            user.setExpRatio(Double.parseDouble(marketObject.getDescription()));
        } else {
            user.setCoin(user.getCoin() + (int) marketObject.getAmount());
        }
        userRepository.save(user);
        return ResponseEntity.ok(userService.refresh(false, user.getLogin()));
    }

    @RequestMapping(value = "/1/videoWatch", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> videoWatch(@RequestBody String username) throws JsonProcessingException {
        User user = userRepository.findOneByLogin(username.toLowerCase()).get();


        user.setCoin(user.getCoin() + Constants.video);
        userRepository.save(user);
        return ResponseEntity.ok("200");
    }


    @RequestMapping(value = "/1/expandMenu", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> expandMenu(@RequestBody String username) throws JsonProcessingException {
        User user = userRepository.findOneByLogin(username.toLowerCase()).get();


        user.setCoin(user.getCoin() - Constants.ExpandMenu);
        return ResponseEntity.ok("200");
    }

    @RequestMapping(value = "/1/marketObjects", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> marketObjects() throws JsonProcessingException {
        List<MarketObject> list = marketRepository.findAll();
        list.forEach(l -> {
            if (l.getName().contains("exp")) {
                l.setDescription("ضریب " + l.getAmount() + " برابری " + l.getDescription() + " روزه");
            }
        });
        return ResponseEntity.ok(list);
    }

    @RequestMapping(value = "/1/games", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> games(@RequestBody String data) throws JsonProcessingException {
        List<MenuDTO> menuDTOS = new ArrayList<>();
        String[] s = data.split(",");
        List<AbstractGame> menus = abstractGameRepository.findByGameCategory(categoryRepository.findOne(Long.valueOf(s[1])));
        List<Challenge> challenges;
        if (s[0].equalsIgnoreCase("train")) {
            challenges = new ArrayList<>();
        } else {
            challenges = gameRepository.findOne(Long.valueOf(s[0])).getChallenges();
        }
        menus.forEach(menu -> {
            boolean[] flag = {false};
            challenges.forEach(c -> {
                if (c.getName().equalsIgnoreCase(menu.getName())) {
                    flag[0] = true;
                }
            });
            MenuDTO menuDTO = new MenuDTO();
            menuDTO.adr = menu.getUrl();
            menuDTO.id = menu.getId();
            menuDTO.menuicon = menu.getIcon();
            menuDTO.style = "{\"font-size\": \"large\"}";
            menuDTO.style2 = flag[0];

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
                if (game1.getName().equalsIgnoreCase(abstractGame.getName())) {
                    abstractGames.remove(abstractGame);
                }
            }
        }
        Random r = new Random();
        return abstractGames.get(r.nextInt(abstractGames.size()));
    }

    @RequestMapping(value = "/1/detailGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> detailGame(@RequestBody String data) throws JsonProcessingException {
        String[] s = data.split(",");
        Game game = gameRepository.findOne(Long.valueOf(s[0]));
        List<Challenge> l = game.getChallenges().stream().sorted(Comparator.comparingLong(Challenge::getId)).collect(Collectors.toList());

        DetailDTO detailDTO = new DetailDTO();
        detailDTO.gameId = game.getId();
        DetailDTO.User secondUser = new DetailDTO.User();
        detailDTO.user = secondUser;
        if (s[1].equalsIgnoreCase(game.getFirst().getLogin())) {
            if (game.getSecond() != null) {
                secondUser.user = game.getSecond().getLogin();
                secondUser.avatar = game.getSecond().getAvatar();
                secondUser.level = game.getSecond().getLevel();
            }
        } else {
            secondUser.user = game.getFirst().getLogin();
            secondUser.avatar = game.getFirst().getAvatar();
        }
        final int[] first = {0};
        final int[] second = {0};
        if (game.getDateTime() != null)
            detailDTO.timeLeft = (game.getDateTime().toInstant().toEpochMilli() - ZonedDateTime.now().toInstant().toEpochMilli()) / 1000;

        l.forEach(challenge -> {
            DetailDTO.GameDTO gameDTO = new DetailDTO.GameDTO();
            gameDTO.icon = challenge.getIcon();
            if (game.getFirst().getLogin().equalsIgnoreCase(s[1])) {
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

            gameDTO.challengeId = challenge.getId();

            if (challenge.getSecondScore() != null && !challenge.getSecondScore().isEmpty() && (challenge.getFirstScore() == null || challenge.getFirstScore().isEmpty()) && game.getFirst().getLogin().equalsIgnoreCase(s[1])) {
                detailDTO.status = "1";
                detailDTO.url = challenge.getUrl();
            }
            if (challenge.getSecondScore() != null && !challenge.getSecondScore().isEmpty() && (challenge.getFirstScore() == null || challenge.getFirstScore().isEmpty()) && !game.getFirst().getLogin().equalsIgnoreCase(s[1])) {
                detailDTO.status = "2";
            }

            if (l.size() != 3 && challenge.getFirstScore() != null && !challenge.getFirstScore().isEmpty() && (challenge.getSecondScore() == null || challenge.getSecondScore().isEmpty()) && (game.getSecond() != null) && game.getSecond().getLogin().equalsIgnoreCase(s[1])) {
                detailDTO.status = "1";
                if (l.size() == 1) {
                    detailDTO.url = challenge.getUrl();
                }
            }
            if (challenge.getFirstScore() != null && !challenge.getFirstScore().isEmpty() && (challenge.getSecondScore() == null || challenge.getSecondScore().isEmpty()) && (game.getSecond() != null) && !game.getSecond().getLogin().equalsIgnoreCase(s[1])) {
                detailDTO.status = "2";
            }

        });
        if (l.size() == 1 && (detailDTO.status == null || detailDTO.status.isEmpty())) {
            if ((game.getSecond() != null) && game.getSecond().getLogin().equalsIgnoreCase(s[1]))
                detailDTO.status = "1";
            else {
                detailDTO.status = "2";

            }
        }
        if (l.size() == 2 && (detailDTO.status == null || detailDTO.status.isEmpty())) {
            detailDTO.status = "3";
            AbstractGame abstractGame = thirdGame(Long.parseLong(s[0]));
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
        if (l.size() == 3 && (detailDTO.status == null || detailDTO.status.isEmpty())) {
            detailDTO.url = l.get(2).getUrl();
            detailDTO.status = "3";

        }
        if (game.getFirst().getLogin().equalsIgnoreCase(s[1])) {
            detailDTO.score = game.getFirstScore();

            secondUser.score = game.getSecondScore();
        } else {
            detailDTO.score = game.getSecondScore();

            secondUser.score = game.getFirstScore();
        }

        return ResponseEntity.ok(detailDTO);
    }

    @RequestMapping(value = "/1/detailLeague", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> detailLeague(@RequestBody String data) {
        String[] s = data.split(",");
        User user = userRepository.findOneByLogin(s[1].toLowerCase()).get();
        League league = leagueRepository.findOne(Long.valueOf(s[0]));
        Game game = gameRepository.findByFirstOrSecondAndLeagueAndGameStatus(user, user, league, GameStatus.FULL);

        try {
            DetailDTO d = new DetailDTO();
            if (!game.getGameStatus().equals(GameStatus.FINISHED)) {
                d = gameService.detailGame(game);
            } else {
                d.timeLeft = null;
            }
            if (game.getChallenges().size() == 0) {
                if (user.getId().equals(game.getFirst().getId()))
                    d.status = "10";
            }
            return ResponseEntity.ok(d);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("200");

    }


    @RequestMapping(value = "/1/finishedLeague", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> finishedLeague(@RequestBody long id) {
        Page<Object[]> topPlayers = userRepository.topPlayer(new PageRequest(0, 20, new Sort(Sort.Direction.DESC, "score")));
        List<RecordDTO.User> recordDTOS = new ArrayList<>();
        final int[] i = {0};
        for (Object[] topPlayer : topPlayers.getContent()) {

            RecordDTO.User recordDTO = new RecordDTO.User();
            recordDTO.avatar = valueOf(topPlayer[0]);
            recordDTO.index = i[0]++ % 4;
            recordDTO.user = valueOf(topPlayer[2]);
            recordDTOS.add(recordDTO);
        }
        return ResponseEntity.ok(recordDTOS);

    }


    @RequestMapping(value = "/1/stopGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> stopGame(@RequestBody String data) throws JsonProcessingException {
        String[] s = data.split(",");
        Game game = gameRepository.findOne(Long.valueOf(s[0]));

        User firsUser = game.getFirst();
        User secondUser = game.getSecond();
        boolean newLevel;

        if (s[1].equalsIgnoreCase(game.getFirst().getLogin())) {
            game.setWinner(2);
            secondUser.setScore(secondUser.getScore() + Constants.doubleWinEXP);
            secondUser.setCoin(secondUser.getCoin() + Constants.doubleWinPrize);
        } else {
            game.setWinner(1);
            firsUser.setScore(firsUser.getScore() + Constants.doubleWinEXP);
            firsUser.setCoin(firsUser.getCoin() + Constants.doubleWinPrize);

        }

        userRepository.save(firsUser);
        userRepository.save(secondUser);
        newLevel = isNewLevel(firsUser, secondUser, s[1]);
        game.setGameStatus(GameStatus.FINISHED);

        RedisUtil.removeItem("full", game.getId().toString());

        gameRepository.save(game);


        return ResponseEntity.ok(userService.refresh(newLevel, s[1]));
    }

    @RequestMapping(value = "/1/timeOut", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")

    public ResponseEntity<?> timeOut(@RequestBody String data) throws JsonProcessingException {
        String[] s = data.split(",");
        Game game = gameRepository.findOne(Long.valueOf(s[0]));

        User firstUser = game.getFirst();
        User secondUser = game.getSecond();
        final boolean[] hasWinner = {false};

        game.getChallenges().forEach(challenge -> {
            if (challenge.getFirstScore() == null || challenge.getFirstScore().isEmpty()) {

                game.setWinner(2);
                secondUser.setScore(secondUser.getScore() + Constants.doubleWinEXP);
                secondUser.setCoin(secondUser.getCoin() + Constants.doubleWinPrize);
                hasWinner[0] = true;
            } else if (challenge.getSecondScore() == null || challenge.getSecondScore().isEmpty()) {
                game.setWinner(1);
                firstUser.setScore(firstUser.getScore() + Constants.doubleWinEXP);
                firstUser.setCoin(firstUser.getCoin() + Constants.doubleWinPrize);
                hasWinner[0] = true;
            }
        });
        if (!hasWinner[0] && game.getChallenges().size() == 1) {
            game.setWinner(1);
            firstUser.setScore(firstUser.getScore() + Constants.doubleWinEXP);
            firstUser.setCoin(firstUser.getCoin() + Constants.doubleWinPrize);
        }
        if (game.getWinner() == 1) {
            firstUser.setWin(firstUser.getWin() + 1);
            firstUser.setWinInRow(firstUser.getWinInRow() + 1);
            if (firstUser.getWinInRow() > firstUser.getMaxWinInRow()) {
                firstUser.setMaxWinInRow(firstUser.getWinInRow());
            }

            secondUser.setLose(secondUser.getLose() + 1);
            secondUser.setWinInRow(0);
        } else {
            secondUser.setWin(secondUser.getWin() + 1);
            secondUser.setWinInRow(secondUser.getWinInRow() + 1);
            if (secondUser.getWinInRow() > secondUser.getMaxWinInRow()) {
                secondUser.setMaxWinInRow(secondUser.getWinInRow());
            }

            firstUser.setLose(firstUser.getLose() + 1);
            firstUser.setWinInRow(0);
        }
        userRepository.save(firstUser);
        userRepository.save(secondUser);
        boolean newLevel = isNewLevel(firstUser, secondUser, s[1]);

        game.setGameStatus(GameStatus.FINISHED);

        RedisUtil.removeItem("full", game.getId().toString());

        gameRepository.save(game);


        return ResponseEntity.ok(userService.refresh(newLevel, s[1]));
    }

    private boolean isNewLevel(User firsUser, User secondUser, String username) {
        if (username.equalsIgnoreCase(firsUser.getLogin())) {
            Level level = levelRepository.findByLevel(firsUser.getLevel());
            if (firsUser.getScore() > level.getThreshold()) {
                firsUser.setLevel(firsUser.getLevel() + 1);
                userRepository.save(firsUser);
                return true;
            }
        } else {
            Level level = levelRepository.findByLevel(secondUser.getLevel());
            if (secondUser.getScore() > level.getThreshold()) {
                secondUser.setLevel(secondUser.getLevel() + 1);
                userRepository.save(secondUser);
                return true;
            }
        }
        return false;
    }

    @RequestMapping(value = "/1/endGame", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")


    public ResponseEntity<?> endGame(@RequestBody String data) {
        try {
            String[] s = data.split(",");
            Game game = gameRepository.findOne(Long.valueOf(s[0]));
            List<Challenge> l = game.getChallenges().stream().sorted(Comparator.comparingLong(Challenge::getId)).collect(Collectors.toList());

            int index = 1;
            for (Challenge challenge : l) {
                if (challenge.getId().equals(Long.valueOf(s[1]))) {
                    if (s[3].equalsIgnoreCase(game.getFirst().getLogin())) {
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
                if (challenge.getFirstScore() != null && challenge.getSecondScore() != null && index == l.size()) {
                    if (Long.valueOf(challenge.getFirstScore()) > Long.valueOf(challenge.getSecondScore())) {
                        game.setFirstScore(game.getFirstScore() + 1);
                    } else if (Long.valueOf(challenge.getFirstScore()) < Long.valueOf(challenge.getSecondScore())) {
                        game.setSecondScore(game.getSecondScore() + 1);
                    } else {
                        game.setSecondScore(game.getSecondScore() + 1);
                        game.setFirstScore(game.getFirstScore() + 1);

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
            gameRedisDTO.challengeList = l;
            if (l.size() == 3 && l.get(2).getFirstScore() != null && !l.get(2).getFirstScore().isEmpty() && l.get(2).getSecondScore() != null && !l.get(2).getSecondScore().isEmpty()) {

                User firstUser = game.getFirst();
                User secondUser = game.getSecond();
                if (game.getFirstScore() > game.getSecondScore()) {
                    game.setWinner(1);

                } else if (game.getFirstScore() < game.getSecondScore()) {
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
                        if (firstUser.getExpireExp() != null && firstUser.getExpireExp().isAfter(ZonedDateTime.now()))
                            firstUser.setScore(firstUser.getScore() + (int) (Constants.doubleWinEXP * firstUser.getExpRatio()));
                        else
                            firstUser.setScore(firstUser.getScore() + Constants.doubleWinEXP);
                        firstUser.setCoin(firstUser.getCoin() + Constants.doubleWinPrize);
                    } else {
                        if (firstUser.getExpireExp() != null && firstUser.getExpireExp().isAfter(ZonedDateTime.now()))
                            firstUser.setScore(firstUser.getScore() + (int) (Constants.doubleWinEXP * firstUser.getExpRatio()));
                        else
                            firstUser.setScore(firstUser.getScore() + Constants.winEXP);
                        if (secondUser.getExpireExp() != null && secondUser.getExpireExp().isAfter(ZonedDateTime.now()))
                            secondUser.setScore(secondUser.getScore() + (int) (Constants.doubleWinEXP * secondUser.getExpRatio()));
                        else
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
                        if (secondUser.getExpireExp() != null && secondUser.getExpireExp().isAfter(ZonedDateTime.now()))
                            secondUser.setScore(secondUser.getScore() + (int) (Constants.doubleWinEXP * secondUser.getExpRatio()));
                        else
                            secondUser.setScore(secondUser.getScore() + Constants.doubleLoseEXP);
                        secondUser.setCoin(secondUser.getCoin() + Constants.doubleWinPrize);
                    } else {
                        if (firstUser.getExpireExp() != null && firstUser.getExpireExp().isAfter(ZonedDateTime.now()))
                            firstUser.setScore(firstUser.getScore() + (int) (Constants.doubleWinEXP * firstUser.getExpRatio()));
                        else
                            firstUser.setScore(firstUser.getScore() + Constants.loseEXP);
                        if (secondUser.getExpireExp() != null && secondUser.getExpireExp().isAfter(ZonedDateTime.now()))
                            secondUser.setScore(secondUser.getScore() + (int) (Constants.doubleWinEXP * secondUser.getExpRatio()));
                        else
                            secondUser.setScore(secondUser.getScore() + Constants.winEXP);
                        firstUser.setCoin(firstUser.getCoin() + Constants.losePrize);
                        secondUser.setCoin(secondUser.getCoin() + Constants.winPrize);
                    }
                } else {

                    secondUser.setDraw(secondUser.getDraw() + 1);
                    secondUser.setWinInRow(0);
                    firstUser.setDraw(firstUser.getDraw() + 1);
                    firstUser.setWinInRow(0);
                    if (firstUser.getExpireExp() != null && firstUser.getExpireExp().isAfter(ZonedDateTime.now()))
                        firstUser.setScore(firstUser.getScore() + (int) (Constants.doubleWinEXP * firstUser.getExpRatio()));
                    else
                        firstUser.setScore(firstUser.getScore() + Constants.drawEXP);
                    if (secondUser.getExpireExp() != null && secondUser.getExpireExp().isAfter(ZonedDateTime.now()))
                        secondUser.setScore(secondUser.getScore() + (int) (Constants.doubleWinEXP * secondUser.getExpRatio()));
                    else
                        secondUser.setScore(secondUser.getScore() + Constants.drawEXP);
                    firstUser.setCoin(firstUser.getCoin() + Constants.drawPrize);
                    secondUser.setCoin(secondUser.getCoin() + Constants.drawPrize);
                }

                userRepository.save(firstUser);
                userRepository.save(secondUser);
                newLevel = isNewLevel(firstUser, secondUser, s[3]);

                game.setGameStatus(GameStatus.FINISHED);
                gameRepository.save(game);

                RedisUtil.removeItem("full", game.getId().toString());

            }

            if (l.size() == 1 && l.get(0).getSecondScore() == null) {
                RedisUtil.addHashItem("half", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));
            } else if (!game.getGameStatus().equals(GameStatus.FINISHED)) {
                RedisUtil.addHashItem("full", game.getId().toString(), new ObjectMapper().writeValueAsString(gameRedisDTO));
            }

            return ResponseEntity.ok(userService.refresh(newLevel, s[3]));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return ResponseEntity.ok("201");
    }


    @RequestMapping(value = "/1/submitRecord", method = RequestMethod.POST)
    @Timed
    @CrossOrigin(origins = "*")


    public ResponseEntity<?> submitRecord(@RequestBody InputDTO data) throws
        JsonProcessingException {
        User user = userRepository.findOneByLogin(data.user.toLowerCase()).get();

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
