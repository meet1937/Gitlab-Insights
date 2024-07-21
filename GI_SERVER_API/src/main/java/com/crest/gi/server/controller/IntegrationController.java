package com.md.gi.server.controller;


import com.md.gi.server.dto.IntegrationDto;
import java.util.List;
import java.util.UUID;

import com.md.gi.utils.schemaSetup.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class IntegrationController {
  @Value("${scheduler_url}")
  String url;

  @Autowired
  WebClient webClient;

  @Autowired
  DbService dbService;

  @PostMapping("register-integration")
  public ResponseEntity<UUID> addIntegration(@RequestBody IntegrationDto integrationDto) {
    dbService.setupSchema(integrationDto.getSchemaName());
    return ResponseEntity.ok(
            webClient
                    .post()
                    .uri(url+"register-credentials")
                    .bodyValue(integrationDto)
                    .retrieve()
                    .toEntity(UUID.class)
                    .block()
                    .getBody());
  }

  @PutMapping("update-integration")
  public ResponseEntity<String> updateCredentials(@RequestBody IntegrationDto integrationDto) {
    return ResponseEntity.ok(
            webClient
                    .post()
                    .uri(url+"update-credentials")
                    .bodyValue(integrationDto)
                    .retrieve()
                    .toEntity(String.class)
                    .block()
                    .getBody());
  }

  @GetMapping("get-all-integrations")
  public ResponseEntity<List<?>> getAllIntegration(){
    return ResponseEntity.ok(
            webClient
                    .get()
                    .uri(url+"get-all-integration")
                    .retrieve()
                    .toEntity(List.class)
                    .block()
                    .getBody()
    );
  }
}
