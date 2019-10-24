package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.ApiSchedule;
import com.ryanair.flights.model.Route;
import com.ryanair.flights.services.interfaces.RouteService;
import com.ryanair.flights.services.interfaces.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.YearMonth;
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
}
