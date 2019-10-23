package com.ryanair.flights.model;

import java.util.Collections;
import java.util.List;

public class RouteConnection {
    private final List<Route> routes;

    public RouteConnection(List<Route> routes) {
        this.routes = Collections.unmodifiableList(routes);
    }

    public List<Route> getRoutes() {
        return routes;
    }
}
