package com.ryanair.flights.services.decorators;

import com.ryanair.flights.model.Route;
import com.ryanair.flights.services.interfaces.RouteService;

import java.util.List;
import java.util.function.Predicate;

import static com.ryanair.flights.utils.ListHelper.filterList;
import static com.ryanair.flights.utils.ListHelper.getListOrEmpty;

public abstract class RouteServiceDecorator implements RouteService {

    private RouteService routeService;

    public RouteServiceDecorator(RouteService routeService) {
        this.routeService = routeService;
    }

    @Override
    public List<Route> getRoutes() {
        return getListOrEmpty(filterList(routeService.getRoutes(), getFilter()));
    }

    protected abstract Predicate<Route> getFilter();
}
