package ir.company.app.domain.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by farzad on 8/1/17.
 */
@Entity
@DynamicUpdate
@Table(name = "tb_ErrorLog")
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private User user;
    private String log;

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

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


}
