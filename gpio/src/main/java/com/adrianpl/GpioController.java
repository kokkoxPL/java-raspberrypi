package com.adrianpl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

public class GpioController {
    final int TIME_TO_START = 5000;
    final int TIME_TO_CHECK = 5000;
    private boolean didGpioStateChange = false;

    private SnmpController snmpController;
    private ModbusTCPServer modbusTCPServer;
    private int gpioAddress;

    public GpioController(SnmpController snmpController, ModbusTCPServer modbusTCPServer, int gpioAddress) {
        this.snmpController = snmpController;
        this.modbusTCPServer = modbusTCPServer;
        this.gpioAddress = gpioAddress;
    }

    public void restart() throws Exception {
        System.out.println("restart");
        snmpController.sendResponse();
        modbusTCPServer.writeHoldingRegisters(0, 123);
    }

    public void sendPostRequest() throws Exception {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost httppost = new HttpPost("http://localhost:8081/test");

        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("value", "test"));
        httppost.setEntity(new UrlEncodedFormEntity(params));
        @SuppressWarnings("deprecation")
        CloseableHttpResponse response = httpclient.execute(httppost);
        System.out.println(EntityUtils.toString(response.getEntity()));
        response.close();
        httpclient.close();

    }

    public void testGpio() throws Exception {
        final Console console = new Console();
        console.title("<-- The GPIO Project -->");

        var pi4j = Pi4J.newAutoContext();

        var output = pi4j.dout().create(gpioAddress);
        output.config().shutdownState(DigitalState.LOW);

        output.addListener(e -> {
            System.out.println(e);
            didGpioStateChange = true;
        });

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        Runnable checkGpioRunnable = () -> {
            if (didGpioStateChange) {
                didGpioStateChange = false;
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

        executorService.scheduleAtFixedRate(checkGpioRunnable, TIME_TO_START, TIME_TO_CHECK, TimeUnit.MILLISECONDS);

        var n = 0;
        while (n < 8) {
            n++;
            output.toggle();
            TimeUnit.MILLISECONDS.sleep(1000);
        }
    }
}
