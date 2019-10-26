package com.ryanair.flights.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ryanair.flights.utils.ValidationHelper;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.ryanair.flights.utils.DateTimeFormatHelper.ISO_DATE_TIME;
import static com.ryanair.flights.utils.ValidationHelper.*;

public class FlightInfo {

    private IataCode departureAirport;

    private IataCode arrivalAirport;

    private LocalDateTime departureDateTime;

    private LocalDateTime arrivalDateTime;

    public FlightInfo(String departureAirport, String arrivalAirport,
                      LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        this.departureAirport = IataCode.of(departureAirport);
        this.arrivalAirport = IataCode.of(arrivalAirport);
        this.departureDateTime = departureDateTime;
        this.arrivalDateTime = arrivalDateTime;
        validateDatesRange(departureDateTime, arrivalDateTime);
    }

    public FlightInfo(FlightInfo other) {
        this(other.getDepartureAirport(), other.getArrivalAirport(), other.getDepartureDateTime()
                , other.getArrivalDateTime());
    }

    public String getDepartureAirport() {
        return departureAirport.getIataCode();
    }

    public String getArrivalAirport() {
        return arrivalAirport.getIataCode();
    }

    @JsonFormat(pattern = ISO_DATE_TIME)
    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    @JsonFormat(pattern = ISO_DATE_TIME)
    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = IataCode.of(departureAirport);
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = IataCode.of(arrivalAirport);
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
        validateDatesRange(departureDateTime, arrivalDateTime);
    }

    public void setArrivalDateTime(LocalDateTime arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
        validateDatesRange(departureDateTime, arrivalDateTime);
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
        return "FlightInfo{" +
                "departureAirport=" + departureAirport +
                ", arrivalAirport=" + arrivalAirport +
                ", departureDateTime=" + departureDateTime +
                ", arrivalDateTime=" + arrivalDateTime +
                '}';
    }
}
