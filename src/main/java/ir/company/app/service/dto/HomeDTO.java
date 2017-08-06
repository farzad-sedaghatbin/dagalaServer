package ir.company.app.service.dto;

import java.util.List;

/**
 * Created by farzad on 12/10/16.
 */
public class HomeDTO {
    public int coins;
    public int level;
    public int gem;
    public long score;
    public String avatar;
    public int rating;
    public Long userid;
    public List<GameLowDTO> halfGame;
    public List<GameLowDTO> fullGame;
}
