package com.ryanair.flights.services.interfaces;

import com.ryanair.flights.model.FlightConnection;
import com.ryanair.flights.model.FlightSearchAttributes;
import com.ryanair.flights.model.Leg;

import java.util.List;

public interface FlightFinderService {
    List<FlightConnection> findFlights(Leg destination, FlightSearchAttributes attrs);
}
