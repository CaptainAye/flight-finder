package com.ryanair.flights.services.interfaces;

import com.ryanair.flights.model.FlightInfo;
import java.util.List;

public interface ScheduleFlightsService {
    List<FlightInfo> getScheduleFlights(FlightInfo flightInfo);
}
