package com.ryanair.flights.services.interfaces;

import com.ryanair.flights.model.Destination;
import java.util.List;
import java.util.function.Predicate;

public interface DestinationService {
    List<Destination> getDestinations(Predicate<Destination> destinationFilter);
}
