package com.ryanair.flights.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

public class FlightConnection {

    private final int stops;

    @NotNull
    @NotEmpty
    private final List<Leg> legs;

    public FlightConnection(List<Leg> legs) {
        this.legs = Collections.unmodifiableList(legs);
        this.stops = this.legs.size() -1;
        validateArguments();
    }

    private void validateArguments() {
        if (legs == null || legs.isEmpty()) {
            throw new IllegalArgumentException("FlightConnection legs list cannot be empty");
        }
    }

    public int getStops() {
        return stops;
    }

    public List<Leg> getLegs() {
        return legs;
    }
}
