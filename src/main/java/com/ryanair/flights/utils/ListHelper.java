package com.ryanair.flights.utils;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListHelper {
    private ListHelper() {
    }

    public static <T> List<T> getListOrEmpty(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static <T> List<T> filterList(List<T> flights, List<Predicate<T>> predicates) {
        Stream<T> flightStream = flights.stream();
        for (Predicate<T> predicate : predicates) {
            flightStream = flightStream.filter(predicate);
        }
        return flightStream.collect(Collectors.toList());
    }

    public static <T> List<T> filterList(List<T> flights, Predicate<T> predicate) {
        return getListOrEmpty(flights).stream().filter(predicate).collect(Collectors.toList());
    }


}
