package com.ryanair.flights.model;

import java.time.Month;
import java.util.List;
import java.util.Objects;

public class ApiSchedule {

    //int is a workaround for jackson mapping Month enum in 0..11 basis
    private int month;
    private List<FlightDay> days;

    public ApiSchedule(Month month, List<FlightDay> days) {
        this.month = month.getValue();
        this.days = days;
    }

    public ApiSchedule() {
    }

    public Month getMonth() {
        return Month.of(month);
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public List<FlightDay> getDays() {
        return days;
    }

    public void setDays(List<FlightDay> days) {
        this.days = days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiSchedule that = (ApiSchedule) o;
        return month == that.month &&
                Objects.equals(days, that.days);
    }

    @Override
    public int hashCode() {
        return Objects.hash(month, days);
    }

    @Override
    public String toString() {
        return "ApiSchedule{" +
                "month=" + getMonth() +
                ", days=" + getDays() +
                '}';
    }
}
