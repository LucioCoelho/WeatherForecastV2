package com.luciocoelho.weatherforecast.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.luciocoelho.weatherforecast.service.WeatherForecastService;
import com.luciocoelho.weatherforecast.dto.WeatherForecastResponse;


@RestController
@RequestMapping("/weatherforecast")
@RequiredArgsConstructor
public class WeatherForecastController {

    @Autowired
    private WeatherForecastService weatherForecastService;

    @GetMapping("/{zipCode}")
    public WeatherForecastResponse getWeatherForecast(@PathVariable String zipCode) {
        return weatherForecastService.getWeatherFromZipCode(zipCode);
    }

}
