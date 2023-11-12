package com.example.backend.comparators;

import com.example.backend.dtos.BaseCity;

import java.lang.reflect.Field;
import java.util.Comparator;

public class CityComparator implements Comparator<BaseCity> {
    private String fieldName;

    public CityComparator(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public int compare(final BaseCity city1, final BaseCity city2) {
        try {
            final Field field = BaseCity.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            final Comparable value1 = (Comparable) field.get(city1);
            final Comparable value2 = (Comparable) field.get(city2);

            return value1.compareTo(value2);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }
}