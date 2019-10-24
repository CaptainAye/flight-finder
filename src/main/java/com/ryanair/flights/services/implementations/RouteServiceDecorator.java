package com.ryanair.flights.services.implementations;

import com.ryanair.flights.services.interfaces.RouteService;

public abstract class RouteServiceDecorator implements RouteService {

    protected RouteService routeService;

    public RouteServiceDecorator(RouteService routeService) {
        this.routeService = routeService;
    }
}
