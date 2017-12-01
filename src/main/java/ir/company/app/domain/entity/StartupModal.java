package ir.company.app.domain.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A StartupModal.
 */
@Entity
@Table(name = "startup_modal")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StartupModal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "version")
    private String version = "4.0.0";

    @Column(name = "index")
    private Integer index;

    public StartupModal(String version) {
        this.version = version;
    }

    public StartupModal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StartupModal startupModal = (StartupModal) o;
        if (startupModal.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, startupModal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "StartupModal{" +
            "id=" + id +
            ", url='" + content + "'" +
            ", index='" + index + "'" +
            '}';
    }
}
