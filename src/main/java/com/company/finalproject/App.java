package com.company.finalproject;

import com.company.finalproject.cryptoAPI.CoinResponse;
import com.company.finalproject.cryptoAPI.CryptoCall;
import com.company.finalproject.issAPI.IssCall;
import com.company.finalproject.issAPI.IssResponse;
import com.company.finalproject.weatherAPI.WeatherCall;
import com.company.finalproject.weatherAPI.WeatherResponse;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.DecimalFormat;
import java.util.Scanner;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
		new App().startApp();
	}

	WeatherCall weatherCall = new WeatherCall();
	IssCall issCall = new IssCall();
	CryptoCall cryptoCall = new CryptoCall();

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
					WeatherResponse weatherResponse = weatherCall.cityWeather(scan.next());
					if(weatherResponse!=null){
						System.out.println("=======================================================");
						System.out.println("Location: "+weatherResponse.name+"\nCountry: "+weatherResponse.sys.country+"\nTemperature: "+weatherResponse.main.temp
								+"F\nDescription: "+weatherResponse.weather[0].description+"\nWind Speed: "+weatherResponse.wind.speed+"mph");
					}
				}

				else if (answer == 2) {
					System.out.println("Welcome to the ISS location checker:");
					//request of data from API is handled in issLocation and cityWeather(latitude, longitude)method
					IssResponse issResponse = issCall.issLocation();
					WeatherResponse weatherResponse = weatherCall.cityWeather(issResponse.iss_position.latitude,issResponse.iss_position.longitude);
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
					IssResponse issResponse = issCall.issLocation();
					WeatherResponse weatherResponse = weatherCall.cityWeather(issResponse.iss_position.latitude,issResponse.iss_position.longitude);
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
					CoinResponse[] coinResponse = cryptoCall.cryptoPrice(scan.next());
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
				System.out.println(e.getMessage());
			}
			System.out.println("=======================================================");
			System.out.println("\nHave a look at the options again");
			System.out.println("1. Weather of any city\n2. Location of the International Space Station (ISS)"
					+"\n3. Weather in the location of the ISS\n4. Current Cryptocurrency Price"
					+"\n5. Exit");
		}
	}

}
