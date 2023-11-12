package com.example.backend.repositories;

import com.example.backend.dtos.BaseCity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.util.List;

@Repository
public class CityRepositoryImpl implements CityRepository {

    private final CacheManager cacheManager;

    public CityRepositoryImpl(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    //@Cacheable("citiesCache")
    public BaseCity[] getCitiesFromDataSource() {
        final BaseCity[] cities;
        try {
            final ClassPathResource resource = new ClassPathResource("data/cities.json");
            final byte[] jsonData = FileCopyUtils.copyToByteArray(resource.getInputStream());

            final ObjectMapper objectMapper = new ObjectMapper();
            cities = objectMapper.readValue(jsonData, BaseCity[].class);
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
        return cities;
    }

    public void addCityToDataSource(final BaseCity baseCity) {
        final ClassPathResource resource = new ClassPathResource("data/cities.json");

        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final List<BaseCity> cities = objectMapper.readValue(resource.getFile(), new TypeReference<>() {
            });

            cities.add(baseCity);

            objectMapper.writeValue(resource.getFile(), cities);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
