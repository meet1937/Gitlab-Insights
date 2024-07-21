package com.crest.gi.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.crest.gi.utils","com.crest.gi"})
public class GiServerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiServerApiApplication.class, args);
	}

}
