package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.FlightConnection;
import com.ryanair.flights.model.FlightSearchAttributes;
import com.ryanair.flights.model.Leg;
import com.ryanair.flights.model.Route;
import com.ryanair.flights.services.interfaces.FlightFinderService;
import com.ryanair.flights.services.interfaces.RouteService;
import com.ryanair.flights.services.interfaces.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class RecursiveFlightFinderService implements FlightFinderService {

    private final ScheduleService scheduleService;
    private final RouteService routeService;

    public RecursiveFlightFinderService(@Autowired ScheduleService scheduleService,
                                        @Autowired RouteService routeService) {
        this.scheduleService = scheduleService;
        this.routeService = routeService;
    }

    @Override
    public List<FlightConnection> findFlights(Leg destination, FlightSearchAttributes attrs) {
        validateInputArguments(destination, attrs);
        List<Route> routes = getRoutes();
        return findFlights(destination, attrs, routes);
    }

    private void validateInputArguments(Leg destination, FlightSearchAttributes attributes) {

        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null");
        }

        if (attributes == null) {
            throw new IllegalArgumentException("Attributes cannot be null");
        }
    }

    private List<Route> getRoutes() {
        return getListOrEmpty(routeService.getRoutes());
    }

    private <T> List<T> getListOrEmpty(List<T> list) {
        return list == null ? Collections.emptyList() : list;

    }

    private List<FlightConnection> findFlights(Leg destination, FlightSearchAttributes attributes,
                                               List<Route> routes) {
        String departureAirport = destination.getDepartureAirport();
        List<FlightConnection> flightConnections = new ArrayList<>();
        List<Route> departureRoutes = filterRouteDepartureAirport(routes, departureAirport);

        for (Route route : departureRoutes) {
            flightConnections.addAll(findFlights(route, destination, attributes, routes));
        }
        return flightConnections;
    }

    private List<FlightConnection> findFlights(Route route, Leg destination,
                                               FlightSearchAttributes attributes,
                                               List<Route> allRoutes) {
        LocalDateTime departure = destination.getDepartureDateTime();
        LocalDateTime arrival = destination.getArrivalDateTime();
        List<Leg> scheduledFlights = getRouteFlights(route, departure, arrival);
        return isDirectRoute(route, destination.getArrivalAirport()) ?
                findDirectFlights(scheduledFlights) :
                findTransferFlights(scheduledFlights, destination, attributes, allRoutes);
    }

    private List<Leg> getRouteFlights(Route route, LocalDateTime departure, LocalDateTime arrival) {
        return getListOrEmpty(scheduleService.getRouteFlights(route, departure, arrival));
    }

    private List<FlightConnection> findDirectFlights(List<Leg> routeFlights) {
        Function<Leg, FlightConnection> flightConnectionMapper =
                flight -> new FlightConnection(Collections.singletonList(flight));
        return routeFlights.stream().map(flightConnectionMapper).collect(Collectors.toList());
    }

    private List<FlightConnection> findTransferFlights(List<Leg> routeFlights, Leg destination,
                                                       FlightSearchAttributes attributes,
                                                       List<Route> allRoutes) {
        List<FlightConnection> flightConnections = new ArrayList<>();
        if (isAdditionalStopAllowed(attributes.getMaxStops())) {
            for (Leg flight : routeFlights) { //TODO CAN BE REMOVED IF METHOD CHANGED
                flightConnections.addAll(findTransferFlights(flight, destination, attributes,
                        allRoutes));
            }
        }
        return flightConnections;
    }

    private List<FlightConnection> findTransferFlights(Leg flight, Leg destination,
                                                       FlightSearchAttributes attributes,
                                                       List<Route> allRoutes) {
        String nextAirport = flight.getArrivalAirport();
        String destinationAirport = destination.getArrivalAirport();
        LocalDateTime nextMinDepartureDateTime =
                flight.getArrivalDateTime().plus(attributes.getMinimalTransferTime());
        LocalDateTime maxArrivalDateTime = destination.getArrivalDateTime();
        Leg nextDestinationFlight = new Leg(nextAirport, destinationAirport,
                nextMinDepartureDateTime, maxArrivalDateTime);
        attributes.decrementMaxStops();

        List<FlightConnection> foundConnections = findFlights(nextDestinationFlight, attributes,
                allRoutes);
        return addFlightToConnections(foundConnections, flight);
    }

    private List<FlightConnection> addFlightToConnections(List<FlightConnection> foundConnections
            , Leg flight) {
        foundConnections.forEach(connection -> connection.addFlight(0, flight));
        return foundConnections;
    }

    private List<Route> filterRouteDepartureAirport(List<Route> routes, String departureAirport) {
        Predicate<Route> departureRoutesFilter =
                route -> route.getAirportFrom().equals(departureAirport);
        return routes.stream().filter(departureRoutesFilter).collect(Collectors.toList());
    }

    private boolean isDirectRoute(Route route, String destinationAirport) {
        return route.getAirportTo().equals(destinationAirport);
    }

    private boolean isAdditionalStopAllowed(int maxNumberOfStops) {
        return maxNumberOfStops > 0;
    }
}
