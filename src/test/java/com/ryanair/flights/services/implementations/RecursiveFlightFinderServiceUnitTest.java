package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.FlightConnection;
import com.ryanair.flights.model.FlightInfo;
import com.ryanair.flights.model.Route;
import com.ryanair.flights.model.SearchCriteria;
import com.ryanair.flights.services.interfaces.FlightFinderService;
import com.ryanair.flights.services.interfaces.RouteService;
import com.ryanair.flights.services.interfaces.ScheduleFlightsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ActiveProfiles({"unit", "all"})
@DisplayName("FlightFinderService unit tests")
public class RecursiveFlightFinderServiceUnitTest {

    private Duration minTransferTime = Duration.ofHours(2);

    private Route mockWroDubRoute = new Route("WRO", "DUB", null, false, false, "RYANAIR",
            "ETHNIC");
    private Route mockWroStnRoute = new Route("WRO", "STN", null, false, false, "RYANAIR",
            "ETHNIC");
    private Route mockStnDubRoute = new Route("STN", "DUB", null, false, false, "RYANAIR",
            "ETHNIC");
    private Route mockWroGdnRoute = new Route("WRO", "GDN", null, false, false, "RYANAIR",
            "ETHNIC");
    private Route mockGdnDubRoute = new Route("GDN", "DUB", null, false, false, "RYANAIR",
            "ETHNIC");

    private LocalDateTime date_2019_10_31_14_30 = LocalDateTime.of(2019, Month.OCTOBER, 31, 14, 30);
    private LocalDateTime date_2019_10_31_9_30 = LocalDateTime.of(2019, Month.OCTOBER, 31, 9, 30);
    private LocalDateTime date_2019_10_31_12_30 = LocalDateTime.of(2019, Month.OCTOBER, 31, 12, 30);
    private LocalDateTime date_2019_10_31_15_30 = LocalDateTime.of(2019, Month.OCTOBER, 31, 15, 30);
    FlightInfo stnDubAfternoonFlight = new FlightInfo("STN", "DUB", date_2019_10_31_14_30,
            date_2019_10_31_15_30);
    private LocalDateTime date_2019_10_31_18_30 = LocalDateTime.of(2019, Month.OCTOBER, 31, 18, 30);
    private LocalDateTime date_2019_10_31_19_30 = LocalDateTime.of(2019, Month.OCTOBER, 31, 19, 30);
    FlightInfo stnDubEveningFlight = new FlightInfo("STN", "DUB", date_2019_10_31_18_30,
            date_2019_10_31_19_30);
    private FlightInfo wroDubFlight = new FlightInfo("WRO", "DUB", date_2019_10_31_12_30,
            date_2019_10_31_15_30);
    private FlightInfo wroStnMorningFlight = new FlightInfo("WRO", "STN", date_2019_10_31_9_30,
            date_2019_10_31_12_30);
    private FlightInfo wroStnAfternoonFlight = new FlightInfo("WRO", "STN", date_2019_10_31_12_30,
            date_2019_10_31_15_30);
    private FlightInfo wroGdnAfternoonFlight = new FlightInfo("WRO", "GDN", date_2019_10_31_12_30,
            date_2019_10_31_15_30);
    private FlightInfo gdnDubEveningFlight = new FlightInfo("GDN", "DUB", date_2019_10_31_18_30,
            date_2019_10_31_19_30);
    private FlightConnection expectedTransferFlight1 =
            new FlightConnection(Arrays.asList(wroStnMorningFlight
                    , stnDubAfternoonFlight));
    private FlightConnection expectedTransferFlight2 =
            new FlightConnection(Arrays.asList(wroStnMorningFlight
                    , stnDubEveningFlight));
    private FlightConnection expectedTransferFlight3 =
            new FlightConnection(Arrays.asList(wroStnAfternoonFlight, stnDubEveningFlight));
    private FlightConnection expectedTransferFlight4 =
            new FlightConnection(Arrays.asList(wroGdnAfternoonFlight, gdnDubEveningFlight));
    private List<FlightConnection> expectedTransferFlightConnections =
            Arrays.asList(expectedTransferFlight1,
                    expectedTransferFlight2, expectedTransferFlight3, expectedTransferFlight4);
    private FlightConnection expectedDirectFlight =
            new FlightConnection(Collections.singletonList(wroDubFlight));
    private List<FlightConnection> expectedDirectFlightConnections =
            Collections.singletonList(expectedDirectFlight);

    @Mock
    private RouteService routeService;
    @Mock
    private ScheduleFlightsService scheduleFlightsService;
    private FlightFinderService flightFinderService;

