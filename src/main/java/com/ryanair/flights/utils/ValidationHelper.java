package com.ryanair.flights.utils;

import java.time.LocalDateTime;

public class ValidationHelper {
    private ValidationHelper() {
    }

    public static void validateDatesRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("start DateTime cannot be after end DateTime");
        }
    }
}
