package ir.company.app.service.migmig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by farzad on 9/16/17.
 */
public class LeagueDTO {

    public int capacity;
    public int left;
    public int size;
    public String cost;
    public String startDate;
    public Long timeLeft;
    public Long id;
    public int status;
public List<Prize> prizes = new ArrayList<>();

    public int index;
    public String name;
    public int minLevel;

    public static class Prize {
        public Prize(int index, String desc) {
            this.index = index;
            this.desc = desc;
        }

        public  int index;
        public  String desc;
    }
}
