package com.company.finalproject.cryptoAPI;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class CryptoCall {
    //Returns a CoinResponse list from the cryptocurrency API based on the crypto symbol passed
    //It is returned as an array and not an object because the json file defines it as an Array
    public CoinResponse[] cryptoPrice(String crypto) {
        WebClient coinClient = WebClient.create("https://rest.coinapi.io/v1/assets/"+crypto+"?apikey=61E86D0D-3ED1-4498-A9A3-45992BCA52D8");
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
}
