package com.ryanair.flights.model;

import java.time.Month;
import java.util.Collections;
import java.util.List;

public class ApiSchedule {
    private final Month month;
    private final List<FlightDay> days;

    public ApiSchedule(Month month, List<FlightDay> days) {
        this.month = month;
        this.days = Collections.unmodifiableList(days);
    }

    public Month getMonth() {
        return month;
    }

    public List<FlightDay> getDays() {
        return days;
    }
}
