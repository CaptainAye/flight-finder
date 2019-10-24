package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.Route;
import com.ryanair.flights.services.interfaces.RouteService;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ryanair.flights.utils.ListHelper.*;

public class RyanairOperatorFilterDecorator extends RouteServiceDecorator {

    private static final String RYANAIR_OPERATOR = "RYANAIR";

    public RyanairOperatorFilterDecorator(RouteService routeService) {
        super(routeService);
    }

    private Predicate<Route> getOnlyRyanairOperator() {
        return route -> route.getOperator().equalsIgnoreCase(RYANAIR_OPERATOR);
    }

    @Override
    public List<Route> getRoutes() {
        return getListOrEmpty(routeService.getRoutes()).stream().filter(getOnlyRyanairOperator()).collect(Collectors.toList());
    }
}
