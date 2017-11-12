package ir.company.app.service.dto;

import java.util.List;

/**
 * Created by farzad on 12/10/16.
 */
public class HomeDTO {
    public int coins;
    public int level;
    public boolean newLevel = false;
    public boolean guest = false;
    public int gem;
    public long score;
    public String avatar;
    public String user;
    public int rating;
    public Long userid;
    public String token;
    public int perGameCoins;
    public List<GameLowDTO> friendly;
    public List<GameLowDTO> halfGame;
    public List<GameLowDTO> fullGame;


    public long exp;
}
