package com.crest.gi.server.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * This method is used for taking response body from frontend
 */
@Data
public class QueryDto {
    List<UUID> integrationId;
    Long startDate;
    Long endDate;
}
