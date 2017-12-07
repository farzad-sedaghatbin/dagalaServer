package ir.company.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.company.app.domain.entity.*;
import ir.company.app.repository.AbstractGameRepository;
import ir.company.app.repository.ChallengeRepository;
import ir.company.app.repository.GameRepository;
import ir.company.app.security.SecurityUtils;
import ir.company.app.service.dto.DetailDTO;
import ir.company.app.service.dto.GameRedisDTO;
import ir.company.app.service.util.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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


    public DetailDTO detailGame(Game game) throws JsonProcessingException {
        DetailDTO detailDTO = new DetailDTO();
        detailDTO.gameId = game.getId();
        DetailDTO.User secondUser = new DetailDTO.User();
        detailDTO.user = secondUser;
        if (SecurityUtils.getCurrentUserLogin().equalsIgnoreCase(game.getFirst().getLogin())) {
            if (game.getSecond() != null) {
                secondUser.user = game.getSecond().getLogin();
                secondUser.level = game.getSecond().getLevel();
                secondUser.avatar = game.getSecond().getAvatar();
                if(game.getMessagesSecond()!=null&&game.getMessagesSecond().size()>0)
                detailDTO.messages = game.getMessagesSecond().stream().map(Message::getId).collect(Collectors.toList());

            }
        } else {
            secondUser.user = game.getFirst().getLogin();
            secondUser.level = game.getFirst().getLevel();
            secondUser.avatar = game.getFirst().getAvatar();
            if(game.getMessagesFirst()!=null&&game.getMessagesFirst().size()>0)
            detailDTO.messages = game.getMessagesFirst().stream().map(Message::getId).collect(Collectors.toList());

        }
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
            gameDTO.id = challenge.getAbstractId();

            if (challenge.getSecondScore() != null && !challenge.getSecondScore().isEmpty() && (challenge.getFirstScore() == null || challenge.getFirstScore().isEmpty()) && game.getFirst().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
                detailDTO.status = "1";
                detailDTO.url = challenge.getUrl();
            }
            if (challenge.getSecondScore() != null && !challenge.getSecondScore().isEmpty() && (challenge.getFirstScore() == null || challenge.getFirstScore().isEmpty()) && !game.getFirst().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
                detailDTO.status = "2";
            }

            if (game.getChallenges().size() != 5 && challenge.getFirstScore() != null && !challenge.getFirstScore().isEmpty() && (challenge.getSecondScore() == null || challenge.getSecondScore().isEmpty()) && (game.getSecond() != null) && game.getSecond().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin())) {
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
        }    if (game.getChallenges().size() == 2 && (detailDTO.status == null || detailDTO.status.isEmpty())) {
            if ((game.getSecond() != null) && game.getSecond().getLogin().equalsIgnoreCase(SecurityUtils.getCurrentUserLogin()))
                detailDTO.status = "2";
            else {
                detailDTO.status = "1";

            }
        }
        if (game.getChallenges().size() == 4 && (detailDTO.status == null || detailDTO.status.isEmpty())) {
            detailDTO.status = "3";
            AbstractGame abstractGame = thirdGame(game.getId());
            detailDTO.url = abstractGame.getUrl();
            List<Challenge> challengeList = game.getChallenges();
            Challenge challenge = new Challenge();
            challenge.setIcon(abstractGame.getIcon());
            challenge.setName(abstractGame.getName());
            challenge.setUrl(abstractGame.getUrl());
            challenge.setAbstractId(abstractGame.getId());
            challengeRepository.save(challenge);
            challengeList.add(challenge);
            gameRepository.save(game);

        }
        if (game.getChallenges().size() == 5 && (detailDTO.status == null || detailDTO.status.isEmpty())) {
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
        List<AbstractGame> longSet = new ArrayList<>(abstractGames);
        for (AbstractGame abstractGame : longSet) {
            game.getChallenges().stream().filter(game1 -> game1.getName().equalsIgnoreCase(abstractGame.getName())).forEach(game1 -> abstractGames.remove(abstractGame));
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
