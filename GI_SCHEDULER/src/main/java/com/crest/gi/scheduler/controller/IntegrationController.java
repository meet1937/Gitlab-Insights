package com.md.gi.scheduler.controller;

import com.md.gi.scheduler.dto.IntegrationDto;
import com.md.gi.scheduler.model.Integration;
import com.md.gi.scheduler.service.ClassMethodInvokeService;
import com.md.gi.scheduler.service.IntegrationService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controller for crud operation of integeration credentials */
@RestController
@RequestMapping("/api/v1")
public class IntegrationController {

  IntegrationService integrationService;
  ClassMethodInvokeService classMethodInvokeService;

  public IntegrationController(
      IntegrationService integrationService, ClassMethodInvokeService classMethodInvokeService) {
    this.integrationService = integrationService;
    this.classMethodInvokeService = classMethodInvokeService;
  }

  /**
   * For registering the integeration credential into database
   *
   * @param integerationCredential
   * @return
   */
  @PostMapping("/register-credentials")
  public ResponseEntity<UUID> registerCredentials(
      @RequestBody IntegrationDto integerationCredential) {
    UUID integrationId = integrationService.addCredential(integerationCredential);
    return ResponseEntity.ok(integrationId);
  }

  /**
   * For update credentials in db
   *
   * @param integrationDto
   * @return
   */
  @PutMapping("/update-credentials")
  public ResponseEntity<String> updateCredentials(@RequestBody IntegrationDto integrationDto) {
    integrationService.updateIntegerationHandlerHelper(integrationDto);
    return ResponseEntity.ok("Credential Updated Successfully");
  }

  /**
   * For getting all integrations
   *
   * @return List of all integrations
   */
  @GetMapping("/get-all-integrations")
  public ResponseEntity<List<Integration>> getAllIntegrations() {
    return ResponseEntity.ok(integrationService.getAllIntegration());
  }
}
