package com.ryanair.flights.services.interfaces;

import com.ryanair.flights.model.Leg;
import com.ryanair.flights.model.Route;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleFlightsService {
    List<Leg> getScheduleFlights(Route route, LocalDateTime departureDateTime,
                                 LocalDateTime arrivalDateTime);
}
