package com.ryanair.flights.model;

import java.time.Duration;

public class FlightSearchAttributes {
    private final Duration minimalTransferTime;
    private int maxStops;

    public FlightSearchAttributes(int maxStops, Duration minimalTransferTime) {
        this.maxStops = maxStops;
        this.minimalTransferTime = minimalTransferTime;
        validateInputArguments();
    }

    private void validateInputArguments() {
        if (maxStops < 0) {
            throw new IllegalArgumentException("maxStops cannot be lesser than 0");
        }
        if (minimalTransferTime == null) {
            throw new IllegalArgumentException("minimalTransferTime cannot be null");
        }
    }

    public int getMaxStops() {
        return maxStops;
    }

    public void decrementMaxStops() {
        if (maxStops > 0) {
            --maxStops;
        }
    }

    public Duration getMinimalTransferTime() {
        return minimalTransferTime;
    }
}
