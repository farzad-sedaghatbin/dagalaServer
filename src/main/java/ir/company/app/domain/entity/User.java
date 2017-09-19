package ir.company.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.company.app.domain.AbstractAuditingEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * A user.
 */
@Entity
@Table(name = "jhi_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(length = 100, unique = true, nullable = false)
    private String login;

    @JsonIgnore
    @NotNull
    @Column(name = "password_hash", length = 60)
    private String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 100, unique = true)
    private String guestId;

    @NotNull
    @Column(nullable = false)
    private boolean activated = false;

    @Size(min = 2, max = 5)
    @Column(name = "lang_key", length = 5)
    private String langKey;

    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    private String resetKey;

    @Column(name = "reset_date", nullable = true)
    private ZonedDateTime resetDate = null;

    @Column(name = "rating", nullable = true)
    private int rating = 0;

    @Column(name = "mobile", nullable = true)
    private String mobile;

    @Column(name = "score", nullable = true)
    private long score = 0;

    @Column(name = "coin", nullable = true)
    private int coin = 300;


    @Column(name = "avatar", nullable = true)
    private String avatar;

    @Column(name = "gem", nullable = true)
    private int gem = 0;

    @Column(name = "level", nullable = true)
    private int level = 0;

    @Size(max = 50)
    @Column(name = "push_session_key", length = 50)

    private String pushSessionKey;

    @Column(name = "gender", nullable = true)

    private Boolean gender;


    @Column(name = "win", nullable = true)
    private int win = 0;
    @Column(name = "lose", nullable = true)
    private int lose = 0;

    @Column(name = "draw", nullable = true)
    private int draw = 0;


    @Column(name = "win_in_row", nullable = true)
    private int winInRow = 0;

    @Column(name = "max_win_in_row", nullable = true)
    private int maxWinInRow = 0;

    private ZonedDateTime lastRoulette;

    @ManyToMany(fetch = FetchType.EAGER)
    private  List<League> leagues;


//    @JsonIgnore
//    @JoinTable(
//        name = "jhi_user_authority",
//        joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
//        inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
//    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//
//    private List<Authority> authorities = new ArrayList<>();


    public List<League> getLeagues() {
        return leagues;
    }

    public void setLeagues(List<League> leagues) {
        this.leagues = leagues;
    }

    public int getMaxWinInRow() {
        return maxWinInRow;
    }

    public void setMaxWinInRow(int maxWinInRow) {
        this.maxWinInRow = maxWinInRow;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public ZonedDateTime getResetDate() {
        return resetDate;
    }

    public void setResetDate(ZonedDateTime resetDate) {
        this.resetDate = resetDate;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGem() {
        return gem;
    }

    public void setGem(int gem) {
        this.gem = gem;
    }

    public String getPushSessionKey() {
        return pushSessionKey;
    }

    public void setPushSessionKey(String pushSessionKey) {
        this.pushSessionKey = pushSessionKey;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getScore() {
        return score;
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

    public int getWinInRow() {
        return winInRow;
    }

    public void setWinInRow(int winInRow) {
        this.winInRow = winInRow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (!login.equals(user.login)) {
            return false;
        }

        return true;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", activated='" + activated + '\'' +
            ", langKey='" + langKey + '\'' +
            ", activationKey='" + activationKey + '\'' +
            "}";
    }

    public ZonedDateTime getLastRoulette() {
        return lastRoulette;
    }

    public void setLastRoulette(ZonedDateTime lastRoulette) {
        this.lastRoulette = lastRoulette;
    }
}
