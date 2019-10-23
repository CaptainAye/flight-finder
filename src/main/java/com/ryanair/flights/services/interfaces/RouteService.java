package com.ryanair.flights.services.interfaces;

import com.ryanair.flights.model.Route;
import com.ryanair.flights.model.RouteConnection;

import java.util.List;

public interface RouteService {
    List<Route> getRoutes();
}
