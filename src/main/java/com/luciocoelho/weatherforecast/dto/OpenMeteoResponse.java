package com.luciocoelho.weatherforecast.dto;

import java.util.List;

public record OpenMeteoResponse(Current current,  Daily daily) {

    public record Current(
            String time,
            double temperature_2m
    ) {}

    public record Daily(
            List<String> time,
            List<Double> temperature_2m_max,
            List<Double> temperature_2m_min
    ) {}

}
