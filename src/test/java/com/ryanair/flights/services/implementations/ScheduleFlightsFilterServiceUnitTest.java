package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.FlightInfo;
import com.ryanair.flights.services.interfaces.ScheduleFlightsService;
import com.ryanair.flights.services.interfaces.ScheduleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

public class ScheduleFlightsFilterServiceUnitTest {

    @Mock
    private ScheduleService scheduleService;

    private ScheduleFlightsService scheduleFlightsService;

    @BeforeEach
    private void setup() {
        MockitoAnnotations.initMocks(this);
        scheduleFlightsService = new ScheduleFlightsFilterService(scheduleService);
    }

    @Test
    @DisplayName("Given flight starts at the same time that search departure time" +
            "when getScheduleFlights from for specific dates should return the flight")
    void givenFlightStartsAtTheSameTimeAsSearchDepartureDate_whenGetScheduleFlightsForSpecificDates_shouldReturnFlight() {
        FlightInfo flightSearchInfo = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 15, 0),
                LocalDateTime.of(2019, 10, 31, 20, 0));

        FlightInfo flightInRange = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 15, 0),
                LocalDateTime.of(2019, 10, 31, 18, 0));
        Mockito.when(scheduleService.getScheduleFlights("WRO", "DUB", YearMonth.of(2019, 10)))
                .thenReturn(Collections.singletonList(flightInRange));

        List<FlightInfo> expectedFlights = Collections.singletonList(flightInRange);
        Assertions.assertEquals(expectedFlights,
                scheduleFlightsService.getScheduleFlights(flightSearchInfo));
    }

    @Test
    @DisplayName("Given flight ends at the same time that search arrival time" +
            "when getScheduleFlights from for specific dates should return the flight")
    void givenFlightEndsAtTheSameTimeAsSearchArrivalDate_whenGetScheduleFlightsForSpecificDates_shouldReturnFlight() {
        FlightInfo flightSearchInfo = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 15, 0),
                LocalDateTime.of(2019, 10, 31, 20, 0));

        FlightInfo flightInRange = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 17, 0),
                LocalDateTime.of(2019, 10, 31, 20, 0));
        Mockito.when(scheduleService.getScheduleFlights("WRO", "DUB", YearMonth.of(2019, 10)))
                .thenReturn(Collections.singletonList(flightInRange));

        List<FlightInfo> expectedFlights = Collections.singletonList(flightInRange);
        Assertions.assertEquals(expectedFlights,
                scheduleFlightsService.getScheduleFlights(flightSearchInfo));
    }

    @Test
    @DisplayName("Given flight is between search departure and arrival times" +
            "when getScheduleFlights from for specific dates should return the flight")
    void givenFlightBetweenSearchDepartureAndArrivalTimes_whenGetScheduleFlightsForSpecificDates_shouldReturnFlight() {
        FlightInfo flightSearchInfo = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 15, 0),
                LocalDateTime.of(2019, 10, 31, 20, 0));

        FlightInfo flightInRange = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 16, 0),
                LocalDateTime.of(2019, 10, 31, 19, 0));
        Mockito.when(scheduleService.getScheduleFlights("WRO", "DUB", YearMonth.of(2019, 10)))
                .thenReturn(Collections.singletonList(flightInRange));

        List<FlightInfo> expectedFlights = Collections.singletonList(flightInRange);
        Assertions.assertEquals(expectedFlights,
                scheduleFlightsService.getScheduleFlights(flightSearchInfo));
    }

    @Test
    @DisplayName("Given flight starts before search departure time" +
            "when getScheduleFlights from for specific dates should filter out the flight")
    void givenFlightStartsBeforeSearchDepartureTime_whenGetScheduleFlightsForSpecificDates_shouldReturnEmptyList() {
        FlightInfo flightSearchInfo = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 15, 0),
                LocalDateTime.of(2019, 10, 31, 20, 0));

        FlightInfo flightInRange = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 14, 0),
                LocalDateTime.of(2019, 10, 31, 17, 0));
        Mockito.when(scheduleService.getScheduleFlights("WRO", "DUB", YearMonth.of(2019, 10)))
                .thenReturn(Collections.singletonList(flightInRange));

        List<FlightInfo> expectedFlights = Collections.emptyList();
        Assertions.assertEquals(expectedFlights,
                scheduleFlightsService.getScheduleFlights(flightSearchInfo));
    }

    @Test
    @DisplayName("Given flight ends after search arrival time" +
            "when getScheduleFlights from for specific dates should filter out the flight")
    void givenFlightEndsAfterSearchArrivalTime_whenGetScheduleFlightsForSpecificDates_shouldReturnEmptyList() {
        FlightInfo flightSearchInfo = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 15, 0),
                LocalDateTime.of(2019, 10, 31, 20, 0));

        FlightInfo flightInRange = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 18, 0),
                LocalDateTime.of(2019, 10, 31, 21, 0));
        Mockito.when(scheduleService.getScheduleFlights("WRO", "DUB", YearMonth.of(2019, 10)))
                .thenReturn(Collections.singletonList(flightInRange));

        List<FlightInfo> expectedFlights = Collections.emptyList();
        Assertions.assertEquals(expectedFlights,
                scheduleFlightsService.getScheduleFlights(flightSearchInfo));
    }

    @Test
    @DisplayName("Given no flights returned from schedule" +
            "when getScheduleFlights then should return empty list")
    void givenNoFlightsInSchedule_whenGetScheduleFlights_shouldReturnEmptyList() {
        FlightInfo flightSearchInfo = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 15, 0),
                LocalDateTime.of(2019, 10, 31, 20, 0));

        Mockito.when(scheduleService.getScheduleFlights("WRO", "DUB", YearMonth.of(2019, 10)))
                .thenReturn(Collections.emptyList());

        List<FlightInfo> expectedFlights = Collections.emptyList();
        Assertions.assertEquals(expectedFlights,
                scheduleFlightsService.getScheduleFlights(flightSearchInfo));

        Mockito.when(scheduleService.getScheduleFlights("WRO", "DUB", YearMonth.of(2019, 10)))
                .thenReturn(null);
        Assertions.assertEquals(expectedFlights,
                scheduleFlightsService.getScheduleFlights(flightSearchInfo));
    }

    @Test
    @DisplayName("Given scheduleService throws HttpClientResponseException" +
            "when getScheduleFlights then should rethrow the exception")
    void givenScheduleServiceThrowsHttpClientException_whenGetScheduleFlights_shouldRethrowException() {
        FlightInfo flightSearchInfo = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 15, 0),
                LocalDateTime.of(2019, 10, 31, 20, 0));

        Mockito.when(scheduleService.getScheduleFlights("WRO", "DUB", YearMonth.of(2019, 10)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Assertions.assertThrows(HttpClientErrorException.class, () ->
                scheduleFlightsService.getScheduleFlights(flightSearchInfo));
    }

    @Test
    @DisplayName("Given scheduleService throws HttpClientResponseException" +
            "when getScheduleFlights then should rethrow the exception")
    void givenScheduleServiceThrowsHttpServerException_whenGetScheduleFlights_shouldRethrowException() {
        FlightInfo flightSearchInfo = new FlightInfo("WRO", "DUB",
                LocalDateTime.of(2019, 10, 31, 15, 0),
                LocalDateTime.of(2019, 10, 31, 20, 0));

        Mockito.when(scheduleService.getScheduleFlights("WRO", "DUB", YearMonth.of(2019, 10)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        Assertions.assertThrows(HttpServerErrorException.class, () ->
                scheduleFlightsService.getScheduleFlights(flightSearchInfo));
    }
}
