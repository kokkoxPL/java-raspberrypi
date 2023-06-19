package com.adrianpl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;

public class GpioController {
    final int TIME_TO_START_TOGGLE = 1000;
    final int TIME_TO_START_CHECK = 5000;
    final int TIME_TO_TOGGLE = 8000;
    final int TIME_TO_CHECK = 5000;
    final Console console;

    private SnmpController snmpController;
    private ModbusTCPServer modbusTCPServer;
    private int gpioAddress;
    private boolean didGpioStateChange = false;

    public GpioController(SnmpController snmpController, ModbusTCPServer modbusTCPServer, int gpioAddress) {
        this.console = new Console();
        this.snmpController = snmpController;
        this.modbusTCPServer = modbusTCPServer;
        this.gpioAddress = gpioAddress;
    }

    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        System.out.println(builder.toString());
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    public void sendPostRequest(String value) throws IOException, InterruptedException {
        var values = new HashMap<Object, Object>();
        values.put("value", value);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/test"))
                .header("Content-Type", "application/json")
                .POST(buildFormDataFromMap(values))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        console.box(response.body());
    }

    public void restart() throws IOException, InterruptedException {
        console.box("RESTART");
        sendPostRequest("0");
        snmpController.sendResponse();
        modbusTCPServer.writeHoldingRegisters(0, 123);
    }

    public void testGpio() {
        console.title("<-- The GPIO Project -->");

        Context pi4j = Pi4J.newAutoContext();

        DigitalOutput output = pi4j.dout().create(gpioAddress);
        output.config().shutdownState(DigitalState.LOW);

        output.addListener(e -> {
            console.print(e);
            didGpioStateChange = true;
        });

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

        Runnable toggleGpio = () -> {
            output.toggle();
        };

        Runnable checkGpioState = () -> {
            if (didGpioStateChange) {
                didGpioStateChange = false;
                console.box("OK");
                return;
            }

            try {
                restart();
                executorService.shutdown();
                pi4j.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        executorService.scheduleAtFixedRate(toggleGpio, TIME_TO_START_TOGGLE, TIME_TO_TOGGLE, TimeUnit.MILLISECONDS);
        executorService.scheduleAtFixedRate(checkGpioState, TIME_TO_START_CHECK, TIME_TO_CHECK, TimeUnit.MILLISECONDS);
    }
}
