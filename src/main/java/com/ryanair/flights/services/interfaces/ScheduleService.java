package com.ryanair.flights.services.interfaces;

import com.ryanair.flights.model.ApiSchedule;
import com.ryanair.flights.model.FlightInfo;

import java.time.YearMonth;
import java.util.List;

public interface ScheduleService {
    ApiSchedule getSchedule(String departureAirport, String arrivalAirport, YearMonth date);

    List<FlightInfo> getScheduleFlights(String departureAirport, String arrivalAirport,
                                        YearMonth date);
}