    @BeforeEach
    private void setup() {
        MockitoAnnotations.initMocks(this);
        flightFinderService = new RecursiveFlightFinderService(scheduleFlightsService,
                routeService);
    }

    @Test
    @DisplayName("given ryanair direct flights are in schedule findFlights should return list of " +
            "direct flights")
    public void givenOnlyDirectFlightsInSchedule_whenSearchingFlightsWithOneStopMax_shouldReturnDirectFlights() {
        int maxStops = 1;
        LocalDateTime actualStartDateTime = LocalDateTime.of(2019, Month.OCTOBER, 31, 9, 30);
        LocalDateTime actualEndDateTime = LocalDateTime.of(2019, Month.NOVEMBER, 1, 15, 30);
        FlightInfo actualDestination = new FlightInfo("WRO", "DUB", actualStartDateTime,
                actualEndDateTime);
        SearchCriteria attributes = new SearchCriteria(maxStops, minTransferTime);

        setupMocksForRoutes();
        setupMocksForDirectFlightTest(actualStartDateTime, actualEndDateTime);

        List<FlightConnection> actualFlightConnections =
                flightFinderService.findFlights(actualDestination, attributes);

        Assertions.assertEquals(expectedDirectFlightConnections, actualFlightConnections);
    }

    private void setupMocksForDirectFlightTest(LocalDateTime actualStartDateTime,
                                               LocalDateTime actualEndDateTime) {
        FlightInfo flightSearchInfo = new FlightInfo(mockWroDubRoute.getAirportFrom(),
                mockWroDubRoute.getAirportTo(), actualStartDateTime, actualEndDateTime);
        Mockito.when(scheduleFlightsService.getScheduleFlights(flightSearchInfo))
                .thenReturn(Collections.singletonList(wroDubFlight));

    }

    @Test
    @DisplayName("Given only one stop flight are in schedule when findFligts with maxStops=1 " +
            "should return FlightConnections with one stop")
    void givenOnlyOneStopFlightsInSchedule_whenSearchingFlightWithOneStopMax_shouldReturnFlightConnectionsWithOneStop() {
        int maxStops = 1;
        LocalDateTime actualStartDateTime = LocalDateTime.of(2019, Month.OCTOBER, 30, 7, 0);
        LocalDateTime actualEndDateTime = LocalDateTime.of(2019, Month.OCTOBER, 31, 20, 0);
        FlightInfo actualDestination = new FlightInfo("WRO", "DUB", actualStartDateTime,
                actualEndDateTime);
        SearchCriteria attributes = new SearchCriteria(maxStops, minTransferTime);

        setupMocksForRoutes();
        setupMocksForOneStopTest(actualStartDateTime, actualEndDateTime);

        List<FlightConnection> actualFlightConnections =
                flightFinderService.findFlights(actualDestination, attributes);

        Assertions.assertEquals(expectedTransferFlightConnections, actualFlightConnections);
    }

    @Test
    @DisplayName("Given only one stop flight are in schedule when findFlight with maxStops=0 " +
            "should return empty list")
    void givenOnlyOneStopFlightsInSchedule_whenSearchingFlightWithZeroStopMax_shouldReturnEmptyList() {
        int maxStops = 0;
        LocalDateTime actualStartDateTime = LocalDateTime.of(2019, Month.OCTOBER, 30, 7, 0);
        LocalDateTime actualEndDateTime = LocalDateTime.of(2019, Month.OCTOBER, 31, 20, 0);
        FlightInfo actualDestination = new FlightInfo("WRO", "DUB", actualStartDateTime,
                actualEndDateTime);
        SearchCriteria attributes = new SearchCriteria(maxStops, minTransferTime);

        setupMocksForRoutes();
        setupMocksForOneStopTest(actualStartDateTime, actualEndDateTime);

        List<FlightConnection> actualFlightConnections =
                flightFinderService.findFlights(actualDestination, attributes);

        Assertions.assertEquals(Collections.emptyList(), actualFlightConnections);
    }

    private void setupMocksForRoutes() {
        Mockito.when(routeService.getRoutes()).thenReturn(Arrays.asList(mockWroStnRoute,
                mockWroDubRoute, mockStnDubRoute, mockWroGdnRoute, mockGdnDubRoute));
    }

