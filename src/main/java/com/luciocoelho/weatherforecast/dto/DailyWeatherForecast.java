package com.luciocoelho.weatherforecast.dto;

public record DailyWeatherForecast(

        String date,
        double maxTemperature,
        double minTemperature

) {}
