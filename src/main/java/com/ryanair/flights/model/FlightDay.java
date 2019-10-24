package com.ryanair.flights.model;

import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Objects;

public class FlightDay {

    private int day;
    private List<Flight> flights;

    public FlightDay() {
    }

    public FlightDay(int day, List<Flight> flights) {
        this.day = day;
        this.flights = flights;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightDay flightDay = (FlightDay) o;
        return day == flightDay.day &&
                Objects.equals(flights, flightDay.flights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, flights);
    }

    @Override
    public String toString() {
        return "FlightDay{" +
                "day=" + day +
                ", flights=" + flights +
                '}';
    }
}
