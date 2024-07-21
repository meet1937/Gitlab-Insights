package com.md.gi.server.service;

import com.md.gi.server.dao.WidgetDao;
import com.md.gi.server.dto.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** This class provide various services for gitlab integration */
@Service
@Slf4j
public class WidgetService {
  @Autowired
  WidgetDao widgetDao;

  String[] leadTimeStages = {"mr_created", "mr_review", "mr_approval", "mr_merged"};

  /**
   * This method will return the mr raised by each developer
   *
   * @param queryDto it will provide the start and end time for which we return the result
   * @return List of merge raised by each developer in particular time
   */
  public List<MrRaisedByDeveloper> getMrRaisedByDeveloper(QueryDto queryDto) {
    return widgetDao.mrRaisedByDevelopers(
            queryDto.getStartDate(), queryDto.getEndDate(), queryDto.getIntegrationId().get(0));
  }

  public List<MrRaisedAndMrMergedByDeveloper> getMrRaisedAndMrMregedByDeveloper(
          FilterQueryDto queryDto) {

    return widgetDao.mrRaisedAndMrMregedByDevelopers(
            queryDto.getStartDate(), queryDto.getEndDate(), queryDto.getIntegrationId().get(0));
  }

  /**
   * Method to get no of MR merged over day week and month
   * @param queryDto it will provide the start and end time for which we return the result
   * @return MR merged over day week and month
   */
  public MrMergedOverTime getMrMergedOverTime(QueryDto queryDto) {
    MrMergedOverTime result = new MrMergedOverTime();
    Long startDateEpoch = queryDto.getStartDate();
    Long endDateEpoch = queryDto.getEndDate();
    List<LocalDateTime> mergedAtTimeList =
            widgetDao.fetchMrMergedTime(queryDto.getIntegrationId().get(0), startDateEpoch, endDateEpoch);
    result.setDay(widgetDao.getMrMergedOverDay(startDateEpoch, endDateEpoch, mergedAtTimeList));
    result.setWeek(widgetDao.getMrMergedOverWeek(startDateEpoch, endDateEpoch, mergedAtTimeList));
    result.setMonth(widgetDao.getMrMergedOverMonth(startDateEpoch, endDateEpoch, mergedAtTimeList));
    return result;
  }

  /**
   * Method to fetch MR raised vs merged over day week and month
   * @param queryDto it will provide the start and end time for which we return the result
   * @return MR raised vs merged over day week and month
   */
  public MrRaisedVsMergedOverTime getMrRaisedVsMergedOverTime(
          FilterQueryDto queryDto) {
    MrRaisedVsMergedOverTime result = new MrRaisedVsMergedOverTime();
    Long startDateEpoch = queryDto.getStartDate();
    Long endDateEpoch = queryDto.getEndDate();

    List<LocalDateTime> mergedAtTimeList =
            widgetDao.fetchMrMergedTime(queryDto.getIntegrationId().get(0), startDateEpoch, endDateEpoch);
    List<LocalDateTime> raisedAtTimeList =
            widgetDao.fetchMrRaisedTime(queryDto.getIntegrationId().get(0), startDateEpoch, endDateEpoch);

    result.setDay(
            widgetDao.getMrRaisedVsMergedOverDay(
                    startDateEpoch, endDateEpoch, mergedAtTimeList, raisedAtTimeList));
    result.setWeek(
            widgetDao.getMrRaisedVsMergedOverWeek(
                    startDateEpoch, endDateEpoch, mergedAtTimeList, raisedAtTimeList));
    result.setMonth(
            widgetDao.getMrRaisedVsMergedOverMonth(
                    startDateEpoch, endDateEpoch, mergedAtTimeList, raisedAtTimeList));
    return result;
  }

  public Map<String, Double> getLeadTimeReport(
          StageTimePayload stageTimePayload) {
    Map<String, Double> map = new HashMap<>();
    Map<String, Double> result =
            widgetDao.leadTimeQuery(
                    stageTimePayload.isFirstReview(),
                    stageTimePayload.getIntegrationId().get(0),
                    stageTimePayload.getFilters(),
                    stageTimePayload.getStartDate(),
                    stageTimePayload.getEndDate());
    Double temp = 0d;
    for (String i : leadTimeStages) {
      if (stageTimePayload.getStages().contains(i)) {
        map.put(i, temp + result.get(i));
        temp = 0d;
      }
      else{
        temp = temp + result.get(i);
      }
    }
    return map;
  }

  public Map<String, Double> getStageTime(StageTimePayload stageTimePayload) {
    System.out.println(stageTimePayload);
    Map<String, Double> result =
            widgetDao.stageTimeQuery(
                    stageTimePayload.isFirstReview(),
                    stageTimePayload.getIntegrationId().get(0),
                    stageTimePayload.getFilters(),
                    stageTimePayload.getStartDate(),
                    stageTimePayload.getEndDate()
            );

    return stageTimePayload.getStages().stream()
            .filter(stage -> result.containsKey(stage) && stageTimePayload.getStages().contains(stage))
            .collect(Collectors.toMap(stage -> stage, result::get));
  }
}
