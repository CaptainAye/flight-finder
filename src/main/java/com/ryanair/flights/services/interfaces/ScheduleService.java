package com.ryanair.flights.services.interfaces;

import com.ryanair.flights.model.ApiSchedule;
import java.time.YearMonth;

public interface ScheduleService {
    ApiSchedule getSchedule(String departureAirport, String arrivalAirport, YearMonth date);
}
