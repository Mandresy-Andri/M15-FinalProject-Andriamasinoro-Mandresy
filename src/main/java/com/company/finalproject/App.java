package com.company.finalproject;

import com.company.finalproject.cryptoAPI.CoinResponse;
import com.company.finalproject.issAPI.IssResponse;
import com.company.finalproject.weatherAPI.WeatherResponse;


import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.Scanner;

@SpringBootApplication
public class App {

	//loads the environment variables containing the api_keys
	Dotenv dotenv = Dotenv.configure()
			.directory("C:\\Users\\andri\\Documents\\Netflix\\Workspace\\M15-FinalProject-Andriamasinoro-Mandresy\\src\\main\\resources\\.env")
			.ignoreIfMalformed()
			.ignoreIfMissing()
			.load();

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
		new App().startApp();
	}

	public void startApp(){
		Scanner scan = new Scanner(System.in);
		System.out.println("\nWelcome user. Have a look at the options and enter the number of your choice:");
		System.out.println("1. Weather of any city\n2. Location of the International Space Station (ISS)"
				+"\n3. Weather in the location of the ISS\n4. Current Cryptocurrency Price"
				+"\n5. Exit");

		//Loop that shows the choices and asks for input
		while(scan.hasNext()) {
			String num=scan.next();
			try {
				int answer =Integer.parseInt(num);
				if (answer == 1) {
					System.out.println("Welcome to the weather checker. Enter the name of the city you want to know about:");
					//request of data from API is handled in cityWeather method
					WeatherResponse weatherResponse = cityWeather(scan.next());
					if(weatherResponse!=null){
						System.out.println("=======================================================");
						System.out.println("Location: "+weatherResponse.name+"\nCountry: "+weatherResponse.sys.country+"\nTemperature: "+weatherResponse.main.temp
								+"F\nDescription: "+weatherResponse.weather[0].description+"\nWind Speed: "+weatherResponse.wind.speed+"mph");
					}
				}
				else if (answer == 2) {
					System.out.println("Welcome to the ISS location checker:");
					//request of data from API is handled in issLocation and cityWeather(latitude, longitude)method
					IssResponse issResponse = issLocation();
					WeatherResponse weatherResponse = cityWeather(issResponse.iss_position.latitude,issResponse.iss_position.longitude);
					if (issResponse != null) {
						System.out.println("=======================================================");
						System.out.println("Latitude: " + issResponse.iss_position.latitude + "\nLongitude: " + issResponse.iss_position.longitude);
					}
					if(weatherResponse != null){
						if(weatherResponse.name=="")
							System.out.println("Location: The ISS is not currently in a country");
						else
							System.out.println("Location: "+weatherResponse.name+"\nCountry: "+weatherResponse.sys.country);
					}
				}
				else if (answer == 3){
					System.out.println("Welcome to the ISS location weather checker:");
					//request of data from API is handled in cryptoPrice method
					IssResponse issResponse = issLocation();
					WeatherResponse weatherResponse = cityWeather(issResponse.iss_position.latitude,issResponse.iss_position.longitude);
					if (issResponse != null) {
						System.out.println("=======================================================");
						System.out.println("Latitude: " + issResponse.iss_position.latitude + "\nLongitude: " + issResponse.iss_position.longitude);
					}
					if(weatherResponse!=null){
						if(weatherResponse.name=="")
							weatherResponse.name="Not in any country";
						System.out.println("Location: "+weatherResponse.name+"\nTemperature: "+weatherResponse.main.temp
									+"F\nDescription: "+weatherResponse.weather[0].description+"\nWind Speed: "+weatherResponse.wind.speed+"mph");
					}
				}
				else if (answer == 4) {
					System.out.println("Welcome to the crypto price finder. Enter the symbol of the crypto you want to check:");
					//request of data from API is handled in cryptoPrice method
					CoinResponse[] coinResponse = cryptoPrice(scan.next());
					if(coinResponse!=null){
						System.out.println("=======================================================");
						DecimalFormat fm = new DecimalFormat("#,###.##");//to format doubles with commas
						System.out.println("Name: "+coinResponse[0].name+"\nID: "+coinResponse[0].asset_id
								+"\nPrice: $"+fm.format(Double.parseDouble(coinResponse[0].price_usd)));
					}
				}
				else if (answer == 5) {
					System.out.println("Thanks for using the app");
					break;
				}
				else
					System.out.println("Please choose from the given options");
				}
			catch (Exception e){//handles errors such as mismatch input
				System.out.println("Please enter a valid number");
			}
			System.out.println("=======================================================");
			System.out.println("\nHave a look at the options again");
			System.out.println("1. Weather of any city\n2. Location of the International Space Station (ISS)"
					+"\n3. Weather in the location of the ISS\n4. Current Cryptocurrency Price"
					+"\n5. Exit");
		}
	}

	//Returns a CoinResponse list from the cryptocurrency API based on the crypto symbol passed
	//It is returned as an array and not an object because the json file defines it as an Array
	private CoinResponse[] cryptoPrice(String crypto) {
		WebClient coinClient = WebClient.create("https://rest.coinapi.io/v1/assets/"+crypto+"?apikey="+dotenv.get("CRYPTO_KEY"));
		CoinResponse[] coinResponse = null;
			try {
				Mono<CoinResponse[]> response = coinClient
						.get()
						.retrieve()
						.bodyToMono(CoinResponse[].class);

				 coinResponse = response.share().block();
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
			return coinResponse;
	}

	//returns an IssResponse from the ISS API
	private  IssResponse issLocation() {
		WebClient issClient = WebClient.create("http://api.open-notify.org/iss-now.json");
		IssResponse issResponse = null;
		try {
			Mono<IssResponse> response = issClient
					.get()
					.retrieve()
					.bodyToMono(IssResponse.class);

			issResponse = response.share().block();
		} catch (WebClientResponseException we) {
			int statusCode = we.getRawStatusCode();
			if (statusCode >= 400 && statusCode < 500)
				System.out.println("Client Error");
			else if (statusCode >= 500 && statusCode < 600)
				System.out.println("Server Error");
		} catch (Exception e) {
			System.out.println("An error occurred: " + e.getMessage());
		}
		return issResponse;
	}

	//returns a WeatherResponse from the Weather API based on the city passed
	private WeatherResponse cityWeather(String city) {
		WebClient cityClient = WebClient.create("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+dotenv.get("WEATHER_KEY")+"&units=imperial");
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
	private WeatherResponse cityWeather(String lat, String lon) {
		WebClient cityClient = WebClient.create("https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid="+dotenv.get("WEATHER_KEY")+"&units=imperial");
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
