package com.example.backend.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CityWithDensity extends BaseCity {
    public double density;
}
