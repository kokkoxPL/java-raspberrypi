package com.adrianpl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;

public class PostSender {
    public static String sendPostRequest(HashMap<String, String> values) throws Exception {
        try {
            URI uri = new URI("http://localhost:8081/test");
            var body = hashMapToString(values);

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

    private static String hashMapToString(HashMap<String, String> mapOfValues) {
        var str = "{";
        for (var values : mapOfValues.entrySet()) {
            str += String.format("\"%s\": \"%s\",", values.getKey(), values.getValue());
        }
        str = str.substring(0, str.length() - 1);
        str += "}";
        return str;
    }
}
