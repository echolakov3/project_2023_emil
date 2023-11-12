package com.example.backend.transformers;

import com.example.backend.dtos.BaseCity;
import com.example.backend.dtos.CityWithDensity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CityTransformer {
    public CityWithDensity[] convertBaseCitiesToCitiesWithDensities(final BaseCity[] baseCities) {
        return Arrays.stream(baseCities).map(baseCity -> {
            final CityWithDensity cityWithDensity = new CityWithDensity();
            cityWithDensity.setName(baseCity.getName());
            cityWithDensity.setArea(baseCity.getArea());
            cityWithDensity.setPopulation(baseCity.getPopulation());
            cityWithDensity.setDensity(baseCity.getArea() > 0 ? baseCity.getPopulation() / baseCity.getArea() : 0.0f);
            return cityWithDensity;
        }).toArray(CityWithDensity[]::new);
    }
}
