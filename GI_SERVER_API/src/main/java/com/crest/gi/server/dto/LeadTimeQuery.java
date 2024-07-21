package com.md.gi.server.dto;

import java.util.List;
import lombok.Data;

@Data
public class LeadTimeQuery {
    Long startDate;
    Long endDate;
    boolean isFirstReview;
    List<String> stages;
    List<String> filters;
}