    private void setupMocksForOneStopTest(LocalDateTime actualStartDateTime,
                                          LocalDateTime actualEndDateTime) {
        FlightInfo wroStnSearchInfo = new FlightInfo(mockWroStnRoute.getAirportFrom(),
                mockWroStnRoute.getAirportTo(), actualStartDateTime, actualEndDateTime);
        Mockito.when(scheduleFlightsService.getScheduleFlights(wroStnSearchInfo))
                .thenReturn(Arrays.asList(wroStnMorningFlight, wroStnAfternoonFlight));

        FlightInfo stnDubSearchInfo = new FlightInfo(mockStnDubRoute.getAirportFrom(),
                mockStnDubRoute.getAirportTo(), actualStartDateTime, actualEndDateTime);
        Mockito.when(scheduleFlightsService.getScheduleFlights(stnDubSearchInfo))
                .thenReturn(Arrays.asList(stnDubAfternoonFlight, stnDubEveningFlight));

        FlightInfo wroGdnSearchInfo = new FlightInfo(mockWroGdnRoute.getAirportFrom(),
                mockWroGdnRoute.getAirportTo(), actualStartDateTime, actualEndDateTime);
        Mockito.when(scheduleFlightsService.getScheduleFlights(wroGdnSearchInfo))
                .thenReturn(Collections.singletonList(wroGdnAfternoonFlight));

        FlightInfo gdnDubSearchInfo = new FlightInfo(mockGdnDubRoute.getAirportFrom(),
                mockGdnDubRoute.getAirportTo(), actualStartDateTime, actualEndDateTime);
        Mockito.when(scheduleFlightsService.getScheduleFlights(gdnDubSearchInfo))
                .thenReturn(Collections.singletonList(gdnDubEveningFlight));

    }

    @Test
    void givenDirectAndInterconnectionFlightsAvailable_whenSearchingFlightsWithOneStopMax_shouldReturnDirectAndInterconnectionFlights() {
        int maxStops = 1;
        LocalDateTime actualStartDateTime = LocalDateTime.of(2019, Month.OCTOBER, 30, 7, 0);
        LocalDateTime actualEndDateTime = LocalDateTime.of(2019, Month.OCTOBER, 31, 20, 0);
        FlightInfo actualDestination = new FlightInfo("WRO", "DUB", actualStartDateTime,
                actualEndDateTime);

        setupMocksForRoutes();
        setupMocksForDirectFlightTest(actualStartDateTime, actualEndDateTime);
        setupMocksForOneStopTest(actualStartDateTime, actualEndDateTime);

        List<FlightConnection> expectedFlightConnections = Arrays.asList(expectedTransferFlight1,
                expectedTransferFlight2, expectedTransferFlight3, expectedDirectFlight,
                expectedTransferFlight4);

        SearchCriteria attributes = new SearchCriteria(maxStops, minTransferTime);

        List<FlightConnection> actualFlightConnections =
                flightFinderService.findFlights(actualDestination, attributes);

        Assertions.assertEquals(expectedFlightConnections, actualFlightConnections);
    }

    @Test
    @DisplayName("Given no transfer flights when findFlights() should return empty list")
    void givenNoTransferFlights_whenFindFlights_shouldReturnEmptyList() {
        int maxStops = 1;
        SearchCriteria attributes = new SearchCriteria(maxStops, minTransferTime);
        LocalDateTime actualStartDateTime = LocalDateTime.of(2019, Month.OCTOBER, 30, 7, 0);
        LocalDateTime actualEndDateTime = LocalDateTime.of(2019, Month.OCTOBER, 31, 20, 0);
        FlightInfo actualDestination = new FlightInfo("WRO", "DUB", actualStartDateTime,
                actualEndDateTime);
        setupMocksForRoutes();
        FlightInfo wroStnSearchInfo = new FlightInfo(mockWroStnRoute.getAirportFrom(),
                mockWroStnRoute.getAirportTo(), actualStartDateTime, actualEndDateTime);
        Mockito.when(scheduleFlightsService.getScheduleFlights(wroStnSearchInfo))
                .thenReturn(Arrays.asList(wroStnMorningFlight, wroStnAfternoonFlight));
        LocalDateTime stnDunMorningStartTime =
                wroStnMorningFlight.getArrivalDateTime().plus(minTransferTime);
        LocalDateTime stnDunAfternoonStartTime =
                wroStnAfternoonFlight.getArrivalDateTime().plus(minTransferTime);
        FlightInfo stnDubSearchInfo1 = new FlightInfo(mockStnDubRoute.getAirportFrom(),
                mockStnDubRoute.getAirportTo(), stnDunMorningStartTime, actualEndDateTime);
        Mockito.when(scheduleFlightsService.getScheduleFlights(stnDubSearchInfo1))
                .thenReturn(Collections.emptyList());
        FlightInfo stnDubSearchInfo2 = new FlightInfo(mockStnDubRoute.getAirportFrom(),
                mockStnDubRoute.getAirportTo(), stnDunAfternoonStartTime, actualEndDateTime);
        Mockito.when(scheduleFlightsService.getScheduleFlights(stnDubSearchInfo2))
                .thenReturn(Collections.emptyList());
        List<FlightConnection> actualFlightConnections =
                flightFinderService.findFlights(actualDestination, attributes);

        Assertions.assertEquals(Collections.emptyList(), actualFlightConnections);
    }

