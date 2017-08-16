package ir.company.app.domain.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by farzad on 8/1/17.
 */
@Entity
@DynamicUpdate
@Table(name = "tb_Record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private User user;
    @OneToOne
    private AbstractGame abstractGame;
    private Long score;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }


    public void setAbstractGame(AbstractGame abstractGame) {
        this.abstractGame = abstractGame;
    }

    public AbstractGame getAbstractGame() {
        return abstractGame;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Long getScore() {
        return score;
    }
}
