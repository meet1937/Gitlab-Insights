package com.md.gi.server.dto;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class FilterQueryDto {
    List<UUID> integrationId;
    Long startDate;
    Long endDate;
    String filter;
}