    @Test
    @DisplayName("When destination is null findFlights should throw IllegalArgumentException")
    void whenDestinationIsNull_shouldThrowIllegalArgumentException() {
        int maxStops = 1;
        SearchCriteria attributes = new SearchCriteria(maxStops, minTransferTime);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> flightFinderService.findFlights(null, attributes));
    }

    @Test
    @DisplayName("When max number of stops < 0 findFlights should throw IllegalArgumentException")
    void whenMaxStopsLesserThan0_shouldThrowIllegalArgumentException() {
        LocalDateTime actualStartDateTime = LocalDateTime.of(2019, Month.OCTOBER, 30, 7, 0);
        LocalDateTime actualEndDateTime = LocalDateTime.of(2019, Month.OCTOBER, 31, 20, 0);
        FlightInfo actualDestination = new FlightInfo("WRO", "DUB", actualStartDateTime,
                actualEndDateTime);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> flightFinderService.findFlights(actualDestination, null));
    }

    @Test
    @DisplayName("Given no routes available when findFlights should return empty list of " +
            "FlightConnections")
    void givenNoRoutes_whenFindFlightsWroDub_shouldReturnEmptyList() {
        int maxStops = 1;
        SearchCriteria attributes = new SearchCriteria(maxStops, minTransferTime);
        LocalDateTime actualStartDateTime = LocalDateTime.of(2019, Month.OCTOBER, 30, 7, 0);
        LocalDateTime actualEndDateTime = LocalDateTime.of(2019, Month.OCTOBER, 31, 20, 0);
        Mockito.when(routeService.getRoutes()).thenReturn(null);
        FlightInfo actualDestination = new FlightInfo("WRO", "DUB", actualStartDateTime,
                actualEndDateTime);
        Assertions.assertEquals(Collections.emptyList(),
                flightFinderService.findFlights(actualDestination, attributes));
        Mockito.when(routeService.getRoutes()).thenReturn(Collections.emptyList());
        Assertions.assertEquals(Collections.emptyList(),
                flightFinderService.findFlights(actualDestination, attributes));
    }

    @Test
    @DisplayName("Given no schedules for dates from Wroclaw available when findFlights() WRO-DUB" +
            " should return empty list of FlightConnections")
    void givenNoSchedule_whenFindFlightsWroDub_shouldReturnEmptyList() {
        int maxStops = 1;
        SearchCriteria attributes = new SearchCriteria(maxStops, minTransferTime);
        LocalDateTime actualStartDateTime = LocalDateTime.of(2019, Month.OCTOBER, 30, 7, 0);
        LocalDateTime actualEndDateTime = LocalDateTime.of(2019, Month.OCTOBER, 31, 20, 0);
        setupMocksForRoutes();

        FlightInfo wroDubSearchInfo = new FlightInfo(mockWroDubRoute.getAirportFrom(),
                mockWroDubRoute.getAirportTo(), actualStartDateTime, actualEndDateTime);
        FlightInfo wroStnSearchInfo = new FlightInfo(mockWroStnRoute.getAirportFrom(),
                mockWroStnRoute.getAirportTo(), actualStartDateTime, actualEndDateTime);


        Mockito.when(scheduleFlightsService.getScheduleFlights(wroDubSearchInfo)).thenReturn(null);
        Mockito.when(scheduleFlightsService.getScheduleFlights(wroStnSearchInfo)).thenReturn(null);

        FlightInfo actualDestination = new FlightInfo("WRO", "DUB",
                actualStartDateTime, actualEndDateTime);
        Assertions.assertEquals(Collections.emptyList(),
                flightFinderService.findFlights(actualDestination, attributes));
        Mockito.when(scheduleFlightsService.getScheduleFlights(wroDubSearchInfo))
                .thenReturn(Collections.emptyList());
        Mockito.when(scheduleFlightsService.getScheduleFlights(wroStnSearchInfo))
                .thenReturn(Collections.emptyList());
        Assertions.assertEquals(Collections.emptyList(),
                flightFinderService.findFlights(actualDestination, attributes));
    }
}
