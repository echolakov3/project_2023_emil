package com.example.backend.utils;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class AppUtil {
    public boolean hasField(final Class<?> clazz, final String fieldName) {
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            return true;
        } catch (final NoSuchFieldException e) {
            return false;
        }
    }
}
