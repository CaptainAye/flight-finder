package com.ryanair.flights.services.interfaces;

import com.ryanair.flights.model.ApiSchedule;
import com.ryanair.flights.model.Leg;
import com.ryanair.flights.model.Route;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public interface ScheduleService {
    ApiSchedule getSchedule(String departureAirport, String arrivalAirport, YearMonth date);
    List<Leg> getRouteFlights(Route route, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime);
}
