package com.ryanair.flights.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FlightConnection {

    @NotNull
    @NotEmpty
    private final List<Leg> legs;

    public FlightConnection(List<Leg> legs) {
        this.legs = new ArrayList<>(legs);
        validateArguments();
    }

    private void validateArguments() {
        if (legs == null || legs.isEmpty()) {
            throw new IllegalArgumentException("FlightConnection legs list cannot be empty");
        }
    }

    public int getStops() {
        return legs.size() - 1;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void addFlight(int flightPosition, Leg flight) {
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
