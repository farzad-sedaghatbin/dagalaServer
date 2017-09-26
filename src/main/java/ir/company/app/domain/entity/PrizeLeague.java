package ir.company.app.domain.entity;

import javax.persistence.*;

/**
 * Created by farzad on 9/23/17.
 */
@Entity
@Table(name = "tb_PrizeLeague")
public class PrizeLeague {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int index;
    private String description;
    @ManyToOne
    private  League league;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
