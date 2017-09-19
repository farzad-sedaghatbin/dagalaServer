package ir.company.app.domain.entity;


import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by farzad on 8/1/17.
 */
@Entity
@Table(name = "tb_league")
public class League {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int capacity=0;
    private int fill=0;
    private int cost=0;
    private StatusEnum status;
    private ZonedDateTime startDate;
    @ManyToMany(fetch = FetchType.EAGER)
    List<User> userList;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getFill() {
        return fill;
    }

    public void setFill(int fill) {
        this.fill = fill;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
