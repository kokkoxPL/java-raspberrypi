package com.adrianpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostMapping("/test")
	public ResponseEntity<String> test(@RequestBody String value) {
		System.out.println(value);
		String str = String.format("Hello %s!", value);
		return ResponseEntity.ok(str);
	}
}
