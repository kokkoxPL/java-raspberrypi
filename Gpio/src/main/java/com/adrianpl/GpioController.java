package com.adrianpl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.concurrent.Executors;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;

public class GpioController {
    private final int TIME_TO_START_TOGGLE;
    private final int TIME_TO_START_CHECK;
    private final int TIME_TO_TOGGLE;
    private final int TIME_TO_CHECK;
    private final int GPIO_ADDRESS;
    private final String WEB_SERVER_URL;

    private SnmpController snmpController;
    private ModbusTCPServer modbusTCPServer;

    private Console console = new Console();
    private boolean didGpioStateChange = false;

    // Wiem to jest brzydkie
    public GpioController(SnmpController snmpController, ModbusTCPServer modbusTCPServer, int GPIO_ADDRESS,
            int TIME_TO_CHECK, int TIME_TO_START_CHECK, int TIME_TO_START_TOGGLE, int TIME_TO_TOGGLE,
            String WEB_SERVER_URL) {
        this.GPIO_ADDRESS = GPIO_ADDRESS;
        this.TIME_TO_CHECK = TIME_TO_CHECK;
        this.TIME_TO_START_CHECK = TIME_TO_START_CHECK;
        this.TIME_TO_START_TOGGLE = TIME_TO_START_TOGGLE;
        this.TIME_TO_TOGGLE = TIME_TO_TOGGLE;
        this.WEB_SERVER_URL = WEB_SERVER_URL;

        this.snmpController = snmpController;
        this.modbusTCPServer = modbusTCPServer;
    }

    public void restart() {
        console.box("RESTART");

        var map = new HashMap<String, String>();
        map.put("error", "true");

        modbusTCPServer.writeHoldingRegisters(0, 123);
        snmpController.sendResponse();

        try {
            var body = new PostSender().sendPostRequest(WEB_SERVER_URL, map);
            console.print(body);
        } catch (Exception e) {
            console.print(e);
        }
    }

    public void startGpio() {
        console.title("<-- The GPIO Project -->");

        Context pi4j = Pi4J.newAutoContext();

        DigitalOutput output = pi4j.dout().create(GPIO_ADDRESS);
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

                var map = new HashMap<String, String>();
                map.put("error", "false");

                try {
                    var body = new PostSender().sendPostRequest(WEB_SERVER_URL, map);
                    console.print(body);
                } catch (Exception e) {
                    console.print(e);
                }
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
