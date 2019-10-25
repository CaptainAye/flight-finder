package com.ryanair.flights.services.decorators;

import com.ryanair.flights.model.Route;
import com.ryanair.flights.services.decorators.RouteServiceDecorator;
import com.ryanair.flights.services.interfaces.RouteService;

import java.util.function.Predicate;

public class NoConnectingAirportFilterDecorator extends RouteServiceDecorator {

    public NoConnectingAirportFilterDecorator(RouteService routeService) {
        super(routeService);
    }

    protected Predicate<Route> getFilter() {
        return route -> route.getConnectingAirport() == null;
    }
}
