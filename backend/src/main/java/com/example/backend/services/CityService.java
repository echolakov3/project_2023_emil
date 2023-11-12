package com.example.backend.services;

import com.example.backend.dtos.BaseCity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface CityService {
    Page<BaseCity> fetchCitiesFromDataSource(final int page,
                                             final int size,
                                             final boolean shouldIncludeDensity,
                                             final String fieldToSortBy,
                                             final String sortOrder,
                                             final String containsNameFilter);

    void addCity(final @RequestBody BaseCity baseCity) throws IOException;
}
