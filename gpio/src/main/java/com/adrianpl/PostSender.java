package com.adrianpl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;

import com.google.gson.Gson;

public class PostSender {
    public String sendPostRequest(HashMap<String, String> values) throws Exception {
        try {
            var uri = new URI("http://localhost:8081/test");
            var body = new Gson().toJson(values);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-type", "application/json")
                    .POST(BodyPublishers.ofString(body))
                    .build();

            var response = client.send(request, BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("There was a problem while sending POST request");
            throw e;
        } catch (URISyntaxException e) {
            System.out.println("There was a problem in uri string");
            throw e;
        }
    }
}
