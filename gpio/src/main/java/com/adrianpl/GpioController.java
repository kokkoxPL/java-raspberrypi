package com.adrianpl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
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

    public void restart() throws IOException, InterruptedException {
        console.box("RESTART");

        PostSender postSender = new PostSender(console);
        postSender.sendPostRequest("World");

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
