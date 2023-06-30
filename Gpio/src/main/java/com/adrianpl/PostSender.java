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
    public String sendPostRequest(String URL, HashMap<String, String> body) throws Exception {
        try {
            var uri = new URI(URL + "test");
            var jsonBody = new Gson().toJson(body);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-type", "application/json")
                    .POST(BodyPublishers.ofString(jsonBody))
                    .build();

            var response = client.send(request, BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new Exception("There was a problem while sending POST request");
        } catch (URISyntaxException e) {
            throw new Exception("There was a problem in uri string");
        }
    }
}
