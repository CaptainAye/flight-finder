package com.ryanair.flights.services.decorators;

import com.ryanair.flights.model.Route;
import com.ryanair.flights.services.decorators.NoConnectingAirportFilterDecorator;
import com.ryanair.flights.services.decorators.RyanairOperatorFilterDecorator;
import com.ryanair.flights.services.implementations.RyanairExternalApiService;
import com.ryanair.flights.services.interfaces.RouteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ActiveProfiles({"unit", "all"})
public class RouteServiceDecoratorsUnitTest {

    private static final String ROUTES_URL = "https://services-api.ryanair" +
            ".com/locate/3/routes";

    private RouteService ryanairService;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    private void setup() {
        restTemplate = Mockito.mock(RestTemplate.class);
        ryanairService = new RyanairExternalApiService(restTemplate);
    }

    @Test
    @DisplayName("Given different operators available when getRoutes() should return only RYANAIR operators")
    public void givenDifferentOperator_whenGetRoutes_shouldReturnOnlyRyanairOperatorWithNoConnectingAirport() {
        Route mockRyanairWroDub = new Route("WRO", "DUB", null, false, false, "RYANAIR", "ETHNIC");
        Route mockLufthansaWroDub = new Route("WRO", "DUB", null, false, false, "LUFTHANSA",
                "ETHNIC");
        Route mockConnectingWroDub = new Route("WRO", "DUB", null, false, false, "AIR_MALTA",
                "ETHNIC");

        ryanairService = new RyanairOperatorFilterDecorator(ryanairService);

        ResponseEntity<List<Route>> mockResponse =
                new ResponseEntity<>(Arrays.asList(mockRyanairWroDub, mockConnectingWroDub,
                mockLufthansaWroDub), HttpStatus.OK);
        Mockito.when(restTemplate.exchange(ROUTES_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Route>>() {
                })).thenReturn(mockResponse);

        List<Route> expectedRoutes = Collections.singletonList(mockRyanairWroDub);
        List<Route> actualRoutes = ryanairService.getRoutes();
        Assertions.assertEquals(expectedRoutes, actualRoutes);
    }

    @Test
    @DisplayName("Given routes with connecting airport available when getRoutes() should return only no connecting airport")
    public void givenDifferentConnectingAirports_whenGetRoutes_shouldReturnOnlyNoConnectingAirport() {

        Route mockRyanairWroDub = new Route("WRO", "DUB", null, false, false, "RYANAIR", "ETHNIC");
        Route mockLufthansaWroDub = new Route("WRO", "DUB", "GDA", false, false, "RYANAIR",
                "ETHNIC");
        Route mockConnectingWroDub = new Route("WRO", "DUB", "POR", false, false, "RYANAIR",
                "ETHNIC");

        ryanairService = new NoConnectingAirportFilterDecorator(ryanairService);

        ResponseEntity<List<Route>> mockResponse =
                new ResponseEntity<>(Arrays.asList(mockRyanairWroDub, mockConnectingWroDub,
                        mockLufthansaWroDub), HttpStatus.OK);
        Mockito.when(restTemplate.exchange(ROUTES_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Route>>() {
                })).thenReturn(mockResponse);

        List<Route> expectedRoutes = Collections.singletonList(mockRyanairWroDub);
        List<Route> actualRoutes = ryanairService.getRoutes();
        Assertions.assertEquals(expectedRoutes, actualRoutes);
    }

    @Test
    @DisplayName("Given no routes returned from restTemplate when get Routes should return empty list")
    void givenNoRoutesReturned_whenGetRoutes_shouldReturnEmptyList() {

        ResponseEntity<List<Route>> mockResponse =
                new ResponseEntity<>(null, HttpStatus.OK);
        Mockito.when(restTemplate.exchange(ROUTES_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Route>>() {
                })).thenReturn(mockResponse);
        mockResponse = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        Mockito.when(restTemplate.exchange(ROUTES_URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Route>>() {
                })).thenReturn(mockResponse);
        Assertions.assertEquals(Collections.emptyList(), ryanairService.getRoutes());
    }
}
