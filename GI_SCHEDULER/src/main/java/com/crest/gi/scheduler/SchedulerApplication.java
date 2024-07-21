package com.crest.gi.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.crest.gi"})
@Slf4j
public class SchedulerApplication {
  public static void main(String[] args) {
    SpringApplication.run(SchedulerApplication.class, args);
    log.info("Application is running");
  }
}
