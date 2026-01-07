package com.luciocoelho.weatherforecast.dto;

import java.util.List;

public record WeatherForecastResponse(

    double currentTemperature,
    double maxTemperature,
    double minTemperature,
    List<DailyWeatherForecast> extendedForecast,
    boolean fromCache

) {
    public WeatherForecastResponse makeCached() {
        return new WeatherForecastResponse(
                this.currentTemperature,
                this.maxTemperature,
                this.minTemperature,
                this.extendedForecast,
                true
        );
    }
}
