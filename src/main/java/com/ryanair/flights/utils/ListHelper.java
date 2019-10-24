package com.ryanair.flights.utils;

import java.util.Collections;
import java.util.List;

public class ListHelper {
    private ListHelper() {
    }

    public static <T> List<T> getListOrEmpty(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }
}
