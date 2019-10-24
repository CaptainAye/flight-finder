package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.Route;
import com.ryanair.flights.services.interfaces.RouteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
public class RyanairApiRouteServiceTest {
    private static final String ROUTES_BODY_RESPONSE = "[" +
            "{\"airportFrom\":\"AAL\",\"airportTo\":\"STN\",\"connectingAirport\":null," +
            "\"newRoute\":false,\"seasonalRoute\":false,\"operator\":\"RYANAIR\"," +
            "\"group\":\"CITY\",\"similarArrivalAirportCodes\":[],\"tags\":[]," +
            "\"carrierCode\":\"FR\"}," +
            "{\"airportFrom\":\"AAR\",\"airportTo\":\"GDN\"," +
            "\"connectingAirport\":null,\"newRoute\":false,\"seasonalRoute\":false," +
            "\"operator\":\"RYANAIR\",\"group\":\"ETHNIC\",\"similarArrivalAirportCodes\":[]," +
            "\"tags\":[],\"carrierCode\":\"FR\"}" +
            ",{\"airportFrom\":\"AAR\",\"airportTo\":\"STN\"," +
            "\"connectingAirport\":null,\"newRoute\":false,\"seasonalRoute\":false," +
            "\"operator\":\"AIR_MALTA\",\"group\":\"CITY\",\"similarArrivalAirportCodes\":[]," +
            "\"tags\":[],\"carrierCode\":\"FR\"}]";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RouteService externalApiService;
    private MockRestServiceServer mockServer;
    private Route expectedRoute1 = new Route("AAL", "STN", null, false, false, "RYANAIR", "CITY");
    private Route expectedRoute2 = new Route("AAR", "GDN", null, false, false, "RYANAIR", "ETHNIC");
    private Route expectedRoute3 = new Route("AAR", "STN", null, false, false, "AIR_MALTA", "CITY");


    @BeforeEach
    private void setup() {
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void givenExternalApiWorksFind_whenGettingRoutes_shouldReturnRouteList() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("https://services-api.ryanair" +
                ".com/locate/3/routes")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ROUTES_BODY_RESPONSE));

        List<Route> expectedRoutes = Arrays.asList(expectedRoute1, expectedRoute2,
                expectedRoute3);
        List<Route> actualRoutes = externalApiService.getRoutes();
        Assertions.assertEquals(expectedRoutes, actualRoutes);
    }

    @Test
    void givenExternalApiReturn4xx_whenGetRoutes_shouldReturnRouteList() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("https://services-api.ryanair" +
                ".com/locate/3/routes")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        Assertions.assertThrows(HttpClientErrorException.class,
                () -> externalApiService.getRoutes());
    }

    @Test
    void givenExternalApiReturn5xx_whenGetRoutes_shouldReturnRouteList() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("https://services-api.ryanair" +
                ".com/locate/3/routes")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        Assertions.assertThrows(HttpServerErrorException.class,
                () -> externalApiService.getRoutes());
    }
}
