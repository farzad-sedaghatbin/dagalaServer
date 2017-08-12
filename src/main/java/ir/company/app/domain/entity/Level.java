package ir.company.app.domain.entity;

import javax.persistence.*;

/**
 * Created by farzad on 8/1/17.
 */
@Entity
@Table(name = "Level")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private long threshold;
    private int level;


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getThreshold() {
        return threshold;
    }

    public void setThreshold(long threshold) {
        this.threshold = threshold;
    }
}
