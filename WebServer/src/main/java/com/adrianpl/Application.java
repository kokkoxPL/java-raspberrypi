package com.adrianpl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@SpringBootApplication
@RestController
public class Application {
	// DEFAULT VALUES

	// MODBUS
	private static int MODBUS_PORT = 502;

	// SNMP
	private static String SNMP_ADDRESS = "udp:127.0.0.1/161";
	private static String VARIABLE_BINDING = "1.3.6.1.2.1.1.2";

	private static SnmpController snmpController;
	private static ModbusTCPServer modbusTCPServer;

	public static void main(String[] args) {
		loadProperties();
		modbusTCPServer = new ModbusTCPServer(MODBUS_PORT);
		snmpController = new SnmpController(SNMP_ADDRESS, VARIABLE_BINDING);
		SpringApplication.run(Application.class, args);
	}

	@PostMapping("/test")
	public ResponseEntity<String> test(@RequestBody String jsonString) {
		JsonObject data = new Gson().fromJson(jsonString, JsonObject.class);
		var error = data.get("error").getAsBoolean();

		if (error) {
			modbusTCPServer.writeHoldingRegisters(1, 321);
			snmpController.sendResponse();
			return ResponseEntity.status(400).body("oh no");
		}
		return ResponseEntity.ok("ok");
	}

	private static void loadProperties() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("/etc/gpio-testing/web-server.properties"));
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
