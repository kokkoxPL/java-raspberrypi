package com.adrianpl;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.pi4j.util.Console;

public class PostSender {
    private Console console;

    public PostSender(Console console) {
        this.console = console;
    }

    public void sendPostRequest(String value) throws IOException, InterruptedException {
        var values = new HashMap<Object, Object>();
        values.put("value", value);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/test"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(buildFormDataFromMap(values))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        console.box(response.body());
    }

    private HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        console.print(builder.toString());
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
