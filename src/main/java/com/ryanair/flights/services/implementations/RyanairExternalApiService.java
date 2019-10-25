package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.*;
import com.ryanair.flights.services.interfaces.RouteService;
import com.ryanair.flights.services.interfaces.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RyanairExternalApiService implements RouteService, ScheduleService {
    private static final String ROUTES_URL = "https://services-api.ryanair.com/locate/3/routes";
    private static final String SCHEDULE_URL = "https://services-api.ryanair" +
            ".com/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}";

    private RestTemplate restTemplate;

    public RyanairExternalApiService(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Route> getRoutes() {
        List<Route> routes = restTemplate.exchange(ROUTES_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Route>>() {
                }).getBody();
        return routes == null ? Collections.emptyList() : routes;
    }

    @Override
    public ApiSchedule getSchedule(String departureAirport, String arrivalAirport, YearMonth date) {
        return restTemplate.getForObject(SCHEDULE_URL, ApiSchedule.class, departureAirport,
                arrivalAirport, date.getYear(), date.getMonthValue());
    }

    @Override
    public List<FlightInfo> getScheduleFlights(String departureAirport, String arrivalAirport,
                                               YearMonth date) {
        ApiSchedule schedule = getSchedule(departureAirport,
                arrivalAirport, date);
        return (getScheduleFlights(departureAirport, arrivalAirport, date, schedule.getDays()));
    }

    private List<FlightInfo> getScheduleFlights(String departureAirport, String arrivalAirport,
                                                YearMonth date, List<FlightDay> flightDays) {
        List<FlightInfo> flightList = new ArrayList<>();
        for (FlightDay flightDay : flightDays) {
            LocalDate flightDate = LocalDate.of(date.getYear(), date.getMonth(),
                    flightDay.getDay());
            flightList.addAll(getScheduleFlights(departureAirport, arrivalAirport, flightDate,
                    flightDay.getFlights()));
        }
        return flightList;
    }

    private List<FlightInfo> getScheduleFlights(String departureAirport, String arrivalAirport,
                                                LocalDate flightDate, List<Flight> flights) {
        List<FlightInfo> flightList = new ArrayList<>();
        for (Flight flight : flights) {
            LocalDateTime departure = LocalDateTime.of(flightDate, flight.getDepartureTime());
            LocalDateTime arrival = LocalDateTime.of(flightDate, flight.getArrivalTime());
            flightList.add(new FlightInfo(departureAirport, arrivalAirport, departure,
                    arrival));
        }
        return flightList;
    }
}
