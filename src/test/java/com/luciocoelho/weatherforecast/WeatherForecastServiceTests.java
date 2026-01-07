package com.luciocoelho.weatherforecast;

import com.luciocoelho.weatherforecast.dto.WeatherForecastResponse;
import com.luciocoelho.weatherforecast.service.WeatherForecastService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherforecastServiceTests {

    private final String TEST_ZIP_CODE = "99999";

    @Mock
    private Cache weatherCache;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private WeatherForecastService weatherForecastService;

    @BeforeEach
    void setUp() {
        // Ensure the cacheManager returns the mocked cache
        when(cacheManager.getCache(WeatherForecastService.CACHE_NAME)).thenReturn(weatherCache);
    }

    @Test
    void testCacheHit() {
        WeatherForecastResponse cachedResponse = new WeatherForecastResponse(10.0, 20.0,
                5.0, java.util.Collections.emptyList(), false
        );
        when(weatherCache.get(eq(TEST_ZIP_CODE), eq(WeatherForecastResponse.class))).thenReturn(cachedResponse);

        WeatherForecastResponse result = weatherForecastService.getWeatherFromZipCode(TEST_ZIP_CODE);

        assertTrue(result.fromCache());
        assertEquals(cachedResponse.currentTemperature(), result.currentTemperature());
        verify(restTemplate, never()).getForObject(anyString(), any(Class.class));
    }

}
