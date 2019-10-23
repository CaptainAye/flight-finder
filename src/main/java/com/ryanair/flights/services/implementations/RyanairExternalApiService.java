package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.Route;
import com.ryanair.flights.services.interfaces.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RyanairExternalApiService implements RouteService {
    private static final String RYANAIR_OPERATOR = "RYANAIR";
    public static final String ROUTES_URL = "https://services-api.ryanair.com/locate/3/routes";

    private RestTemplate restTemplate;

    public RyanairExternalApiService(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Route> getRoutes() {
        List<Route> routes = restTemplate.exchange(ROUTES_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Route>>() {
                }).getBody();
        return routes == null ? Collections.emptyList() : filterRoutes(routes);
    }

    private List<Route> filterRoutes(List<Route> routes) {
        return routes.stream().filter(getOnlyRyanairOperator()).filter(getOnlyNoConnectingAirport())
                .collect(Collectors.toList());

    }

    private Predicate<Route> getOnlyRyanairOperator() {
        return route -> route.getOperator().equalsIgnoreCase(RYANAIR_OPERATOR);
    }

    private Predicate<Route> getOnlyNoConnectingAirport() {
        return route -> route.getConnectingAirport() == null;
    }
}
