package com.ryanair.flights;

import com.ryanair.flights.controllers.FlightFinderController;
import com.ryanair.flights.services.interfaces.FlightFinderService;
import com.ryanair.flights.services.interfaces.RouteService;
import com.ryanair.flights.services.interfaces.ScheduleFlightsService;
import com.ryanair.flights.services.interfaces.ScheduleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FlightsApplicationTests {

	@Autowired
	FlightFinderService flightFinderService;

	@Autowired
	FlightFinderController flightFinderController;

	@Autowired
	RouteService routeService;

	@Autowired
	ScheduleFlightsService scheduleFlightsService;

	@Autowired
	ScheduleService scheduleService;

	@Test
	@DisplayName("When context load beans should be successfully autowired")
	void whenContextLoads_thenBeansShouldBeAutowiredSuccessfully() {
		Assertions.assertNotNull(flightFinderController);
		Assertions.assertNotNull(flightFinderService);
		Assertions.assertNotNull(routeService);
		Assertions.assertNotNull(scheduleFlightsService);
		Assertions.assertNotNull(scheduleService);
	}

}
