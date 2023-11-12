package com.example.backend.controllers;

import com.example.backend.dtos.BaseCity;
import com.example.backend.exceptions.BadRequestException;
import com.example.backend.services.CityServiceImpl;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@CrossOrigin(origins = "${frontend.url}")
@Validated
public class CityController {

    private final CityServiceImpl cityServiceImpl;

    public CityController(final CityServiceImpl cityServiceImpl) {
        this.cityServiceImpl = cityServiceImpl;
    }

    @GetMapping("/")
    public String home() {
        return "hello world from backend";
    }

    @GetMapping("/cities")
    public Page<BaseCity> getCities(final @RequestParam(name = "page", defaultValue = "0") @Min(0) @Max(Integer.MAX_VALUE) int page,
                                    final @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(Integer.MAX_VALUE) int size,
                                    final @RequestParam(name = "shouldIncludeDensity", defaultValue = "false") boolean shouldIncludeDensity,
                                    final @RequestParam(name = "fieldToSortBy", defaultValue = "") String fieldToSortBy,
                                    final @RequestParam(name = "sortOrder", defaultValue = "") @Pattern(regexp = "asc|desc|^$") String sortOrder,
                                    final @RequestParam(name = "containsNameFilter", defaultValue = "") String containsNameFilter) {
        return cityServiceImpl.fetchCitiesFromDataSource(page, size, shouldIncludeDensity, fieldToSortBy, sortOrder, containsNameFilter);
    }

    @PostMapping("/cities")
    public void addCity(final @Validated @RequestBody BaseCity baseCity) {
        cityServiceImpl.addCity(baseCity);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(final BadRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(final ConstraintViolationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
