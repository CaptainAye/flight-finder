package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.FlightInfo;
import com.ryanair.flights.services.interfaces.ScheduleFlightsService;
import com.ryanair.flights.services.interfaces.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ryanair.flights.utils.ListHelper.filterList;

@Service
public class ScheduleFlightsFilterService implements ScheduleFlightsService {

    private final ScheduleService scheduleService;

    public ScheduleFlightsFilterService(@Autowired ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public List<FlightInfo> getScheduleFlights(FlightInfo flightInfo) {
        List<YearMonth> datesRange = getYearMonthRange(flightInfo.getDepartureDateTime(),
                flightInfo.getArrivalDateTime());
        List<FlightInfo> flights = getScheduleFlights(flightInfo, datesRange);
        return filterFlightsByDates(flights, flightInfo.getDepartureDateTime(),
                flightInfo.getArrivalDateTime());
    }

    private List<FlightInfo> getScheduleFlights(FlightInfo flightInfo, List<YearMonth> datesRange) {
        return datesRange.stream()
                .flatMap(date ->
                        scheduleService.getScheduleFlights(flightInfo.getDepartureAirport(),
                                flightInfo.getArrivalAirport(), date).stream())
                .collect(Collectors.toList());
    }

    private List<FlightInfo> filterFlightsByDates(List<FlightInfo> flights,
                                                  LocalDateTime departureDateTime,
                                                  LocalDateTime arrivalDateTime) {
        Predicate<FlightInfo> isFlightTimeAfterOrEqualsDepartureFilter =
                flight -> flight.getDepartureDateTime().isAfter(departureDateTime) ||
                        flight.getDepartureDateTime().equals(departureDateTime);
        Predicate<FlightInfo> isFlightTimeBeforeOrEqualsArrivalFilter =
                flight -> flight.getArrivalDateTime().isAfter(arrivalDateTime) ||
                        flight.getArrivalDateTime().equals(arrivalDateTime);
        List<Predicate<FlightInfo>> filters =
                Arrays.asList(isFlightTimeAfterOrEqualsDepartureFilter,
                        isFlightTimeBeforeOrEqualsArrivalFilter);
        return filterList(flights, filters);
    }

    private List<YearMonth> getYearMonthRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<YearMonth> yearMonths = new ArrayList<>();
        for (int year = startDate.getYear(); year <= endDate.getYear(); year++) {
            Month endMonth = year == endDate.getYear() ? endDate.getMonth() : Month.DECEMBER;
            Month startMonth = year == startDate.getYear() ? startDate.getMonth() : Month.JANUARY;
            for (int month = startMonth.getValue(); month <= endMonth.getValue(); month++) {
                yearMonths.add(YearMonth.of(year, month));
            }
        }
        return yearMonths;
    }
}
