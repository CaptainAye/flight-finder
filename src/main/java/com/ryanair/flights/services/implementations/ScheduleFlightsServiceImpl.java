package com.ryanair.flights.services.implementations;

import com.ryanair.flights.model.Leg;
import com.ryanair.flights.model.Route;
import com.ryanair.flights.services.interfaces.ScheduleFlightsService;
import com.ryanair.flights.services.interfaces.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleFlightsServiceImpl implements ScheduleFlightsService {

    private final ScheduleService scheduleService;

    public ScheduleFlightsServiceImpl(@Autowired ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public List<Leg> getScheduleFlights(Route route, LocalDateTime departureDateTime,
                                        LocalDateTime arrivalDateTime) {


        return null;
    }
}
