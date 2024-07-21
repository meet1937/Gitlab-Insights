package com.crest.gi.server.controller;

import com.crest.gi.server.dto.*;
import com.crest.gi.server.service.WidgetService;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/widget/")
public class WidgetController {
  @Autowired WidgetService widgetService;

  @PostMapping("mr-raised-by-user")
  public ResponseEntity<List<MrRaisedByDeveloper>> mrRaisedByEachDeveloper(
          @RequestBody QueryDto queryDto) {
    return ResponseEntity.ok(widgetService.getMrRaisedByDeveloper(queryDto));
  }

  @PostMapping("mr-merged-over-time")
  public ResponseEntity<MrMergedOverTime> getMrMergedOverTime(@RequestBody QueryDto queryDto) {
    return ResponseEntity.ok(widgetService.getMrMergedOverTime(queryDto));
  }

  @PostMapping("mr-raised-vs-merged")
  public ResponseEntity getMrRaisedVsMergedComparison(@RequestBody FilterQueryDto filterQueryDto) {
    if ("timeline".equals(filterQueryDto.getFilter())) {
      return ResponseEntity.ok(widgetService.getMrRaisedVsMergedOverTime(filterQueryDto));

    } else if ("user".equals(filterQueryDto.getFilter())) {
      return ResponseEntity.ok(widgetService.getMrRaisedAndMrMregedByDeveloper(filterQueryDto));

    } else {
      return ResponseEntity.badRequest().body("Invalid Filter");
    }
  }

  @PostMapping("get-lead-time-report")
  public ResponseEntity getLeadTimeReport(@RequestBody StageTimePayload stageTimePayload) {
    return ResponseEntity.ok(widgetService.getLeadTimeReport(stageTimePayload));
  }

  @PostMapping("stage-time")
  public Map<String, Double> getStageTime(@RequestBody StageTimePayload stageTimePayload) {
    return widgetService.getStageTime(stageTimePayload);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> exceptionHandler(Exception exception) {
    return ResponseEntity.badRequest().body(exception.getMessage());
  }
}
