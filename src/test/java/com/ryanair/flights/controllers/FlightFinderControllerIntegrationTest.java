package com.ryanair.flights.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.NestedServletException;

import java.net.URI;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class FlightFinderControllerIntegrationTest {
    private static final String ROUTES_BODY_RESPONSE = "[" +
            "{\"airportFrom\":\"DUB\",\"airportTo\":\"WRO\",\"connectingAirport\":null," +
            "\"newRoute\":false,\"seasonalRoute\":false,\"operator\":\"RYANAIR\"," +
            "\"group\":\"CITY\",\"similarArrivalAirportCodes\":[],\"tags\":[]," +
            "\"carrierCode\":\"FR\"}," +
            "{\"airportFrom\":\"GDN\",\"airportTo\":\"WRO\"," +
            "\"connectingAirport\":null,\"newRoute\":false,\"seasonalRoute\":false," +
            "\"operator\":\"RYANAIR\",\"group\":\"ETHNIC\",\"similarArrivalAirportCodes\":[]," +
            "\"tags\":[],\"carrierCode\":\"FR\"}," +
            "{\"airportFrom\":\"DUB\",\"airportTo\":\"GDN\"," +
            "\"connectingAirport\":null,\"newRoute\":false,\"seasonalRoute\":false," +
            "\"operator\":\"RYANAIR\",\"group\":\"CITY\",\"similarArrivalAirportCodes\":[]," +
            "\"tags\":[],\"carrierCode\":\"FR\"}," +
            "{\"airportFrom\":\"DUB\",\"airportTo\":\"GDN\"," +
            "\"connectingAirport\":null,\"newRoute\":false,\"seasonalRoute\":false," +
            "\"operator\":\"AIR_MALTA\",\"group\":\"CITY\",\"similarArrivalAirportCodes\":[]," +
            "\"tags\":[],\"carrierCode\":\"FR\"}]";
    //@formatter:off
    private static final String DUB_WRO_BODY_RESPONSE =
            "{\"month\":11," +
                    "\"days\":[" +
                    "{\"day\":1," +
                    "\"flights\":[{\"carrierCode\":\"FR\",\"number\":\"1926\"," +
                    "\"departureTime\":\"09:15\",\"arrivalTime\":\"12:50\"}]}," +
                    "{\"day\":3," +
                    "\"flights\":[{\"carrierCode\":\"FR\",\"number\":\"5323\"," +
                    "\"departureTime\":\"14:40\",\"arrivalTime\":\"18:15\"}]}," +
                    "{\"day\":4," +
                    "\"flights\":[{\"carrierCode\":\"FR\",\"number\":\"1321\"," +
                    "\"departureTime\":\"19:10\",\"arrivalTime\":\"22:45\"}]}" +
                    "]" +
            "}";
    //@formatter:off
    private static final String DUB_GDN_BODY_RESPONSE =
            "{\"month\":11," +
                    "\"days\":[" +
                    "{\"day\":1," +
                    "\"flights\":[{\"carrierCode\":\"FR\",\"number\":\"1343\"," +
                    "\"departureTime\":\"23:15\",\"arrivalTime\":\"02:50\"}]}," +
                    "{\"day\":6," +
                    "\"flights\":[{\"carrierCode\":\"FR\",\"number\":\"2132\"," +
                    "\"departureTime\":\"22:40\",\"arrivalTime\":\"01:15\"}]}" +
                    "]" +
            "}";
    //@formatter:off
    private static final String GDN_WRO_BODY_RESPONSE =
            "{\"month\":11," +
                    "\"days\":[" +
                    "{\"day\":3," +
                    "\"flights\":[{\"carrierCode\":\"FR\",\"number\":\"2123\"," +
                    "\"departureTime\":\"19:45\",\"arrivalTime\":\"21:00\"}]}," +
                    "{\"day\":5," +
                    "\"flights\":[{\"carrierCode\":\"FR\",\"number\":\"1126\"," +
                    "\"departureTime\":\"14:40\",\"arrivalTime\":\"18:15\"}]}," +
                    "{\"day\":12," +
                    "\"flights\":[{\"carrierCode\":\"FR\",\"number\":\"9926\"," +
                    "\"departureTime\":\"19:10\",\"arrivalTime\":\"22:45\"}]}" +
                    "]" +
                    "}";
    @LocalServerPort
    private int port;
    @Autowired
    private FlightFinderController flightFinderController;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MockMvc mockMvc;
    private MockRestServiceServer mockServer;

    @BeforeEach
    private void setup() {
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("Given external apis work and have data when searching existing flight should " +
            "return flights")
    void givenExternalApisOk_whenSearchingExistingFlight_shouldReturnFlightsJson() throws Exception {

        URI targetURI = new URI("http://localhost:" + port + "/interconnections?departure=DUB" +
                "&arrival=WRO&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03T21:00");

        setupExternalApiMockServerForSuccessfulTest();
        mockMvc.perform(get(targetURI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].stops").value(0))
                .andExpect(jsonPath("$[0].legs.length()").value(1))
                .andExpect(jsonPath("$[0].legs[0].departureAirport").value("DUB"))
                .andExpect(jsonPath("$[0].legs[0].arrivalAirport").value("WRO"))
                .andExpect(jsonPath("$[0].legs[0].departureDateTime").value("2019-11-01T09:15"))
                .andExpect(jsonPath("$[0].legs[0].arrivalDateTime").value("2019-11-01T12:50"))
                .andExpect(jsonPath("$[1].legs.length()").value(1))
                .andExpect(jsonPath("$[1].stops").value(0))
                .andExpect(jsonPath("$[1].legs[0].departureAirport").value("DUB"))
                .andExpect(jsonPath("$[1].legs[0].arrivalAirport").value("WRO"))
                .andExpect(jsonPath("$[1].legs[0].departureDateTime").value("2019-11-03T14:40"))
                .andExpect(jsonPath("$[1].legs[0].arrivalDateTime").value("2019-11-03T18:15"))
                .andExpect(jsonPath("$[2].legs.length()").value(2))
                .andExpect(jsonPath("$[2].stops").value(1))
                .andExpect(jsonPath("$[2].legs[0].departureAirport").value("DUB"))
                .andExpect(jsonPath("$[2].legs[0].arrivalAirport").value("GDN"))
                .andExpect(jsonPath("$[2].legs[0].departureDateTime").value("2019-11-01T23:15"))
                .andExpect(jsonPath("$[2].legs[0].arrivalDateTime").value("2019-11-02T02:50"))
                .andExpect(jsonPath("$[2].legs[1].departureAirport").value("GDN"))
                .andExpect(jsonPath("$[2].legs[1].arrivalAirport").value("WRO"))
                .andExpect(jsonPath("$[2].legs[1].departureDateTime").value("2019-11-03T19:45"))
                .andExpect(jsonPath("$[2].legs[1].arrivalDateTime").value("2019-11-03T21:00"));
    }

    private void setupExternalApiMockServerForSuccessfulTest() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("https://services-api.ryanair" +
                ".com/locate/3/routes")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ROUTES_BODY_RESPONSE));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("https://services-api.ryanair" +
                ".com/timtbl/3/schedules/DUB/WRO/years/2019/months/11")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(DUB_WRO_BODY_RESPONSE));

        mockServer.expect(ExpectedCount.once(), requestTo(new URI("https://services-api.ryanair" +
                ".com/timtbl/3/schedules/GDN/WRO/years/2019/months/11")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(GDN_WRO_BODY_RESPONSE));
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("https://services-api.ryanair" +
                ".com/timtbl/3/schedules/DUB/GDN/years/2019/months/11")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(DUB_GDN_BODY_RESPONSE));
    }

    @Test
    @DisplayName("Given departure IataCode is incorrect should return BAD_REQUEST")
    void givenDepartureIataCodeIsIncorrect_shouldReturn_BAD_REQUEST() throws Exception {
        URI targetURI1 = new URI("http://localhost:" + port + "/interconnections?departure=DU" +
                "&arrival=WRO&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03T21:00");
        URI targetURI2 = new URI("http://localhost:" + port + "/interconnections?departure=" +
                "&arrival=WRO&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03T21:00");
        URI targetURI3 = new URI("http://localhost:" + port + "/interconnections?departure=DULL" +
                "&arrival=WRO&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03T21:00");
        URI targetURI4 = new URI("http://localhost:" + port + "/interconnections?" +
                "arrival=WRO&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03T21:00");
        Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(get(targetURI1))
                        .andExpect(status().isBadRequest()));
        Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(get(targetURI2))
                        .andExpect(status().isBadRequest()));
        Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(get(targetURI3))
                        .andExpect(status().isBadRequest()));

        mockMvc.perform(get(targetURI4)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given arrival IataCode is incorrect should return BAD_REQUEST")
    void givenArrivalIataCodeIsIncorrect_shouldReturn_BAD_REQUEST() throws Exception {
        URI targetURI1 = new URI("http://localhost:" + port + "/interconnections?departure=WRO" +
                "&arrival=AA&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03T21:00");
        URI targetURI2 = new URI("http://localhost:" + port + "/interconnections?departure=WRO" +
                "&arrival=&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03T21:00");
        URI targetURI3 = new URI("http://localhost:" + port + "/interconnections?departure=DUL" +
                "&arrival=WRSDD&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03T21:00");
        URI targetURI4 = new URI("http://localhost:" + port + "/interconnections?departure=DUL" +
                "&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03T21:00");
        Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(get(targetURI1))
                        .andExpect(status().isBadRequest()));
        Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(get(targetURI2))
                        .andExpect(status().isBadRequest()));
        Assertions.assertThrows(NestedServletException.class,
                () -> mockMvc.perform(get(targetURI3))
                        .andExpect(status().isBadRequest()));
        mockMvc.perform(get(targetURI4)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given departureDateTime is incorrect should return BAD_REQUEST")
    void givenDepartureDateTimeIsIncorrect_shouldReturn_BAD_REQUEST() throws Exception {
        URI targetURI1 = new URI("http://localhost:" + port + "/interconnections?departure=WRO" +
                "&arrival=WRO&departureDateTime=201-11-01T07:00&arrivalDateTime=2019-11-03T21:00");
        URI targetURI2 = new URI("http://localhost:" + port + "/interconnections?departure=WRO" +
                "&arrival=WRO&departureDateTime=2019-11-01@07:00&arrivalDateTime=2019-11-03T21:00");
        URI targetURI3 = new URI("http://localhost:" + port + "/interconnections?departure=DUL" +
                "&arrival=WRO&departureDateTime=2019-11-01T07:00:00&arrivalDateTime=2019-11-03T21:00");
        URI targetURI4 = new URI("http://localhost:" + port + "/interconnections?departure=DUL" +
                "&arrival=WRO&arrivalDateTime=2019-11-03T21:00");
        mockMvc.perform(get(targetURI1)).andExpect(status().isBadRequest());
        mockMvc.perform(get(targetURI2)).andExpect(status().isBadRequest());
        mockMvc.perform(get(targetURI3)).andExpect(status().isBadRequest());
        mockMvc.perform(get(targetURI4)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given arrivalDateTime is incorrect should return BAD_REQUEST")
    void givenArrivalDateTimeIsIncorrect_shouldReturn_BAD_REQUEST() throws Exception {
        URI targetURI1 = new URI("http://localhost:" + port + "/interconnections?departure=WRO" +
                "&arrival=WRO&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03");
        URI targetURI2 = new URI("http://localhost:" + port + "/interconnections?departure=WRO" +
                "&arrival=WRO&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03T21:0");
        URI targetURI3 = new URI("http://localhost:" + port + "/interconnections?departure=DUL" +
                "&arrival=WRO&departureDateTime=2019-11-01T07:00&arrivalDateTime=2019-11-03T21");
        URI targetURI4 = new URI("http://localhost:" + port + "/interconnections?departure=DUL" +
                "&arrival=WRO&departureDateTime=2019-11-01T07:00");
        mockMvc.perform(get(targetURI1)).andExpect(status().isBadRequest());
        mockMvc.perform(get(targetURI2)).andExpect(status().isBadRequest());
        mockMvc.perform(get(targetURI3)).andExpect(status().isBadRequest());
        mockMvc.perform(get(targetURI4)).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("when departure date time is after arrival date time should return BAD_REQUEST")
    void whenDepartureDateTimeIsAfterArrivalDateTime_thenShouldReturn_BAD_REQUEST() throws Exception{

        URI targetURI = new URI("http://localhost:" + port + "/interconnections?departure=DUB" +
                "&arrival=WRO&departureDateTime=2019-11-10T07:00&arrivalDateTime=2019-11-03T21:00");

        mockMvc.perform(get(targetURI)).andExpect(status().isBadRequest());
    }


}
