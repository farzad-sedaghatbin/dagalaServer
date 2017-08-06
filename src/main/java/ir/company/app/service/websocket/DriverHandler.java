package ir.company.app.service.websocket;

import ir.company.app.config.Constants;
import ir.company.app.service.dto.*;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DriverHandler extends TextWebSocketHandler {
    private static WebSocketSession omid;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String s = message.getPayload();
        String[] splited = s.split(",");
        long id = Long.valueOf(splited[1]);
        if (splited[0].equalsIgnoreCase("start")) {
            if (Constants.carsDTOHashMap.get(id) != null) {
                CarsDTO carsDTO = Constants.carsDTOHashMap.get(id);
//                carsDTO.setLat(Double.valueOf(splited[2]));
//                carsDTO.setLongitude(Double.valueOf(splited[3]));
                carsDTO.setWebSocketSession(session);
                Constants.carsDTOHashMap.put(id, carsDTO);
            } else {
                CarsDTO carsDTO = new CarsDTO();
//                carsDTO.setLat(Double.valueOf(splited[2]));
//                carsDTO.setLongitude(Double.valueOf(splited[3]));
                carsDTO.setId(id);
                carsDTO.setWebSocketSession(session);
                Constants.carsDTOHashMap.put(id, carsDTO);


            }

            List<TripInfo> tripInfos = new ArrayList<>();
            TripInfo tripInfo = new TripInfo();
            tripInfo.name = "فرزاد";
            tripInfo.uid = "1212ADSDD";
            tripInfo.cost = 1000;
            tripInfo.family = "صداقت بین";
            tripInfo.mobile = "09128626242";
            tripInfos.add(tripInfo);
            TripInfoObjectValue tripInfoObjectValue = new TripInfoObjectValue();
            tripInfoObjectValue.tripInfos = tripInfos;
            tripInfoObjectValue.command = "requests";
//            //todo change ba selected algorithmm

            try {
                String f = new ObjectMapper().writeValueAsString(tripInfoObjectValue);
                session.sendMessage(new TextMessage(f.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (splited[0].equalsIgnoreCase("delivery")) {
            if (Constants.carsDTOHashMap.get(id) != null) {
                CarsDTO carsDTO = Constants.carsDTOHashMap.get(id);
                carsDTO.setLat(Double.valueOf(splited[2]));
                carsDTO.setLongitude(Double.valueOf(splited[3]));
                carsDTO.setWebSocketSession(session);
                Constants.carsDTOHashMap.put(id, carsDTO);
                try {
                    DeliveryLocationDTO deliveryLocationDTO = new DeliveryLocationDTO();
                    deliveryLocationDTO.lat = String.valueOf(carsDTO.getLat());
                    deliveryLocationDTO.lng = String.valueOf(carsDTO.getLongitude());
                    DeliveryLocationObjectValue deliveryLocationObjectValue = new DeliveryLocationObjectValue();
                    deliveryLocationObjectValue.command = "delivery";
                    deliveryLocationObjectValue.deliveryLocationDTO = deliveryLocationDTO;
                    Constants.userHashMap.get(Long.valueOf(splited[4])).sendMessage(new TextMessage(new ObjectMapper().writeValueAsBytes(deliveryLocationObjectValue)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
