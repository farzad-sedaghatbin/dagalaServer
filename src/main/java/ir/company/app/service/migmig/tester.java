package ir.company.app.service.migmig;

import ir.company.app.domain.entity.Game;
import ir.company.app.domain.entity.GameStatus;
import ir.company.app.domain.entity.LeagueUser;
import ir.company.app.domain.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by farzad on 12/6/2017.
 */
public class tester {


    public static void main(String[] arg) {
        List<LeagueUser> leagueUsers = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            User u = new User();
            u.setFirstName(String.valueOf(i));
            LeagueUser l = new LeagueUser();
            l.setUser(u);
            leagueUsers.add(l);
        }
        List<Game> games = new ArrayList<>();

        games.forEach(game -> System.out.println(game.getFirst().getFirstName() + " : " + game.getSecond().getFirstName()));
    }
}
