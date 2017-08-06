package ir.company.app.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.company.app.domain.enumoration.TripInfoStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by farzad on 12/8/16.
 */
public class TripInfo {
    public long cost;
    //    public String distance;
    public String uid;
    public String day;
    public String month;
    public String year;
    public String time;
    public String source;
    public String destination;
    public String name;
    public String family;
    public String description;
    public boolean approved;
    public String mobile;
}
