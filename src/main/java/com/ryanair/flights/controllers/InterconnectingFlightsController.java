package com.ryanair.flights.controllers;

import com.ryanair.flights.model.FlightConnection;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class InterconnectingFlightsController {

    @RequestMapping("/interconnections")
    public List<FlightConnection> getInterconnectingFlights(@RequestParam("departure") String departureAirport, @RequestParam("arrival") String arrivalAirport, @RequestParam("departureDateTime") LocalDateTime departureDateTime, @RequestParam("arrivalDateTime") LocalDateTime arrivalDateTime) {
        return null;
    }
}
