package com.ryanair.flights.services.interfaces;

import com.ryanair.flights.model.Schedule;

import java.time.YearMonth;
import java.util.List;

public interface ScheduleService {
    List<Schedule> getSchedules(String departureAirport, String arrivalAirport, YearMonth startingDate, YearMonth endingDate);
}
