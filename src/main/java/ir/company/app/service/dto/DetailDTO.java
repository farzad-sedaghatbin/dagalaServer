package ir.company.app.service.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by farzad on 12/8/16.
 */
public class DetailDTO {
    //    public String distance;
    public String status;
    public int score;
    public String url;
    public Long timeLeft=null;
    public long gameId;

    public List<GameDTO> gameDTOS = new ArrayList<>();
    public DetailDTO.User user;

    public static class User {
        public String user;
        public String avatar;
        public int score;

    }

    public static class GameDTO {
        public String myScore;
        public String secondScore;
        public String icon;
        public Long challengeId;

    }
}
