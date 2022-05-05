package com.company.finalproject.issAPI;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

public class IssCall {
    //returns an IssResponse from the ISS API
    public IssResponse issLocation() {
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
}
