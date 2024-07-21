package com.md.gi.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.md.gi.utils","com.md.gi"})
public class GiServerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiServerApiApplication.class, args);
	}

}
