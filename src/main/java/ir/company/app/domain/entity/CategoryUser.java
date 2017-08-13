package ir.company.app.domain.entity;

import javax.persistence.*;

/**
 * Created by farzad on 8/1/17.
 */
@Entity
@Table(name = "GameCategory")
public class CategoryUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private GameCategory gameCategory;

    @Column(name = "win", nullable = true)
    private int win = 0;
    @Column(name = "lose", nullable = true)
    private int lose = 0;
    @Column(name = "draw", nullable = true)
    private int draw = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GameCategory getGameCategory() {
        return gameCategory;
    }

    public void setGameCategory(GameCategory gameCategory) {
        this.gameCategory = gameCategory;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }
}
