package com.ryanair.flights.model;

public class Route {
    private final String airportFrom;
    private final String airportTo;
    private final String connectingAirport;
    private final boolean newRoute;
    private final boolean seasonalRoute;
    private final String operator;
    private final String group;

    public Route(String airportFrom, String airportTo, String connectingAirport, boolean newRoute, boolean seasonalRoute, String operator, String group) {
        this.airportFrom = airportFrom;
        this.airportTo = airportTo;
        this.connectingAirport = connectingAirport;
        this.newRoute = newRoute;
        this.seasonalRoute = seasonalRoute;
        this.operator = operator;
        this.group = group;
    }

    public String getAirportFrom() {
        return airportFrom;
    }

    public String getAirportTo() {
        return airportTo;
    }

    public String getConnectingAirport() {
        return connectingAirport;
    }

    public boolean isNewRoute() {
        return newRoute;
    }

    public boolean isSeasonalRoute() {
        return seasonalRoute;
    }

    public String getOperator() {
        return operator;
    }

    public String getGroup() {
        return group;
    }
}
