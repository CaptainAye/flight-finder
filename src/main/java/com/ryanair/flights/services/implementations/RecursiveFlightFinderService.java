package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.FlightConnection;
import com.ryanair.flights.model.FlightInfo;
import com.ryanair.flights.model.Route;
import com.ryanair.flights.model.SearchCriteria;
import com.ryanair.flights.services.decorators.NoConnectingAirportFilterDecorator;
import com.ryanair.flights.services.decorators.RyanairOperatorFilterDecorator;
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
    public List<FlightConnection> findFlights(FlightInfo destination, SearchCriteria criteria) {
        validateInputArguments(destination, criteria);
        List<Route> routes = getRoutes();
        return findFlights(destination, criteria.getMaxStops(), criteria.getMinTransferTime(),
                routes);
    }

    private void validateInputArguments(FlightInfo destination, SearchCriteria criteria) {

        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null");
        }

        if (criteria == null) {
            throw new IllegalArgumentException("Attributes cannot be null");
        }
    }

    private List<FlightConnection> findFlights(FlightInfo destination, int maxStops,
                                               Duration minTransferTime, List<Route> allRoutes) {
        List<Route> outgoingRoutes =
                allRoutes.stream()
                        .filter(route -> route.getAirportFrom().equals(destination.getDepartureAirport()))
                        .collect(Collectors.toList());
        return findFlights(outgoingRoutes, destination, maxStops, minTransferTime, allRoutes);
    }

    private List<FlightConnection> findFlights(List<Route> outgoingRoutes, FlightInfo destination,
                                               int maxStops, Duration minTransferTime,
                                               List<Route> allRoutes) {
        List<FlightConnection> connections = new ArrayList<>();
        for (Route outgoingRoute : outgoingRoutes) {
            connections.addAll(findFlights(outgoingRoute, destination, maxStops, minTransferTime,
                    allRoutes));
        }
        return connections;
    }

    private List<FlightConnection> findFlights(Route outgoingRoute, FlightInfo destination,
                                               int maxStops, Duration minTransferTime,
                                               List<Route> allRoutes) {
        List<FlightConnection> connections = new ArrayList<>();
        if (isDirectRoute(outgoingRoute, destination.getArrivalAirport())) {
            connections = findDirectConnections(outgoingRoute, destination);
        } else if (isAdditionalStopAllowed(maxStops)) {
            connections = findTransferFlights(outgoingRoute, destination, maxStops,
                    minTransferTime, allRoutes);
        }
        return connections;
    }

    private List<FlightConnection> findTransferFlights(Route outgoingRoute,
                                                       FlightInfo destination, int maxStops,
                                                       Duration minTransferTime,
                                                       List<Route> allRoutes) {

        FlightInfo nextDestination = new FlightInfo(destination);
        nextDestination.setDepartureAirport(outgoingRoute.getAirportTo());
        List<FlightConnection> nextStopConnections =
                findFlights(nextDestination, maxStops - 1, minTransferTime, allRoutes);
        FlightInfo outgoingFlightInfo = new FlightInfo(outgoingRoute.getAirportFrom(),
                outgoingRoute.getAirportTo(), destination.getDepartureDateTime(),
                destination.getArrivalDateTime());
        return addOutgoingFlightsToConnections(nextStopConnections, outgoingFlightInfo,
                minTransferTime);
    }

    private List<FlightConnection> addOutgoingFlightsToConnections(List<FlightConnection> nextStopConnections,
                                                                   FlightInfo outgoingFlightInfo,
                                                                   Duration minTransferTime) {
        List<FlightConnection> connections = new ArrayList<>();
        if (!nextStopConnections.isEmpty()) {
            List<FlightInfo> outgoingFlightsSchedule =
                    getScheduleFlights(outgoingFlightInfo);
            connections = addFlightsToConnections(outgoingFlightsSchedule,
                    nextStopConnections, minTransferTime);
        }
        return connections;
    }

    private List<FlightConnection> addFlightsToConnections(List<FlightInfo> flightsToAdd,
                                                           List<FlightConnection> currentConnections,
                                                           Duration minTransferTime) {
        return currentConnections.stream()
                .flatMap(connection ->
                        addFlightsToConnection(flightsToAdd, connection, minTransferTime).stream())
                .collect(Collectors.toList());
    }

    private List<FlightConnection> addFlightsToConnection(List<FlightInfo> flightsToAdd,
                                                          FlightConnection currentConnection,
                                                          Duration minTransferTime) {
        return flightsToAdd.stream().flatMap(flight -> addFlightToConnection(flight,
                currentConnection, minTransferTime).stream()).collect(Collectors.toList());
    }

    private List<FlightConnection> addFlightToConnection(FlightInfo flight,
                                                         FlightConnection connection,
                                                         Duration minTransferTime) {
        List<FlightConnection> connections = new ArrayList<>();
        LocalDateTime nextDestinationEarliestDepartureTime =
                flight.getArrivalDateTime().plus(minTransferTime);
        LocalDateTime nextDestinationDepartureDateTime =
                connection.getLegs().get(0).getDepartureDateTime();
        boolean isTransferTimeLongEnough =
                nextDestinationDepartureDateTime.isAfter(nextDestinationEarliestDepartureTime) ||
                        nextDestinationDepartureDateTime.equals(nextDestinationEarliestDepartureTime);
        if (isTransferTimeLongEnough) {
            FlightConnection newConnection =
                    new FlightConnection(connection.getLegs());
            newConnection.addFlight(0, flight);
            connections.add(newConnection);
        }
        return connections;
    }

    private List<FlightConnection> findDirectConnections(Route outgoingRoute,
                                                         FlightInfo destination) {
        FlightInfo outgoingFlightInfo = new FlightInfo(outgoingRoute.getAirportFrom(),
                outgoingRoute.getAirportTo(), destination.getDepartureDateTime(),
                destination.getArrivalDateTime());
        List<FlightInfo> flights = getScheduleFlights(outgoingFlightInfo);
        Function<FlightInfo, FlightConnection> flightConnectionMapper =
                flight -> new FlightConnection(Collections.singletonList(flight));
        return flights.stream().map(flightConnectionMapper).collect(Collectors.toList());
    }

    private List<Route> getRoutes() {
        return getListOrEmpty(routeService.getRoutes());
    }

    private List<FlightInfo> getScheduleFlights(FlightInfo flightInfo) {
        return getListOrEmpty(scheduleFlightsService.getScheduleFlights(flightInfo));
    }

    private boolean isDirectRoute(Route route, String destinationAirport) {
        return route.getAirportTo().equals(destinationAirport);
    }
    private boolean isAdditionalStopAllowed(int maxNumberOfStops) {
        return maxNumberOfStops > 0;
    }
}
