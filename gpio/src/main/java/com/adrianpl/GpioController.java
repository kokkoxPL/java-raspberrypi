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
    private final int TIME_TO_START_TOGGLE = 1000;
    private final int TIME_TO_START_CHECK = 5000;
    private final int TIME_TO_TOGGLE = 8000;
    private final int TIME_TO_CHECK = 5000;
    private final Console console = new Console();

    private final int gpioAddress;
    private final SnmpController snmpController;
    private final ModbusTCPServer modbusTCPServer;

    private boolean didGpioStateChange = false;

    public GpioController(SnmpController snmpController, ModbusTCPServer modbusTCPServer, int gpioAddress) {
        this.snmpController = snmpController;
        this.modbusTCPServer = modbusTCPServer;
        this.gpioAddress = gpioAddress;
    }

    public void restart() {
        console.box("RESTART");

        var map = new HashMap<String, String>();
        map.put("error", "true");

        modbusTCPServer.writeHoldingRegisters(0, 123);
        snmpController.sendResponse();

        try {
            var body = new PostSender().sendPostRequest(map);
            console.print(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGpio() {
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

                var map = new HashMap<String, String>();
                map.put("error", "false");

                try {
                    var body = new PostSender().sendPostRequest(map);
                    console.print(body);
                } catch (Exception e) {
                    e.printStackTrace();
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
