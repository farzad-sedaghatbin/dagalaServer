package ir.company.app.domain.entity;

import javax.persistence.*;

/**
 * Created by farzad on 8/1/17.
 */
@Entity
@Table(name = "TB_POLICY")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private EPolicy ePolicy;
    private String name;
    private int value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EPolicy getePolicy() {
        return ePolicy;
    }

    public void setePolicy(EPolicy ePolicy) {
        this.ePolicy = ePolicy;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
