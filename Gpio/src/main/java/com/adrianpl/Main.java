package com.adrianpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Main {
    // DEFAULT VALUES

    // GPIO
    private static int GPIO_ADDRESS = 24;
    private static int TIME_TO_START_TOGGLE = 1000;
    private static int TIME_TO_START_CHECK = 5000;
    private static int TIME_TO_TOGGLE = 3000;
    private static int TIME_TO_CHECK = 5000;
    private static String WEB_SERVER_URL = "http://localhost:8081/";

    // MODBUS
    private static int MODBUS_PORT = 502;

    // SNMP
    private static String SNMP_ADDRESS = "udp:127.0.0.1/161";
    private static String VARIABLE_BINDING = "1.3.6.1.2.1.1.1";

    public static void main(String[] args) {
        loadProperties();

        ModbusTCPServer modbusTCPServer = new ModbusTCPServer(MODBUS_PORT);
        SnmpController snmpController = new SnmpController(SNMP_ADDRESS, VARIABLE_BINDING);

        GpioController gpioController = new GpioController(snmpController,
                modbusTCPServer, GPIO_ADDRESS, TIME_TO_CHECK,
                TIME_TO_START_CHECK, TIME_TO_START_TOGGLE, TIME_TO_TOGGLE, WEB_SERVER_URL);
        gpioController.startGpio();
    }

    public static void loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("/etc/gpio-testing/gpio.properties"));

            GPIO_ADDRESS = Integer.parseInt(properties.getProperty("GPIO_ADDRESS"));
            TIME_TO_START_TOGGLE = Integer.parseInt(properties.getProperty("TIME_TO_START_TOGGLE"));
            TIME_TO_START_CHECK = Integer.parseInt(properties.getProperty("TIME_TO_START_CHECK"));
            TIME_TO_TOGGLE = Integer.parseInt(properties.getProperty("TIME_TO_TOGGLE"));
            TIME_TO_CHECK = Integer.parseInt(properties.getProperty("TIME_TO_CHECK"));
            WEB_SERVER_URL = properties.getProperty("WEB_SERVER_URL");
            MODBUS_PORT = Integer.parseInt(properties.getProperty("MODBUS_PORT"));
            SNMP_ADDRESS = properties.getProperty("SNMP_ADDRESS");
            VARIABLE_BINDING = properties.getProperty("VARIABLE_BINDING");
        } catch (FileNotFoundException e) {
            System.out.println("Not found application.properties file, using default properties");
        } catch (IOException e) {
            System.out.println("There was a problem loading values from application.properties file");
        } catch (NumberFormatException e) {
            System.out.println("There was a error in application.properties file");
        }
    }
}
