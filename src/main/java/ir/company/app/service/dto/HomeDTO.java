package ir.company.app.service.dto;

import ir.company.app.service.migmig.MenuDTO;

import java.util.List;

/**
 * Created by farzad on 12/10/16.
 */
public class HomeDTO {
    public int coins;
    public int level;
    public boolean newLevel=false;
    public int gem;
    public long score;
    public String avatar;
    public int rating;
    public Long userid;
    public String token;
    public int perGameCoins;
    public List<GameLowDTO> halfGame;
    public List<GameLowDTO> fullGame;
    public List<MenuDTO> category;


}
