package com.ryanair.flights.model;

import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.List;

public class FlightDay {
    private static final int MIN_DAY = 1;
    private static final int MAX_DAY = 31;

    private final int day;
    private final List<Flight> flights;

    public FlightDay(int day, List<Flight> flights) {
        this.day = day;
        this.flights = Collections.unmodifiableList(flights);
        validateArguments();
    }

    private void validateArguments() {
        ChronoField.DAY_OF_MONTH.checkValidIntValue(this.day);
        boolean isFlightsValid = flights != null && !flights.isEmpty();
        if (!isFlightsValid) {
            throw new IllegalArgumentException("FlightDay flight list not valid");
        }
    }

    public int getDay() {
        return day;
    }

    public List<Flight> getFlights() {
        return flights;
    }
}
