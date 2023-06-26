package com.adrianpl;

public class Main {
    public static void main(String[] args) {
        SnmpController snmpController = new SnmpController();
        ModbusTCPServer modbusTCPServer = new ModbusTCPServer();

        GpioController gpioController = new GpioController(snmpController, modbusTCPServer, 24);
        gpioController.startGpio();
    }
}