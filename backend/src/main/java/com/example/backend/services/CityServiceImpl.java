package com.example.backend.services;

import com.example.backend.comparators.CityComparator;
import com.example.backend.dtos.BaseCity;
import com.example.backend.exceptions.BadRequestException;
import com.example.backend.repositories.CityRepositoryImpl;
import com.example.backend.transformers.CityTransformer;
import com.example.backend.utils.AppUtil;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Comparator;

@Service
public class CityServiceImpl implements CityService {

    private final CacheManager cacheManager;
    private final CityRepositoryImpl cityRepositoryImpl;
    private final CityTransformer cityTransformer;
    private final AppUtil appUtil;

    public CityServiceImpl(final CacheManager cacheManager, final CityRepositoryImpl cityRepositoryImpl, final CityTransformer cityTransformer,
                           final AppUtil appUtil) {
        this.cacheManager = cacheManager;
        this.cityRepositoryImpl = cityRepositoryImpl;
        this.cityTransformer = cityTransformer;
        this.appUtil = appUtil;
    }

    //@Cacheable("pagedCitiesCache")
    public Page<BaseCity> fetchCitiesFromDataSource(final int page,
                                                    final int size,
                                                    final boolean shouldIncludeDensity,
                                                    final String fieldToSortBy,
                                                    final String sortOrder,
                                                    final String containsNameFilter) {
        final Pageable pageable = buildPageable(page, size, fieldToSortBy, sortOrder);

        final BaseCity[] cities = cityRepositoryImpl.getCitiesFromDataSource();
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), cities.length);

        if (start > end) {
            throw new BadRequestException("too big page number");
        }
        BaseCity[] pagedCities = Arrays.copyOfRange(!shouldIncludeDensity ? cities : cityTransformer.convertBaseCitiesToCitiesWithDensities(cities), start, end);

        if (StringUtils.hasText(fieldToSortBy)) {
            Comparator<BaseCity> comparator = new CityComparator(fieldToSortBy);
            comparator = getSortingOrder(pageable).equals(Sort.Direction.ASC.name()) ? comparator : comparator.reversed();
            Arrays.sort(pagedCities, comparator);
        }

        if (StringUtils.hasText(containsNameFilter)) {
            pagedCities = Arrays.stream(pagedCities).filter(city -> city.getName().contains(containsNameFilter)).toArray(BaseCity[]::new);
        }

        return new PageImpl<>(Arrays.asList(pagedCities), pageable, cities.length);
    }

    public void addCity(final BaseCity baseCity) {
        cityRepositoryImpl.addCityToDataSource(baseCity);
    }

    private Pageable buildPageable(final int page, final int size, final String fieldToSortBy, final String sortOrder) {
        if (StringUtils.hasText(fieldToSortBy) && !appUtil.hasField(BaseCity.class, fieldToSortBy)) {
            throw new BadRequestException("no such field for sorting in cities");
        }

        final Pageable pageable;
        if (StringUtils.hasText(fieldToSortBy) && appUtil.hasField(BaseCity.class, fieldToSortBy)) {
            Sort sortOrderFilter = Sort.by(fieldToSortBy);
            if (StringUtils.hasText(sortOrder)) {
                if (sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name())) {
                    sortOrderFilter = sortOrderFilter.ascending();
                } else if (sortOrder.equalsIgnoreCase(Sort.Direction.DESC.name())) {
                    sortOrderFilter = sortOrderFilter.descending();
                }
            }

            pageable = PageRequest.of(page, size, sortOrderFilter);
        } else {
            pageable = PageRequest.of(page, size);
        }
        return pageable;
    }

    private String getSortingOrder(final Pageable pageable) {
        final Sort sort = pageable.getSort();

        if (sort != null && !sort.isEmpty()) {
            final Sort.Order firstOrder = sort.getOrderFor(sort.iterator().next().getProperty());

            if (firstOrder != null) {
                return firstOrder.getDirection().name();
            }
        }

        return Sort.Direction.ASC.name();
    }


}
