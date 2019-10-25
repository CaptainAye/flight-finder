package com.ryanair.flights.model;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

public class FlightInfo {

    @NotNull
    private final String departureAirport;

    @NotNull
    private final String arrivalAirport;

    @NotNull
    private final LocalDateTime departureDateTime;

    @NotNull
    private final LocalDateTime arrivalDateTime;

    public FlightInfo(String departureAirport, String arrivalAirport, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        validateNotEmpty();
    }

    private void validateNotEmpty() {
        boolean departureAirportIsEmpty = departureAirport == null || departureAirport.isEmpty();
        boolean arrivalAirportIsEmpty = arrivalAirport == null || arrivalAirport.isEmpty();
        boolean departureDateTimeIsNull = departureDateTime == null;
        boolean arrivalDateTimeIsNull = arrivalDateTime == null;

        if (departureAirportIsEmpty || arrivalAirportIsEmpty || departureDateTimeIsNull || arrivalDateTimeIsNull) {
            throw new IllegalArgumentException("Leg arguments cannot be null nor empty");
        }
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightInfo flightInfo = (FlightInfo) o;
        return Objects.equals(departureAirport, flightInfo.departureAirport) &&
                Objects.equals(arrivalAirport, flightInfo.arrivalAirport) &&
                Objects.equals(departureDateTime, flightInfo.departureDateTime) &&
                Objects.equals(arrivalDateTime, flightInfo.arrivalDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departureAirport, arrivalAirport, departureDateTime, arrivalDateTime);
    }

    @Override
    public String toString() {
        return "Leg{" +
                "departureAirport='" + departureAirport + '\'' +
                ", arrivalAirport='" + arrivalAirport + '\'' +
                ", departureDateTime=" + departureDateTime +
                ", arrivalDateTime=" + arrivalDateTime +
                '}';
    }
}
