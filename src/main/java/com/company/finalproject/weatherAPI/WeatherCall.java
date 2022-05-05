package com.company.finalproject.weatherAPI;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class WeatherCall {
    //returns a WeatherResponse from the Weather API based on the city passed
    public WeatherResponse cityWeather(String city) {
        WebClient cityClient = WebClient.create("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=723fe5c47faf0450d6e9212d3824b8c3&units=imperial");
        WeatherResponse weatherResponse = null;
        try {
            Mono<WeatherResponse> response = cityClient
                    .get()
                    .retrieve()
                    .bodyToMono(WeatherResponse.class);

            weatherResponse = response.share().block();
        }
        catch (WebClientResponseException we){
            int statusCode = we.getRawStatusCode();
            if(statusCode >= 400 && statusCode <500)
                System.out.println("Client Error");
            else if(statusCode >= 500 && statusCode <600)
                System.out.println("Server Error");
        }
        catch (Exception e){
            System.out.println("An error occurred: "+e.getMessage());
        }
        return weatherResponse;
    }

    //overloaded cityWeather method with latitude and longitude as parameters
    public WeatherResponse cityWeather(String lat, String lon) {
        WebClient cityClient = WebClient.create("https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid=723fe5c47faf0450d6e9212d3824b8c3&units=imperial");
        WeatherResponse weatherResponse = null;
        try {
            Mono<WeatherResponse> response = cityClient
                    .get()
                    .retrieve()
                    .bodyToMono(WeatherResponse.class);//change the json to a class

            weatherResponse = response.share().block();
        }
        catch (WebClientResponseException we){
            int statusCode = we.getRawStatusCode();
            if(statusCode >= 400 && statusCode <500)
                System.out.println("Client Error");
            else if(statusCode >= 500 && statusCode <600)
                System.out.println("Server Error");
        }
        catch (Exception e){
            System.out.println("An error occurred: "+e.getMessage());
        }
        return weatherResponse;
    }
}
