package com.luciocoelho.weatherforecast.service;

import org.springframework.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.IntStream;

import com.luciocoelho.weatherforecast.dto.WeatherForecastResponse;
import com.luciocoelho.weatherforecast.dto.OpenMeteoResponse;
import com.luciocoelho.weatherforecast.dto.DailyWeatherForecast;
import com.luciocoelho.weatherforecast.dto.NominatimResponse;

@Service
@RequiredArgsConstructor
public class WeatherForecastService {

    public static final String CACHE_NAME = "weatherforecast";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;

    private WeatherForecastResponse checkCache(String zipCode){

        Cache weatherForecastCache = cacheManager.getCache(CACHE_NAME);

        if (weatherForecastCache == null) {
            return null;
        }

        WeatherForecastResponse result = weatherForecastCache.get(zipCode, WeatherForecastResponse.class);

        if (result == null) {
            return null;
        }

        return result.makeCached();
    }

    private NominatimResponse[] getCoordinatesFromZip(String zipCode){

        String nominatimUrl = String.format("https://nominatim.openstreetmap.org/search?postalcode=%s&format=jsonv2&limit=1", zipCode);
        NominatimResponse[] result = restTemplate.getForObject(nominatimUrl, NominatimResponse[].class);

        if (result == null || result.length == 0) {
            throw new RuntimeException("No location was found for the given zip code: " + zipCode);
        }

        return result;
    }

    private WeatherForecastResponse getWeatherFromCoordinates(String lattitude, String longitude){

        String openMeteoUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current=temperature_2m&daily=temperature_2m_max,temperature_2m_min&timezone=auto",
                lattitude, longitude);

        OpenMeteoResponse openMeteoResponse = restTemplate.getForObject(openMeteoUrl, OpenMeteoResponse.class);

        if (openMeteoResponse == null || openMeteoResponse.current() == null ||
                openMeteoResponse.daily() == null) {
            throw new RuntimeException("No weather data found for the given coordinates: "
                    + lattitude + ", " + longitude);
        }

        // extended forecast (next 3 days, excluding today)
        List<DailyWeatherForecast> extendedForecast =
                IntStream.range(1, Math.min(4, openMeteoResponse.daily().time().size()))
                        .mapToObj(i -> new DailyWeatherForecast(
                                openMeteoResponse.daily().time().get(i),
                                openMeteoResponse.daily().temperature_2m_max().get(i),
                                openMeteoResponse.daily().temperature_2m_min().get(i)
                        ))
                        .toList();

        return new WeatherForecastResponse(
                        openMeteoResponse.current().temperature_2m(),
                        openMeteoResponse.daily().temperature_2m_max().get(0),
                        openMeteoResponse.daily().temperature_2m_min().get(0),
                        extendedForecast,
                        false
                    );
    }

    private void updateCache(String zipCode, WeatherForecastResponse response){

        Cache weatherForecastCache = cacheManager.getCache(CACHE_NAME);

        if (weatherForecastCache == null) {
            return;
        }
        weatherForecastCache.put(zipCode, response);
    }

    public WeatherForecastResponse getWeatherFromZipCode(String zipCode) {

        // Checks first if there's a cached version
        WeatherForecastResponse cachedResponse = checkCache(zipCode);

        if (cachedResponse != null) {
                return cachedResponse.makeCached();
        }

        // Forecast not in cache, going the regular way then.
        try {
            // Retriveting coordinates
            NominatimResponse[] nominatimResponse = getCoordinatesFromZip(zipCode);

            String lattitude = nominatimResponse[0].lat();
            String longitude = nominatimResponse[0].lon();

            WeatherForecastResponse response = getWeatherFromCoordinates(lattitude, longitude);

            updateCache(zipCode, response);

            return response;

        }
        catch (Exception e) {
            throw new RuntimeException("Error during forecast retrieval: " + e.getMessage());
        }
    }

}