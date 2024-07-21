package com.crest.gi.scheduler.service;

import com.crest.gi.scheduler.dto.IntegrationDto;
import com.crest.gi.scheduler.enums.Status;
import com.crest.gi.scheduler.mapper.Mapper;
import com.crest.gi.scheduler.model.Integration;
import com.crest.gi.scheduler.repository.IntegrationRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** IntegerationCredentials Crud operations service */
@Service
public class IntegrationService {

  public static final int period = 1;
  static HashMap<UUID, LocalDateTime> servicesWithTime = new HashMap<>();
  IntegrationRepository integrationRepository;
  Base64.Encoder encoder = Base64.getEncoder();
  Base64.Decoder decoder = Base64.getDecoder();

  @Autowired
  public IntegrationService(IntegrationRepository integrationRepository) {
    this.integrationRepository = integrationRepository;
  }

  /**
   * For adding credentials to the database
   *
   * @param integrationDto
   */
  public UUID addCredential(IntegrationDto integrationDto) {
    if (servicesWithTime != null) {
      getServicesWithTime();
    }
    if (servicesWithTime.size() >= 1) {
      throw new IllegalArgumentException("Already have a Integration");
    }
    Integration integration = Mapper.getIntegrationCredential(integrationDto);
    integration.setApiKey(encoder.encodeToString(integration.getApiKey().getBytes()));
    integration.setId(UUID.randomUUID());
    integrationRepository.save(integration);
    servicesWithTime.put(integration.getId(), integration.getLastExecuted());
    return integration.getId();
  }

  /**
   * Getting the integration credentials from db
   *
   * @param id
   * @return
   */
  public Integration getIntegerationCredentialById(UUID id) {
    Integration integration = integrationRepository.findByIdAndStatus(id, Status.LIVE);
    if (integration != null) {
      integration.setApiKey(new String(decoder.decode(integration.getApiKey())));
    }
    return integration;
  }

  /**
   * Returning all integrations
   *
   * @return
   */
  public List<Integration> getAllIntegration() {
    List<Integration> getAllIntegrationList = integrationRepository.findAll();
    getAllIntegrationList.forEach(i -> i.setApiKey(new String(decoder.decode(i.getApiKey()))));
    return getAllIntegrationList;
  }

  /**
   * Updating the credentials api key
   *
   * @param integration
   */
  public void updateIntegerationCredential(Integration integration) {
    if (integration.getStatus().equals(Status.CLOSED)) {
      servicesWithTime.remove(integration.getId());
    } else {
      servicesWithTime.put(integration.getId(), LocalDateTime.now(ZoneOffset.UTC));
    }
    integration.setApiKey(encoder.encodeToString(integration.getApiKey().getBytes()));
    integrationRepository.save(integration);
  }

  /**
   * If the IntegrationDto will come than this update method will be called
   *
   * @param integrationDto
   */
  public void updateIntegerationHandlerHelper(IntegrationDto integrationDto) {
    Integration integration = getIntegerationCredentialById(integrationDto.getId());
    if (!integrationDto.getApiKey().equals(integration.getApiKey())) {
      integration.setStatus(Status.LIVE);
    } else if (integrationDto.getUrl() != null
        && !integrationDto.getUrl().equals(integration.getUrl())) {
      integration.setStatus(Status.LIVE);
    }
    updateIntegerationCredential(integration);
  }

  /**
   * method checking if any service are completed one hour then return that controller for execution
   *
   * @return Map<String , LocalDateTime>
   */
  public Map<UUID, LocalDateTime> checkForExecution() {
    if (servicesWithTime == null || servicesWithTime.isEmpty()) {
      getServicesWithTime();
    }
    Map<UUID, LocalDateTime> result = new HashMap<>();
    for (Map.Entry<UUID, LocalDateTime> entry : servicesWithTime.entrySet()) {
      if (LocalDateTime.now(ZoneOffset.UTC).equals(entry.getValue())
          || entry
              .getValue()
              .isBefore(LocalDateTime.now(ZoneOffset.UTC).minusHours(period).minusSeconds(20))) {
        result.put(entry.getKey(), entry.getValue());
      }
    }
    return result;
  }

  /** getting all service data and time entries for execute periodically */
  private void getServicesWithTime() {
    List<Integration> integrationList = integrationRepository.findByStatus(Status.LIVE);
    integrationList.forEach(
        data -> servicesWithTime.put(data.getId(), LocalDateTime.now(ZoneOffset.UTC)));
  }
}
