package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.ApiSchedule;
import com.ryanair.flights.model.Flight;
import com.ryanair.flights.model.FlightDay;
import com.ryanair.flights.services.interfaces.ScheduleService;
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
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
public class RyanairApiScheduleServiceTest {


    //@formatter:off
    private static final String SCHEDULE_BODY_RESPONSE =
            "{\"month\":11," +
                    "\"days\":[" +
                    "{\"day\":1," +
                    "\"flights\":[{\"carrierCode\":\"FR\",\"number\":\"1926\"," +
                    "\"departureTime\":\"09:15\",\"arrivalTime\":\"12:50\"}]}," +
                    "{\"day\":3," +
                    "\"flights\":[{\"carrierCode\":\"FR\",\"number\":\"1926\"," +
                    "\"departureTime\":\"19:40\",\"arrivalTime\":\"23:15\"}]}," +
                    "{\"day\":4," +
                    "\"flights\":[{\"carrierCode\":\"FR\",\"number\":\"1926\"," +
                    "\"departureTime\":\"19:10\",\"arrivalTime\":\"22:45\"}]}" +
                    "]" +
                    "}";
    //@formatter:on

    private Flight day1Flight = new Flight("1926", LocalTime.of(9, 15), LocalTime.of(12, 50));
    private Flight day3Flight = new Flight("1926", LocalTime.of(19, 40), LocalTime.of(23, 15));
    private Flight day4Flight = new Flight("1926", LocalTime.of(19, 10), LocalTime.of(22, 45));
    private FlightDay flightDay1 = new FlightDay(1, Collections.singletonList(day1Flight));
    private FlightDay flightDay3 = new FlightDay(3, Collections.singletonList(day3Flight));
    private FlightDay flightDay4 = new FlightDay(4, Collections.singletonList(day4Flight));
    private List<FlightDay> expectedFlightDays = Arrays.asList(flightDay1, flightDay3, flightDay4);
    private ApiSchedule expectedSchedule = new ApiSchedule(Month.NOVEMBER, expectedFlightDays);

    @Autowired
    private ScheduleService externalApiService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;


    @BeforeEach
    private void setup() {
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void givenScheduleIsAvailable_whenGetSchedule_ReturnValidScheduleObject() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("https://services-api.ryanair" +
                ".com/timtbl/3/schedules/DUB/WRO/years/2019/months/11")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(SCHEDULE_BODY_RESPONSE));

        ApiSchedule actualSchedule = externalApiService.getSchedule("DUB", "WRO",
                YearMonth.of(2019, 11));
        Assertions.assertEquals(expectedSchedule, actualSchedule);
    }


    @Test
    void givenExternalApiReturn4xx_whenGetRoutes_shouldReturnRouteList() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("https://services-api.ryanair" +
                ".com/timtbl/3/schedules/DUB/WRO/years/2019/months/11")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        Assertions.assertThrows(HttpClientErrorException.class,
                () -> externalApiService.getSchedule("DUB", "WRO",
                        YearMonth.of(2019, 11)));
    }

    @Test
    void givenExternalApiReturn5xx_whenGetRoutes_shouldReturnRouteList() throws Exception {
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("https://services-api.ryanair" +
                ".com/timtbl/3/schedules/DUB/WRO/years/2019/months/11")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        Assertions.assertThrows(HttpServerErrorException.class,
                () -> externalApiService.getSchedule("DUB", "WRO",
                        YearMonth.of(2019, 11)));
    }
}
