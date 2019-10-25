package com.ryanair.flights.services.decorators;

import com.ryanair.flights.model.Route;
import com.ryanair.flights.services.decorators.RouteServiceDecorator;
import com.ryanair.flights.services.interfaces.RouteService;

import java.util.function.Predicate;


public class RyanairOperatorFilterDecorator extends RouteServiceDecorator {

    private static final String RYANAIR_OPERATOR = "RYANAIR";

    public RyanairOperatorFilterDecorator(RouteService routeService) {
        super(routeService);
    }

    @Override
    protected Predicate<Route> getFilter() {
        return route -> route.getOperator().equalsIgnoreCase(RYANAIR_OPERATOR);
    }
}
