package com.ryanair.flights.controllers;

import com.ryanair.flights.model.FlightConnection;
import com.ryanair.flights.model.FlightInfo;
import com.ryanair.flights.model.SearchCriteria;
import com.ryanair.flights.services.interfaces.FlightFinderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.ryanair.flights.utils.DateTimeFormatHelper.ISO_DATE_TIME;

@RestController
@Validated
public class FlightFinderController {

    private FlightFinderService flightFinderService;

    public FlightFinderController(@Autowired FlightFinderService flightFinderService) {
        this.flightFinderService = flightFinderService;
    }

    @GetMapping("/interconnections")
    public List<FlightConnection> findInterconnections(@RequestParam @Size(min = 3, max = 3) String departure,
                                                       @RequestParam @Size(min = 3, max = 3) String arrival,
                                                       @RequestParam @DateTimeFormat(pattern =
                                                               ISO_DATE_TIME) LocalDateTime departureDateTime,
                                                       @RequestParam @DateTimeFormat(pattern =
                                                               ISO_DATE_TIME) LocalDateTime arrivalDateTime) {
        FlightInfo flightSearchInfo = new FlightInfo(departure, arrival, departureDateTime,
                arrivalDateTime);
        SearchCriteria criteria = new SearchCriteria(1, Duration.ofHours(2));
        return flightFinderService.findFlights(flightSearchInfo, criteria);
    }
}
