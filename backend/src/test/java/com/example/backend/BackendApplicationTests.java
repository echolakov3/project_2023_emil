package com.example.backend;

import com.example.backend.controllers.CityController;
import com.example.backend.dtos.BaseCity;
import com.example.backend.dtos.CityWithDensity;
import com.example.backend.repositories.CityRepositoryImpl;
import com.example.backend.services.CityServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private CityController cityController;

    @SpyBean
    private CityServiceImpl cityService;

    @SpyBean
    private CityRepositoryImpl cityRepository;

    private Random random = new Random();

    @Test
    void testGetMockedCitiesSuccess() {
        final BaseCity baseCity = buildBaseCity();
        when(cityRepository.getCitiesFromDataSource()).thenReturn(List.of(baseCity).toArray(BaseCity[]::new));

        final Page<BaseCity> cities = cityController.getCities(0, 100, false, null, null, null);
        Assertions.assertEquals(1, cities.getContent().size());
    }

    @Test
    void testGetRealCitiesSuccess() {
        final Page<BaseCity> cities = cityController.getCities(0, 100, false, null, null, null);
        Assertions.assertEquals(100, cities.getContent().size());
    }

    @Test
    void testGetCitiesWithDensitySuccess() {
        final Page cities = cityController.getCities(0, 100, true, null, null, null);
        final List<CityWithDensity> content = (List<CityWithDensity>) cities.getContent();
        Assertions.assertNotNull(content.iterator().next().getDensity());
    }

    @Test
    void testGetCitiesWithSortingByFieldAscSuccess() {
        final Page cities = cityController.getCities(0, 100, false, "population", "asc", null);
        final Iterator<BaseCity> iterator = cities.getContent().iterator();
        Assertions.assertTrue(iterator.next().getPopulation() < iterator.next().getPopulation());
    }

    @Test
    void testGetCitiesWithSortingByFieldDescSuccess() {
        final Page cities = cityController.getCities(0, 100, false, "population", "desc", null);
        final Iterator<BaseCity> iterator = cities.getContent().iterator();
        Assertions.assertTrue(iterator.next().getPopulation() > iterator.next().getPopulation());
    }

    @Test
    void testGetCitiesWithContainsNameFilterSuccess() {
        final String containsNameFilter = "New";
        final List<BaseCity> cities = cityController.getCities(0, 100, false, null, null, containsNameFilter).getContent();
        Assertions.assertTrue(cities.stream().anyMatch(city -> city.getName().contains(containsNameFilter)));
    }

    @Test
    void testGetCitiesPagedSuccess() {
        final int totalCities = 100;

        final Page<BaseCity> citiesFirstPage = cityController.getCities(0, totalCities, false, null, null, null);
        Assertions.assertEquals(true, citiesFirstPage.isFirst());
        Assertions.assertEquals(false, citiesFirstPage.isLast());
        Assertions.assertEquals(true, citiesFirstPage.hasNext());

        final Page<BaseCity> citiesMiddlePage = cityController.getCities(1, totalCities, false, null, null, null);
        Assertions.assertEquals(false, citiesMiddlePage.isFirst());
        Assertions.assertEquals(false, citiesMiddlePage.isLast());
        Assertions.assertEquals(true, citiesMiddlePage.hasNext());

        final List<BaseCity> baseCities = IntStream.range(0, totalCities)
                .mapToObj(i -> buildBaseCity()).toList();
        when(cityRepository.getCitiesFromDataSource()).thenReturn(baseCities.toArray(BaseCity[]::new));

        final Page<BaseCity> citiesLastPage = cityController.getCities(1, totalCities, false, null, null, null);
        Assertions.assertEquals(false, citiesLastPage.isFirst());
        Assertions.assertEquals(true, citiesLastPage.isLast());
        Assertions.assertEquals(false, citiesLastPage.hasNext());
    }

    @Test
    void testAddCitySuccess() {
        final BaseCity baseCity = buildBaseCity();
        cityController.addCity(baseCity);
        final BaseCity fetchBaseCity = cityController.getCities(0, Integer.MAX_VALUE, false, null, null, baseCity.getName()).getContent().iterator().next();
        Assertions.assertEquals(baseCity.getName(), fetchBaseCity.getName());
        verify(cityService).addCity(baseCity);
        verify(cityRepository).addCityToDataSource(baseCity);
    }

    private BaseCity buildBaseCity() {
        final BaseCity baseCity = new BaseCity();
        baseCity.setName(UUID.randomUUID().toString());
        baseCity.setArea(random.nextDouble());
        baseCity.setPopulation(random.nextInt());
        return baseCity;
    }

}
