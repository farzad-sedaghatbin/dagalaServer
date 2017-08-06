package ir.company.app.service.dto;

import org.springframework.web.socket.WebSocketSession;

/**
 * Created by farzad on 12/8/16.
 */
public class CarsDTO {
    long id;
    double lat;
    double longitude;
    WebSocketSession webSocketSession;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }
}
