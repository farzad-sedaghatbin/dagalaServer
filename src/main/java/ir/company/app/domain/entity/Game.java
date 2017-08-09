package ir.company.app.domain.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by farzad on 7/18/17.
 */
@Entity
@Table(name = "Game")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    User first;
    @OneToOne
    User second;
    @Enumerated
    GameStatus gameStatus;
    @OneToMany(fetch = FetchType.EAGER)
    List<Challenge> challenges;
    private ZonedDateTime dateTime;
    private int winner = -1;
    private int firstScore = 0;
    private int SecondScore = 0;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFirst() {
        return first;
    }

    public void setFirst(User first) {
        this.first = first;
    }

    public User getSecond() {
        return second;
    }

    public void setSecond(User second) {
        this.second = second;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getFirstScore() {
        return firstScore;
    }

    public void setFirstScore(int firstScore) {
        this.firstScore = firstScore;
    }

    public int getSecondScore() {
        return SecondScore;
    }

    public void setSecondScore(int secondScore) {
        SecondScore = secondScore;
    }
}
