package com.tomtom.scoop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)

public class ScoopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScoopApplication.class, args);
	}

}
