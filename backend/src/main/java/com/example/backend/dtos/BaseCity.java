package com.example.backend.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BaseCity {
    @NotBlank
    private String name;
    @Min(1)
    @Max(Integer.MAX_VALUE)
    private double area;
    @Min(1)
    @Max(Integer.MAX_VALUE)
    private int population;
}