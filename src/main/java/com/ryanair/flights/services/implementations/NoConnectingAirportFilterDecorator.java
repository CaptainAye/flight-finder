package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.Route;
import com.ryanair.flights.services.interfaces.RouteService;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ryanair.flights.utils.ListHelper.*;

public class NoConnectingAirportFilterDecorator extends RouteServiceDecorator {

    public NoConnectingAirportFilterDecorator(RouteService routeService) {
        super(routeService);
    }

    @Override
    public List<Route> getRoutes() {
        return getListOrEmpty(routeService.getRoutes()).stream().filter(getOnlyNoConnectingAirport()).collect(Collectors.toList());
    }

    private Predicate<Route> getOnlyNoConnectingAirport() {
        return route -> route.getConnectingAirport() == null;
    }
}
