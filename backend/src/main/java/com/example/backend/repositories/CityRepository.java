package com.example.backend.repositories;

import com.example.backend.dtos.BaseCity;

public interface CityRepository {
    BaseCity[] getCitiesFromDataSource();

    void addCityToDataSource(final BaseCity baseCity);
}
