package ir.company.app.service.dto;

import ir.company.app.domain.entity.Challenge;

import java.util.List;

/**
 * Created by farzad on 8/4/2017.
 */
public class GameRedisDTO {

    public String first;
    public String firstAvatar;
    public String secondAvatar;
    public String second;
    public Long gameId;
    public List<Challenge> challengeList;


}
