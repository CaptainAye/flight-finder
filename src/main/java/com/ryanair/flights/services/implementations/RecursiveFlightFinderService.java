package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.FlightConnection;
import com.ryanair.flights.model.Leg;
import com.ryanair.flights.model.Route;
import com.ryanair.flights.model.SearchCriteria;
import com.ryanair.flights.services.interfaces.FlightFinderService;
import com.ryanair.flights.services.interfaces.RouteService;
import com.ryanair.flights.services.interfaces.ScheduleFlightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ryanair.flights.utils.ListHelper.getListOrEmpty;

@Service
public class RecursiveFlightFinderService implements FlightFinderService {

    private final ScheduleFlightsService scheduleFlightsService;
    private final RouteService routeService;

    public RecursiveFlightFinderService(@Autowired ScheduleFlightsService scheduleFlightsService,
                                        @Autowired RouteService routeService) {
        this.scheduleFlightsService = scheduleFlightsService;
        this.routeService = new NoConnectingAirportFilterDecorator(
                new RyanairOperatorFilterDecorator(routeService));
    }

    @Override
    public List<FlightConnection> findFlights(Leg destination, SearchCriteria criteria) {
        validateInputArguments(destination, criteria);
        List<Route> routes = getRoutes();
        return findFlights(destination, criteria, routes);
    }

    private void validateInputArguments(Leg destination, SearchCriteria criteria) {

        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null");
        }

        if (criteria == null) {
            throw new IllegalArgumentException("Attributes cannot be null");
        }
    }

    private List<Route> getRoutes() {
        return getListOrEmpty(routeService.getRoutes());
    }

    private List<FlightConnection> findFlights(Leg destination, SearchCriteria criteria,
                                               List<Route> routes) {
        String departureAirport = destination.getDepartureAirport();
        List<FlightConnection> flightConnections = new ArrayList<>();
        List<Route> departureRoutes = filterRouteDepartureAirport(routes, departureAirport);

        for (Route route : departureRoutes) {
            flightConnections.addAll(findFlights(route, destination, criteria, routes));
        }
        return flightConnections;
    }

    private List<FlightConnection> findFlights(Route route, Leg destination,
                                               SearchCriteria criteria,
                                               List<Route> allRoutes) {
        LocalDateTime departure = destination.getDepartureDateTime();
        LocalDateTime arrival = destination.getArrivalDateTime();
        List<Leg> scheduledFlights = getScheduleFlights(route, departure, arrival);
        return isDirectRoute(route, destination.getArrivalAirport()) ?
                findDirectFlights(scheduledFlights) :
                findTransferFlights(scheduledFlights, destination, criteria, allRoutes);
    }

    private List<Leg> getScheduleFlights(Route route, LocalDateTime departure,
                                         LocalDateTime arrival) {
        return getListOrEmpty(scheduleFlightsService.getScheduleFlights(route, departure, arrival));
    }

    private boolean isDirectRoute(Route route, String destinationAirport) {
        return route.getAirportTo().equals(destinationAirport);
    }

    private List<FlightConnection> findDirectFlights(List<Leg> routeFlights) {
        Function<Leg, FlightConnection> flightConnectionMapper =
                flight -> new FlightConnection(Collections.singletonList(flight));
        return routeFlights.stream().map(flightConnectionMapper).collect(Collectors.toList());
    }

    private List<FlightConnection> findTransferFlights(List<Leg> routeFlights, Leg destination,
                                                       SearchCriteria attributes,
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
                                                       SearchCriteria criteria,
                                                       List<Route> allRoutes) {
        Leg nextDestination = getNextDestination(flight, destination,
                criteria.getMinTransferTime());
        criteria.decrementMaxStops();
        List<FlightConnection> foundConnections = findFlights(nextDestination, criteria,
                allRoutes);
        return addFlightToConnections(foundConnections, flight);
    }

    private Leg getNextDestination(Leg latestFlight, Leg destination,
                                   Duration minTransferTime) {
        LocalDateTime nextEarliestDeparture =
                latestFlight.getArrivalDateTime().plus(minTransferTime);
        return new Leg(latestFlight.getArrivalAirport(), destination.getArrivalAirport(),
                nextEarliestDeparture, destination.getArrivalDateTime());
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

    private boolean isAdditionalStopAllowed(int maxNumberOfStops) {
        return maxNumberOfStops > 0;
    }
}
