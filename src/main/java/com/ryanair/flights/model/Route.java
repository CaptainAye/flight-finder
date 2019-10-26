package com.ryanair.flights.model;

import java.util.Objects;

public class Route {
    private IataCode airportFrom;
    private IataCode airportTo;
    private String connectingAirport;
    private boolean newRoute;
    private boolean seasonalRoute;
    private String operator;
    private String group;

    public Route() {
    }
    public Route(String airportFrom, String airportTo, String connectingAirport, boolean newRoute, boolean seasonalRoute, String operator, String group) {
        this.airportFrom = IataCode.of(airportFrom);
        this.airportTo = IataCode.of(airportTo);
        this.connectingAirport = connectingAirport;
        this.newRoute = newRoute;
        this.seasonalRoute = seasonalRoute;
        this.operator = operator;
        this.group = group;
    }

    public String getAirportFrom() {
        return airportFrom.getIataCode();
    }

    public void setAirportFrom(String airportFrom) {
        this.airportFrom = IataCode.of(airportFrom);
    }

    public String getAirportTo() {
        return airportTo.getIataCode();
    }

    public void setAirportTo(String airportTo) {
        this.airportTo = IataCode.of(airportTo);
    }

    public String getConnectingAirport() {
        return connectingAirport;
    }

    public void setConnectingAirport(String connectingAirport) {
        this.connectingAirport = connectingAirport;
    }

    public boolean isNewRoute() {
        return newRoute;
    }

    public void setNewRoute(boolean newRoute) {
        this.newRoute = newRoute;
    }

    public boolean isSeasonalRoute() {
        return seasonalRoute;
    }

    public void setSeasonalRoute(boolean seasonalRoute) {
        this.seasonalRoute = seasonalRoute;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return newRoute == route.newRoute &&
                seasonalRoute == route.seasonalRoute &&
                Objects.equals(airportFrom, route.airportFrom) &&
                Objects.equals(airportTo, route.airportTo) &&
                Objects.equals(connectingAirport, route.connectingAirport) &&
                Objects.equals(operator, route.operator) &&
                Objects.equals(group, route.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(airportFrom, airportTo, connectingAirport, newRoute, seasonalRoute, operator, group);
    }

    @Override
    public String toString() {
        return "Route{" +
                "airportFrom='" + airportFrom + '\'' +
                ", airportTo='" + airportTo + '\'' +
                ", connectingAirport='" + connectingAirport + '\'' +
                ", newRoute=" + newRoute +
                ", seasonalRoute=" + seasonalRoute +
                ", operator='" + operator + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
