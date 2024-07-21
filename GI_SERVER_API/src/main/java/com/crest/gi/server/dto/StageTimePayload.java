package com.crest.gi.server.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class StageTimePayload {
    List<UUID> integrationId;
    Long startDate;
    Long endDate;
    boolean isFirstReview;
    List<String>filters;
    List<String> stages;
}
