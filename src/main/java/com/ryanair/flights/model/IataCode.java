package com.ryanair.flights.model;

import java.util.Objects;

public class IataCode {
    private final String iataCode;

    private IataCode(String iataCode) {
        this.iataCode = iataCode;
        validateIataCode();
    }

    public static IataCode of(String iataCode) {
        return new IataCode(iataCode);
    }

    private void validateIataCode() {
        if (iataCode == null) {
            throw new IllegalArgumentException("iataCode cannot be null");
        }
        if (iataCode.length() != 3) {
            throw new IllegalArgumentException("iataCode length must be 3");
        }
    }

    public String getIataCode() {
        return iataCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IataCode iataCode1 = (IataCode) o;
        return Objects.equals(iataCode, iataCode1.iataCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iataCode);
    }

    @Override
    public String toString() {
        return "IataCode{" +
                "iataCode='" + iataCode + '\'' +
                '}';
    }
}
