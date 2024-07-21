package com.md.gi.scheduler.mapper;

import com.md.gi.scheduler.dto.IntegrationDto;
import com.md.gi.scheduler.enums.Status;
import com.md.gi.scheduler.model.Integration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;

/** Mapper class for dto to model */
@Service
public class Mapper {
  /**
   * To convert Integration credentials dto to Integration credentials Model
   *
   * @param integrationDto
   * @return
   */
  public static Integration getIntegrationCredential(IntegrationDto integrationDto) {
    return Integration.builder()
        .url(integrationDto.getUrl())
        .apiKey(integrationDto.getApiKey())
        .controller(integrationDto.getController())
        .status(Status.LIVE)
        .schemaName(integrationDto.getSchemaName())
        .lastExecuted(LocalDateTime.now(ZoneOffset.UTC).minusDays(90))
        .build();
  }
}
