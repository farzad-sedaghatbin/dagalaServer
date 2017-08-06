package ir.company.app.service.dto;

import ir.company.app.config.Constants;
import ir.company.app.domain.entity.Trip;
import ir.company.app.service.util.RedisUtil;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static ir.company.app.service.migmig.BusinessService.distFrom;

/**
 * Created by farzad on 12/8/16.
 */
public class ReservedDTO extends TimerTask {
    Trip trip;

    public ReservedDTO(Trip trip) {
        this.trip = trip;
    }

    @Override
    public void run() {
        TripInfo tripInfo = new TripInfo();
        tripInfo.cost = trip.getCost();
        tripInfo.uid = trip.getUID();
        tripInfo.source = trip.getSource();
        tripInfo.destination = trip.getDestination();
//        try {


            TripInfoObjectValue tripInfoObjectValue = new TripInfoObjectValue();
            tripInfoObjectValue.tripInfo = tripInfo;
            tripInfoObjectValue.command = "request";
//            List<CarsDTO> carsDTOs = Constants.carsDTOHashMap.entrySet().parallelStream().filter(e -> distFrom(Double.valueOf(trip.getSlat()), Double.valueOf(trip.getSlng()), e.getValue().getLat(), e.getValue().getLongitude()) < 1).map(map -> map.getValue()).collect(Collectors.toList());
//            Constants.carsDTOHashMap.get(carsDTOs.get(0)).getWebSocketSession().sendMessage(new TextMessage(new ObjectMapper().writeValueAsBytes(tripInfoObjectValue)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
