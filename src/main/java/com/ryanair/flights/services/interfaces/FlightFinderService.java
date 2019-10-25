package com.ryanair.flights.services.interfaces;

import com.ryanair.flights.model.FlightConnection;
import com.ryanair.flights.model.SearchCriteria;
import com.ryanair.flights.model.FlightInfo;

import java.util.List;

public interface FlightFinderService {
    List<FlightConnection> findFlights(FlightInfo destination, SearchCriteria attrs);
}
