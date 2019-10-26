package com.ryanair.flights.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FlightConnection {

    private final List<FlightInfo> legs;

    public FlightConnection(@NotNull @NotEmpty List<FlightInfo> legs) {
        this.legs = new ArrayList<>(legs);
    }

    public int getStops() {
        return legs.size() - 1;
    }

    public List<FlightInfo> getLegs() {
        return legs;
    }

    public void addFlight(int flightPosition, FlightInfo flight) {
        legs.add(flightPosition, flight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightConnection that = (FlightConnection) o;
        return Objects.equals(legs, that.legs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(legs);
    }

    @Override
    public String toString() {
        return "FlightConnection{" +
                "legs=" + legs +
                "}\n";
    }
}
