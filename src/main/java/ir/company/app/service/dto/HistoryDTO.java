package ir.company.app.service.dto;

import ir.company.app.domain.entity.Trip;

import java.util.List;

/**
 * Created by farzad on 12/8/16.
 */
public class HistoryDTO {
    List<Trip> completedTrips;
    List<Trip> reservedTrips;
    List<Trip> inProgressTrips;

    public List<Trip> getCompletedTrips() {
        return completedTrips;
    }

    public void setCompletedTrips(List<Trip> completedTrips) {
        this.completedTrips = completedTrips;
    }

    public List<Trip> getReservedTrips() {
        return reservedTrips;
    }

    public void setReservedTrips(List<Trip> reservedTrips) {
        this.reservedTrips = reservedTrips;
    }

    public List<Trip> getInProgressTrips() {
        return inProgressTrips;
    }

    public void setInProgressTrips(List<Trip> inProgressTrips) {
        this.inProgressTrips = inProgressTrips;
    }
}
