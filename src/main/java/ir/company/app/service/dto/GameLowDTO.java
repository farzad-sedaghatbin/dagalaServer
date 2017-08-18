package ir.company.app.service.dto;

/**
 * Created by farzad on 8/4/2017.
 */
public class GameLowDTO {


    public String status;
    public String scoreStatus;
    public String score;
    public Long gameId;

    public User first;
    public User second;

    public static class User {
        public String user;
        public String avatar;
    }
}
