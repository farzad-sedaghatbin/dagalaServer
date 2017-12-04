package ir.company.app.domain.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by farzad on 7/18/17.
 */
@Entity
@DynamicUpdate
@Table(name = "Challenge")
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "abstractId")
    private Long abstractId;

    @Column(name = "icon")
    private String icon;

    @Column(name = "firstscore")
    private String firstScore;

    @Column(name = "secondscore")
    private String secondScore;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFirstScore() {
        return firstScore;
    }

    public void setFirstScore(String firstScore) {
        this.firstScore = firstScore;
    }

    public String getSecondScore() {
        return secondScore;
    }

    public void setSecondScore(String secondScore) {
        this.secondScore = secondScore;
    }

    public Long getAbstractId() {
        return abstractId;
    }

    public void setAbstractId(Long abstractId) {
        this.abstractId = abstractId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
