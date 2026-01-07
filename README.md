# WeatherForecast

## Overview

This is a simple API for supplying weather forecast to a given zip code. In the following sections, we will see how to run it, manually test it, and then go on over a general overview of the inner workings of the code. Finally, in the last section, we will see the points for improvement / future work.

## Running the Application

This project is based on Maven and so you will need it installed in you machine. In order to run it, you can just run in the command line (all instructions here for MacOS, other environments were not tested):

```
mvn spring-boot:run
```

That will get the application up and running, a process that should finish with a message like:

```
2025-11-25T16:12:46.288-03:00  INFO 28133 --- [weatherforecast] [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
```

After that, you can directly test the application in your browser of preference by calling the endpoint 

```
http://localhost:8080/weatherforecast/[zip code]
```

For instance, here an example for a NYC zip:

```
http://localhost:8080/weatherforecast/07020
```

And that returns the following JSON:

```
{
  "currentTemperature": 23.6,
  "maxTemperature": 24.7,
  "minTemperature": 8,
  "extendedForecast": [
    {
      "date": "2025-11-26",
      "maxTemperature": 22.8,
      "minTemperature": 9
    },
    {
      "date": "2025-11-27",
      "maxTemperature": 20.4,
      "minTemperature": 10.7
    },
    {
      "date": "2025-11-28",
      "maxTemperature": 21.4,
      "minTemperature": 8.8
    }
  ],
  "fromCache": false
}
```

One can see that the response contains current temperature, max and min and a forecast for the news few days. Also, the "fromCache" field tells if the data was recovered from the cache or retrieved / assembled at that moment. Indeed, if we call the same endpoint a second time, it will return true:

```
{
  "currentTemperature": 24.1,
  "maxTemperature": 24.7,
  "minTemperature": 8,
  "extendedForecast": [
    {
      "date": "2025-11-26",
      "maxTemperature": 22.8,
      "minTemperature": 9
    },
    {
      "date": "2025-11-27",
      "maxTemperature": 20.4,
      "minTemperature": 10.7
    },
    {
      "date": "2025-11-28",
      "maxTemperature": 21.4,
      "minTemperature": 8.8
    }
  ],
  "fromCache": true
}
```

By the way, that leads to some implementation details that will be described in the next session.

## Implementation Details / General Execution Flow

The project follows a pretty standard SpringBoot project structure, with code divided in service, dto, controller and config folders. The skeleton was created by Spring Intializr and new dependencies were introduced as needed. Anyway, the generation execution flow is described in the steps below:

1. Request hits controller
2. Controller calls service
3. In the service, first it's verified if the weather forecast for the given zip already exist in cache. If so, the cache value is returned.
4. If there's no cached value, then the weather data is retrieved in the following way:

    4.1. Zip code coordinates are retrieved though a call to the external API Nominatim

    4.2. Once the coordinates are retrieved, then a second external API, Open-Meteo, is called, retrieving all the weather data for the coordinates

    4.3. The desired data for the response is assembled and the response is cached and returned.

## Points of Improvement

The most glaring thing lacking in the project is testing. Unfortunately I left testing for last and I couldn't solve in due time a configuration problem involving mockito / junit / jupiter. Even though, I tried to add just a basic unit test of cache hit and miss cases for the service, whereas a robust testing approach should have broad unit test coverage and also additional layers, for instance integration tests.