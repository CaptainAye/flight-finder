package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.*;
import com.ryanair.flights.services.interfaces.FlightFinderService;
import com.ryanair.flights.services.interfaces.ScheduleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ActiveProfiles({"unit", "all"})
public class RyanairFlightConnectionFinderServiceUnitTest {

    private FlightFinderService flightFinderService;

    @Mock
    private ScheduleService scheduleService;

    @BeforeEach
    private void setup() {
        MockitoAnnotations.initMocks(this);
        flightFinderService = new RyanairFlightFinderService(scheduleService);
    }

    @Test
    @DisplayName("given ryanair direct flights are available findFlights should return list of direct flights")
    public void givenOnlyDirectFlightsAvailable_whenSearchingFlights_shouldReturnDirectFlights() {

        Flight mockWroDubFlight1 = new Flight("2104",
                LocalTime.of(12, 30),
                LocalTime.of(15, 30));
        Flight mockWroDubFlight2 = new Flight("1204",
                LocalTime.of(9, 30),
                LocalTime.of(12, 30));
        FlightDay mockWroDubFlightDay1 = new FlightDay(10, Collections.singletonList(mockWroDubFlight1));
        FlightDay mockWroDubFlightDay2 = new FlightDay(31, Collections.singletonList(mockWroDubFlight2));
        Schedule mockWroDubSchedule = new Schedule(Month.OCTOBER,
                Arrays.asList(mockWroDubFlightDay1, mockWroDubFlightDay2));

        Mockito.when(scheduleService.getSchedules("WRO", "DUB",
                YearMonth.of(2019, Month.OCTOBER), YearMonth.of(2019, Month.NOVEMBER))).
                thenReturn(Collections.singletonList(mockWroDubSchedule));

        Leg expectedLeg = new Leg("WRO", "DUB",
                LocalDateTime.of(2019, Month.OCTOBER, 31, 9, 30),
                LocalDateTime.of(2019, Month.OCTOBER, 31, 12, 30));
        List<FlightConnection> expectedFlightConnections = Collections.singletonList(
                new FlightConnection(Collections.singletonList(expectedLeg)));

        Leg legToFind = new Leg("WRO", "DUB",
                LocalDateTime.of(2019, Month.OCTOBER, 31, 9, 30),
                LocalDateTime.of(2019,Month.NOVEMBER, 1, 15, 30));

        List<FlightConnection> actualFlightConnections = flightFinderService.findFlights(legToFind);
        Assertions.assertEquals(expectedFlightConnections, actualFlightConnections);

    }

}
