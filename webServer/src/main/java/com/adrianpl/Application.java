package com.adrianpl;

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
	static SnmpController snmpController;
	static ModbusTCPServer modbusTCPServer;

	public static void main(String[] args) {
		snmpController = new SnmpController();
		modbusTCPServer = new ModbusTCPServer();
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
}
