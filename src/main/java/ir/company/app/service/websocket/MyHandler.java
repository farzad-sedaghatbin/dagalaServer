package ir.company.app.service.websocket;

import ir.company.app.config.Constants;
import ir.company.app.service.dto.*;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MyHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String s = message.getPayload();
        String[] splited = s.split(",");

        System.out.println(s);
//        double lat=Long.valueOf(splited[1]);
//        double lg=Long.valueOf(splited[2]);
        if (splited[0].equalsIgnoreCase("join")) {

            DriverInfoDTO driverInfoDTO = new DriverInfoDTO();
            driverInfoDTO.mobile = "09121224556";
            driverInfoDTO.name = "امید جواهری";
            driverInfoDTO.source ="تهران";
            driverInfoDTO.destination = "قزوین";
            driverInfoDTO.number = "77ی122 ایران 10";
            driverInfoDTO.carDetail = "پژو 206 سفید";
            DriverInfoObjectValue driverInfoObjectValue= new DriverInfoObjectValue();
            driverInfoObjectValue.command = "activeTrip";
            driverInfoObjectValue.driverInfoDTO = driverInfoDTO;
            try {
            String f = new ObjectMapper().writeValueAsString(driverInfoObjectValue);

                session.sendMessage(new TextMessage(f.getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Constants.userHashMap.put(Long.valueOf(splited[1]), session);
        } else if (splited[0].equalsIgnoreCase("aroundme")) {
            List<AroundmeDTO> aroundmeDTOList = new ArrayList<>();
            for (Map.Entry<Long, CarsDTO> longCarsDTOEntry : Constants.carsDTOHashMap.entrySet()) {

                AroundmeDTO aroundmeDTO = new AroundmeDTO();
                aroundmeDTO.setLat(Constants.carsDTOHashMap.get(longCarsDTOEntry.getKey()).getLat());
                aroundmeDTO.setLongitude(Constants.carsDTOHashMap.get(longCarsDTOEntry.getKey()).getLongitude());
                aroundmeDTOList.add(aroundmeDTO);
            }
            try {
                AroundMeObjectValue aroundMeObjectValue = new AroundMeObjectValue();
                aroundMeObjectValue.aroundmeDTOs = aroundmeDTOList;
                aroundMeObjectValue.command = "aroundme";
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsBytes(aroundMeObjectValue)));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
//        Constants.carsDTOHashMap.entrySet().stream().filter(map->userService.distance(map.getValue().getLat(),map
//        .getValue().getLongitude(),lat,lg)>1)

    }

}
