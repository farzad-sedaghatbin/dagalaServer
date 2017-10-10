package ir.company.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.company.app.config.Constants;
import ir.company.app.domain.Authority;
import ir.company.app.domain.entity.*;
import ir.company.app.repository.*;
import ir.company.app.security.AuthoritiesConstants;
import ir.company.app.security.SecurityUtils;
import ir.company.app.service.dto.DetailDTO;
import ir.company.app.service.dto.GameLowDTO;
import ir.company.app.service.dto.GameRedisDTO;
import ir.company.app.service.dto.HomeDTO;
import ir.company.app.service.util.RandomUtil;
import ir.company.app.service.util.RedisUtil;
import ir.company.app.web.rest.vm.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class GameService {

    @Inject
    private GameRepository gameRepository;
    @Inject
    private AbstractGameRepository abstractGameRepository;
    @Inject
    private ChallengeRepository challengeRepository;
    @Inject
    private UserRepository userRepository;


    public DetailDTO detailGame(Game game) throws JsonProcessingException {
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

            gameDTO.challengeId = challenge.getId();

            if (challenge.getSecondScore() != null && !challenge.getSecondScore().isEmpty() && (challenge.getFirstScore() == null || challenge.getFirstScore().isEmpty()) && game.getFirst().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
                detailDTO.status = "1";
                detailDTO.url = challenge.getUrl();
            }
            if (challenge.getSecondScore() != null && !challenge.getSecondScore().isEmpty() && (challenge.getFirstScore() == null || challenge.getFirstScore().isEmpty()) && !game.getFirst().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
                detailDTO.status = "2";
            }

            if (game.getChallenges().size() != 3 && challenge.getFirstScore() != null && !challenge.getFirstScore().isEmpty() && (challenge.getSecondScore() == null || challenge.getSecondScore().isEmpty()) && (game.getSecond() != null) && game.getSecond().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
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
            AbstractGame abstractGame = thirdGame(game.getId());
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
            detailDTO.score = game.getFirstScore();

            secondUser.score = game.getSecondScore();
        } else {
            detailDTO.score = game.getSecondScore();

            secondUser.score = game.getFirstScore();
        }

        return detailDTO;
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


    public synchronized GameRedisDTO requestGame(User user) throws JsonProcessingException {
        GameRedisDTO gameRedisDTO;
        int i = 0;
        String field = RedisUtil.getFields("half", i);
        gameRedisDTO = RedisUtil.getHashItem("half", field);
        while (gameRedisDTO.first.user.equalsIgnoreCase(user.getLogin())) {
            field = RedisUtil.getFields("half", ++i);

            if (field == null) {
                return null;
            }
            gameRedisDTO = RedisUtil.getHashItem("half", field);
        }
        RedisUtil.removeItem("half", field);
        return gameRedisDTO;
    }
}
