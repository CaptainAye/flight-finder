package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.Destination;
import com.ryanair.flights.model.FlightConnection;
import com.ryanair.flights.model.Leg;
import com.ryanair.flights.model.Schedule;
import com.ryanair.flights.services.interfaces.FlightFinderService;
import com.ryanair.flights.services.interfaces.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.function.Predicate;

@Service
public class RyanairFlightFinderService implements FlightFinderService {

    private final ScheduleService scheduleService;

    private final Predicate<Destination> destinationFilter = destination ->
                    destination.getConnectingAirport() == null
                    && destination.getOperator().equals("RYANAIR");

    public RyanairFlightFinderService(@Autowired ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public List<FlightConnection> findFlights(Leg legToFind) {
        YearMonth startingScheduleDate = YearMonth.of(legToFind.getDepartureDateTime().getYear(),
                legToFind.getDepartureDateTime().getMonth());
        YearMonth endingScheduleDate = YearMonth.of(legToFind.getArrivalDateTime().getYear(),
                legToFind.getArrivalDateTime().getMonth());
        List<Schedule> schedules = scheduleService.getSchedules(
                legToFind.getDepartureAirport(),
                legToFind.getArrivalAirport(),
                startingScheduleDate,
                endingScheduleDate);

        return null;
    }
}
