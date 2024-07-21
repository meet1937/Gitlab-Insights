package com.md.gi.server.dao;

import com.md.gi.server.dto.Merged;
import com.md.gi.server.dto.MrRaisedAndMrMergedByDeveloper;
import com.md.gi.server.dto.MrRaisedByDeveloper;
import com.md.gi.server.dto.RaisedVsMerged;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This method will return the db query method related to gitlab
 */
@Service
@Slf4j
public class WidgetDao {
    private final String zoneId = "Asia/Kolkata";

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * This method will execute and return the list of mr raised by each developer
     *
     * @param startTime
     * @param endTime
     * @param integrationId
     * @return
     */
    public List<MrRaisedByDeveloper> mrRaisedByDevelopers(
            Long startTime, Long endTime, UUID integrationId) {
        String sql =
                "with raised_count as(\n"
                        + "SELECT count(*) as mr_raised_count,author_id FROM crest.merge_request WHERE integration_id = :integrationId AND created_at >= :startTime  AND created_at <= :endTime\n"
                        + "group by author_id\n"
                        + ")\n"
                        + "select u.username as user ,u.name as name,r.mr_raised_count as total_mr_raised from crest.users u left join raised_count r on u.id=r.author_id";
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", new Timestamp(startTime * 1000));
        params.put("endTime", new Timestamp(endTime * 1000));
        params.put("integrationId", integrationId);
        List<MrRaisedByDeveloper> mrRaisedByDevelopers =
                namedParameterJdbcTemplate.query(sql, params, new MrRaisedRowMapper());
        return mrRaisedByDevelopers;
    }

    public List<MrRaisedAndMrMergedByDeveloper> mrRaisedAndMrMregedByDevelopers(
            Long startTime, Long endTime, UUID integrationId) {
        String sql =
                "with raised_count as(\n" +
                        "SELECT count(*) as mr_raised_count,author_id FROM crest.merge_request WHERE integration_id =:integrationId  AND created_at >= :startTime  AND created_at <= :endTime\n" +
                        "group by author_id\n" +
                        "),\n" +
                        "merge_count as(\n" +
                        "SELECT count(*) as mr_merged_count,author_id FROM crest.merge_request WHERE integration_id =:integrationId  AND merged_at >= :startTime  AND merged_at <= :endTime\n" +
                        "group by author_id\n" +
                        ")\n" +
                        "select u.username as user ,u.name as name,r.mr_raised_count as total_mr_raised,m.mr_merged_count as total_mr_merged from crest.users u left join raised_count r on u.id=r.author_id left join merge_count m on u.id =m.author_id\n";
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", new Timestamp(startTime * 1000));
        params.put("endTime", new Timestamp(endTime * 1000));
        params.put("integrationId", integrationId);
        List<MrRaisedAndMrMergedByDeveloper> mrRaisedAndMrMergedByDevelopers =
                namedParameterJdbcTemplate.query(sql, params, new MrRaisedAndMrMergeMapper());
        return mrRaisedAndMrMergedByDevelopers;
    }

    /**
     * Retrieves the count of merge requests merged for each day within a specified time range.
     *
     * @param startDateEpoch The starting epoch timestamp.
     * @param endDateEpoch   The ending epoch timestamp.
     * @return A list of Day objects representing merge request counts for each day.
     */
    public List<Merged> getMrMergedOverDay(
            long startDateEpoch, long endDateEpoch, List<LocalDateTime> mergedAtTimeList) {
        Map<LocalDate, Long> countsMap =
                mergedAtTimeList.stream()
                        .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, Collectors.counting()));
        return generateDayListForMerged(startDateEpoch, endDateEpoch, countsMap);
    }

    /**
     * Retrieves the count of merge requests merged for each week within a specified time range.
     *
     * @param startDateEpoch The starting epoch timestamp.
     * @param endDateEpoch   The ending epoch timestamp.
     * @return A list of Week objects representing merge request counts for each week.
     */
    public List<Merged> getMrMergedOverWeek(
            long startDateEpoch, long endDateEpoch, List<LocalDateTime> mergedAtTimeList) {
        Map<LocalDate, Long> countsMap =
                mergedAtTimeList.stream()
                        .collect(
                                Collectors.groupingBy(
                                        record -> record.with(DayOfWeek.MONDAY).toLocalDate(), Collectors.counting()));
        return generateWeekListForMerged(startDateEpoch, endDateEpoch, countsMap);
    }

    /**
     * Retrieves the count of merge requests merged for each month within a specified time range.
     *
     * @param startDateEpoch The starting epoch timestamp.
     * @param endDateEpoch   The ending epoch timestamp.
     * @return A list of Month objects representing merge request counts for each month.
     */
    public List<Merged> getMrMergedOverMonth(
            long startDateEpoch, long endDateEpoch, List<LocalDateTime> mergedAtTimeList) {
        Map<YearMonth, Long> countsMap =
                mergedAtTimeList.stream()
                        .collect(Collectors.groupingBy(YearMonth::from, Collectors.counting()));
        return generateMonthListForMerged(startDateEpoch, endDateEpoch, countsMap);
    }

    /**
     * Generates a list of MergeDay objects from the given counts map.
     *
     * @param startDateEpoch The starting epoch timestamp.
     * @param endDateEpoch   The ending epoch timestamp.
     * @param countsMap      The map containing counts of merge requests for each day.
     * @return A list of Day objects representing merge request counts for each day.
     */
    private List<Merged> generateDayListForMerged(
            long startDateEpoch, long endDateEpoch, Map<LocalDate, Long> countsMap) {
        LocalDate startDate =
                Instant.ofEpochSecond(startDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();
        LocalDate endDate = Instant.ofEpochSecond(endDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();
        List<LocalDate> dateRange = startDate.datesUntil(endDate.plusDays(1)).toList();

        return dateRange.stream()
                .map(
                        date -> {
                            long startOfDayEpoch =
                                    date.atStartOfDay(ZoneId.of(zoneId)).toInstant().getEpochSecond();
                            int count = countsMap.getOrDefault(date, 0L).intValue();
                            return new Merged(startOfDayEpoch, count);
                        })
                .toList();
    }

    /**
     * Generates a list of MergeWeek objects from the given counts map.
     *
     * @param startDateEpoch The starting epoch timestamp.
     * @param endDateEpoch   The ending epoch timestamp.
     * @param countsMap      The map containing counts of merge requests for each week.
     * @return A list of Week objects representing merge request counts for each week.
     */
    private List<Merged> generateWeekListForMerged(
            long startDateEpoch, long endDateEpoch, Map<LocalDate, Long> countsMap) {
        LocalDate startDate =
                Instant.ofEpochSecond(startDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();
        LocalDate endDate = Instant.ofEpochSecond(endDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();
        LocalDate startMonday = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endMonday = endDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        return Stream.iterate(startMonday, date -> date.plusWeeks(1))
                .limit(ChronoUnit.WEEKS.between(startMonday, endMonday.plusWeeks(1)))
                .map(
                        monday -> {
                            long startOfWeekEpoch =
                                    monday.atStartOfDay(ZoneId.of(zoneId)).toInstant().getEpochSecond();
                            int count = countsMap.getOrDefault(monday, 0L).intValue();
                            return new Merged(startOfWeekEpoch, count);
                        })
                .collect(Collectors.toList());
    }

    /**
     * Generates a list of MergeMonth objects from the given counts map.
     *
     * @param startDateEpoch The starting epoch timestamp.
     * @param endDateEpoch   The ending epoch timestamp.
     * @param countsMap      The map containing counts of merge requests for each month.
     * @return A list of Month objects representing merge request counts for each month.
     */
    private List<Merged> generateMonthListForMerged(
            long startDateEpoch, long endDateEpoch, Map<YearMonth, Long> countsMap) {
        LocalDate startDate =
                Instant.ofEpochSecond(startDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();
        LocalDate endDate = Instant.ofEpochSecond(endDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();

        Stream<YearMonth> yearMonthStream =
                Stream.iterate(
                        YearMonth.from(startDate),
                        ym -> !ym.isAfter(YearMonth.from(endDate)),
                        ym -> ym.plusMonths(1));

        return yearMonthStream
                .map(
                        ym -> {
                            long startOfMonthEpoch =
                                    ym.atDay(1).atStartOfDay(ZoneId.of(zoneId)).toInstant().getEpochSecond();
                            int count = countsMap.getOrDefault(ym, 0L).intValue();
                            return new Merged(startOfMonthEpoch, count);
                        })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the count of merge requests for each day within a specified time range.
     *
     * @param startDateEpoch The starting epoch timestamp.
     * @param endDateEpoch   The ending epoch timestamp.
     * @return A list of Day objects representing merge request counts for each day.
     */
    public List<RaisedVsMerged> getMrRaisedVsMergedOverDay(
            long startDateEpoch,
            long endDateEpoch,
            List<LocalDateTime> mergedAtTimeList,
            List<LocalDateTime> raisedAtTimeList) {
        Map<LocalDate, Long> mergedCountsMap =
                mergedAtTimeList.stream()
                        .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, Collectors.counting()));
        Map<LocalDate, Long> raisedCountsMap =
                raisedAtTimeList.stream()
                        .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, Collectors.counting()));
        return generateDayListForMergedVsRaised(
                startDateEpoch, endDateEpoch, mergedCountsMap, raisedCountsMap);
    }

    /**
     * Retrieves the count of merge requests for each week within a specified time range.
     *
     * @param startDateEpoch The starting epoch timestamp.
     * @param endDateEpoch   The ending epoch timestamp.
     * @return A list of Week objects representing merge request counts for each week.
     */
    public List<RaisedVsMerged> getMrRaisedVsMergedOverWeek(
            long startDateEpoch,
            long endDateEpoch,
            List<LocalDateTime> mergedAtTimeList,
            List<LocalDateTime> raisedAtTimeList) {
        Map<LocalDate, Long> mergedCountsMap =
                mergedAtTimeList.stream()
                        .collect(
                                Collectors.groupingBy(
                                        record -> record.with(DayOfWeek.MONDAY).toLocalDate(), Collectors.counting()));
        Map<LocalDate, Long> raisedCountsMap =
                raisedAtTimeList.stream()
                        .collect(
                                Collectors.groupingBy(
                                        record -> record.with(DayOfWeek.MONDAY).toLocalDate(), Collectors.counting()));
        return generateWeekListForMergedVsRaised(
                startDateEpoch, endDateEpoch, mergedCountsMap, raisedCountsMap);
    }

    /**
     * Retrieves the count of merge requests for each month within a specified time range.
     *
     * @param startDateEpoch The starting epoch timestamp.
     * @param endDateEpoch   The ending epoch timestamp.
     * @return A list of Month objects representing merge request counts for each month.
     */
    public List<RaisedVsMerged> getMrRaisedVsMergedOverMonth(
            long startDateEpoch,
            long endDateEpoch,
            List<LocalDateTime> mergedAtTimeList,
            List<LocalDateTime> raisedAtTimeList) {
        Map<YearMonth, Long> mergedCountsMap =
                mergedAtTimeList.stream()
                        .collect(Collectors.groupingBy(YearMonth::from, Collectors.counting()));
        Map<YearMonth, Long> raisedCountsMap =
                raisedAtTimeList.stream()
                        .collect(Collectors.groupingBy(YearMonth::from, Collectors.counting()));
        return generateMonthListForMergedVsRaised(
                startDateEpoch, endDateEpoch, mergedCountsMap, raisedCountsMap);
    }

    /**
     * Fetches mergedAt time list from the database.
     *
     * @param integrationId  The UUID of the integration.
     * @param startDateEpoch The starting epoch timestamp.
     * @param endDateEpoch   The ending epoch timestamp.
     * @return A list of LocalDateTime objects representing merge request timestamps.
     */
    public List<LocalDateTime> fetchMrMergedTime(
            UUID integrationId, long startDateEpoch, long endDateEpoch) {
        String queryToFetchMergeTime =
                "SELECT merged_at FROM crest.merge_request WHERE integration_id = :integration_id AND merged_at >= :start_time AND merged_at <= :end_time";
        Map<String, Object> params = new HashMap<>();
        params.put("integration_id", integrationId);
        params.put("start_time", new Timestamp(startDateEpoch * 1000));
        params.put("end_time", new Timestamp(endDateEpoch * 1000));
        return namedParameterJdbcTemplate.queryForList(
                queryToFetchMergeTime, params, LocalDateTime.class);
    }

    /**
     * Fetches createdAt time list from the database.
     *
     * @param integrationId  The UUID of the integration.
     * @param startDateEpoch The starting epoch timestamp.
     * @param endDateEpoch   The ending epoch timestamp.
     * @return A list of LocalDateTime objects representing merge request timestamps.
     */
    public List<LocalDateTime> fetchMrRaisedTime(
            UUID integrationId, long startDateEpoch, long endDateEpoch) {
        String queryToFetchRaiseTime =
                "SELECT created_at FROM crest.merge_request WHERE integration_id = :integration_id AND created_at >= :start_time AND created_at <= :end_time";
        Map<String, Object> params = new HashMap<>();
        params.put("integration_id", integrationId);
        params.put("start_time", new Timestamp(startDateEpoch * 1000));
        params.put("end_time", new Timestamp(endDateEpoch * 1000));
        return namedParameterJdbcTemplate.queryForList(
                queryToFetchRaiseTime, params, LocalDateTime.class);
    }

    /**
     * Generates a list of Day objects from the given counts map.
     *
     * @param startDateEpoch  The starting epoch timestamp.
     * @param endDateEpoch    The ending epoch timestamp.
     * @param mergedCountsMap The map containing counts of merge requests merged for each month.
     * @param raisedCountsMap The map containing counts of merge requests raised for each month.
     * @return A list of Day objects representing merge request counts for each day.
     */
    private List<RaisedVsMerged> generateDayListForMergedVsRaised(
            long startDateEpoch,
            long endDateEpoch,
            Map<LocalDate, Long> mergedCountsMap,
            Map<LocalDate, Long> raisedCountsMap) {

        LocalDate startDate =
                Instant.ofEpochSecond(startDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();
        LocalDate endDate = Instant.ofEpochSecond(endDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();
        List<LocalDate> dateRange = startDate.datesUntil(endDate.plusDays(1)).toList();

        return dateRange.stream()
                .map(
                        date -> {
                            long startOfDayEpoch =
                                    date.atStartOfDay(ZoneId.of(zoneId)).toInstant().getEpochSecond();
                            int mergedCount = mergedCountsMap.getOrDefault(date, 0L).intValue();
                            int raisedCount = raisedCountsMap.getOrDefault(date, 0L).intValue();
                            return new RaisedVsMerged(startOfDayEpoch, raisedCount, mergedCount);
                        })
                .toList();
    }

    /**
     * Generates a list of Week objects from the given counts map.
     *
     * @param startDateEpoch  The starting epoch timestamp.
     * @param endDateEpoch    The ending epoch timestamp.
     * @param mergedCountsMap The map containing counts of merge requests merged for each month.
     * @param raisedCountsMap The map containing counts of merge requests raised for each month.
     * @return A list of Week objects representing merge request counts for each week.
     */
    private List<RaisedVsMerged> generateWeekListForMergedVsRaised(
            long startDateEpoch,
            long endDateEpoch,
            Map<LocalDate, Long> mergedCountsMap,
            Map<LocalDate, Long> raisedCountsMap) {

        LocalDate startDate =
                Instant.ofEpochSecond(startDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();
        LocalDate endDate = Instant.ofEpochSecond(endDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();
        LocalDate startMonday = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endMonday = endDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        return Stream.iterate(startMonday, date -> date.plusWeeks(1))
                .limit(ChronoUnit.WEEKS.between(startMonday, endMonday.plusWeeks(1)))
                .map(
                        monday -> {
                            long startOfWeekEpoch =
                                    monday.atStartOfDay(ZoneId.of(zoneId)).toInstant().getEpochSecond();
                            int mergedCount = mergedCountsMap.getOrDefault(monday, 0L).intValue();
                            int raisedCount = raisedCountsMap.getOrDefault(monday, 0L).intValue();
                            return new RaisedVsMerged(startOfWeekEpoch, raisedCount, mergedCount);
                        })
                .collect(Collectors.toList());
    }

    /**
     * Generates a list of Month objects from the given counts map.
     *
     * @param startDateEpoch  The starting epoch timestamp.
     * @param endDateEpoch    The ending epoch timestamp.
     * @param mergedCountsMap The map containing counts of merge requests merged for each month.
     * @param raisedCountsMap The map containing counts of merge requests raised for each month.
     * @return A list of Month objects representing merge request counts for each month.
     */
    private List<RaisedVsMerged> generateMonthListForMergedVsRaised(
            long startDateEpoch,
            long endDateEpoch,
            Map<YearMonth, Long> mergedCountsMap,
            Map<YearMonth, Long> raisedCountsMap) {
        LocalDate startDate =
                Instant.ofEpochSecond(startDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();
        LocalDate endDate = Instant.ofEpochSecond(endDateEpoch).atZone(ZoneId.of(zoneId)).toLocalDate();

        Stream<YearMonth> yearMonthStream =
                Stream.iterate(
                        YearMonth.from(startDate),
                        ym -> !ym.isAfter(YearMonth.from(endDate)),
                        ym -> ym.plusMonths(1));

        return yearMonthStream
                .map(
                        ym -> {
                            long startOfMonthEpoch =
                                    ym.atDay(1).atStartOfDay(ZoneId.of(zoneId)).toInstant().getEpochSecond();
                            int mergedCount = mergedCountsMap.getOrDefault(ym, 0L).intValue();
                            int raisedCount = raisedCountsMap.getOrDefault(ym, 0L).intValue();
                            return new RaisedVsMerged(startOfMonthEpoch, raisedCount, mergedCount);
                        })
                .collect(Collectors.toList());
    }

    /**
     * This sub class is mapper class for MrRaisedByDeveloper
     */
    private class MrRaisedRowMapper implements RowMapper<MrRaisedByDeveloper> {
        @Override
        public MrRaisedByDeveloper mapRow(ResultSet rs, int rowNum) throws SQLException {
            MrRaisedByDeveloper mrRaisedByDeveloper = new MrRaisedByDeveloper();
            mrRaisedByDeveloper.setCount(rs.getLong("total_mr_raised"));
            mrRaisedByDeveloper.setUsername(rs.getString("user"));
            mrRaisedByDeveloper.setName(rs.getString("name"));

            return mrRaisedByDeveloper;
        }
    }

    private class MrRaisedAndMrMergeMapper implements RowMapper<MrRaisedAndMrMergedByDeveloper> {

        @Override
        public MrRaisedAndMrMergedByDeveloper mapRow(ResultSet rs, int rowNum) throws SQLException {
            MrRaisedAndMrMergedByDeveloper mrCount = new MrRaisedAndMrMergedByDeveloper();
            mrCount.setRaisedCount(rs.getInt("total_mr_raised"));
            mrCount.setMergedCount(rs.getInt("total_mr_merged"));
            mrCount.setUsername(rs.getString("user"));
            mrCount.setName(rs.getString("name"));
            return mrCount;
        }
    }

    public Map<String, Double> leadTimeQuery(boolean getFirstReview, UUID integrationId, List<String> filters,
                                             Long startDateEpoch, Long endDateEpoch) {

        String sql = "WITH pr_stage_times AS (" +
                "    SELECT" +
                "        m.integration_id," +
                "        m.project_id," +
                "        m.iid," +
                "        m.created_at AS creation_time," +
                "        CASE WHEN :get_first_review THEN MIN(CASE WHEN n.target_type = 'DiffNote' THEN n.created_at END)" +
                "             ELSE MAX(CASE WHEN n.target_type = 'DiffNote' THEN n.created_at END)" +
                "        END AS review_time," +
                "        MAX(CASE WHEN n.target_type IS NULL THEN n.created_at END) AS approval_time," +
                "        m.merged_at AS merge_time" +
                "    FROM" +
                "        crest.merge_request m" +
                "    LEFT JOIN" +
                "        crest.notes n ON m.integration_id = n.integration_id" +
                "                        AND m.project_id = n.project_id" +
                "                        AND m.iid = n.target_id" +
                "    WHERE" +
                "        m.integration_id = :integration_id AND " +
                getQueryForFilters(filters, startDateEpoch, endDateEpoch) +
                "    GROUP BY" +
                "        m.integration_id," +
                "        m.project_id," +
                "        m.iid," +
                "        m.created_at," +
                "        m.merged_at" +
                ")" +
                "SELECT" +
                "    AVG(EXTRACT(EPOCH FROM (review_time - creation_time)) / 3600.0) AS mr_review," +
                "    AVG(EXTRACT(EPOCH FROM (approval_time - review_time)) / 3600.0) AS mr_approval," +
                "    AVG(EXTRACT(EPOCH FROM (merge_time - approval_time)) / 3600.0) AS mr_merged" +
                " FROM" +
                "    pr_stage_times";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("get_first_review", getFirstReview);
        parameters.addValue("integration_id", integrationId);
        Map<String, Object> stageTimeQueryList = namedParameterJdbcTemplate.queryForMap(sql, parameters);
        Map<String, Double> resultMap = new HashMap<>();
        resultMap.put("mr_created", 0d);
        for (String i : stageTimeQueryList.keySet()) {
            if (stageTimeQueryList.get(i) == null) {
                resultMap.put(i, 0d);
            } else {
                resultMap.put(i,new BigDecimal(stageTimeQueryList.get(i).toString()).doubleValue());
            }
        }
        return resultMap;
    }

    private String getQueryForFilters(List<String> filters, Long startDateEpoch, Long endDateEpoch) {
        if (filters.size() == 1 && "merged_between".equals(filters.get(0))) {
            return "m.merged_at >='" + new Timestamp(startDateEpoch * 1000) + "' AND " +
                    "m.merged_at <='" + new Timestamp(endDateEpoch * 1000) + "'";
        } else {
            log.error("Invalid Filter selected");
            throw new IllegalArgumentException("Invalid Filter selection");
        }
    }

    public Map<String, Double> stageTimeQuery(boolean getFirstReview, UUID integrationId, List<String> filters,
                                              Long startDateEpoch, Long endDateEpoch) {

        String sql = "WITH pr_stage_times AS (" +
                "    SELECT" +
                "        m.integration_id," +
                "        m.project_id," +
                "        m.iid," +
                "        m.created_at AS creation_time," +
                "        CASE WHEN :get_first_review THEN MIN(CASE WHEN n.target_type = 'DiffNote' THEN n.created_at END)" +
                "             ELSE MAX(CASE WHEN n.target_type = 'DiffNote' THEN n.created_at END)" +
                "        END AS review_time," +
                "        MAX(CASE WHEN n.target_type IS NULL THEN n.created_at END) AS approval_time," +
                "        m.merged_at AS merge_time" +
                "    FROM" +
                "        crest.merge_request m" +
                "    LEFT JOIN" +
                "        crest.notes" +
                " n ON m.integration_id = n.integration_id" +
                "                        AND m.project_id = n.project_id" +
                "                        AND m.iid = n.target_id" +
                "    WHERE" +
                "        m.integration_id = :integration_id AND " +
                getQueryForFilters(filters, startDateEpoch, endDateEpoch) +
                "    GROUP BY" +
                "        m.integration_id," +
                "        m.project_id," +
                "        m.iid," +
                "        m.created_at," +
                "        m.merged_at" +
                ")" +
                "SELECT" +
                "    AVG(EXTRACT(EPOCH FROM (review_time - creation_time)) / 3600.0) AS mr_created," +
                "    AVG(EXTRACT(EPOCH FROM (approval_time - review_time)) / 3600.0) AS mr_review," +
                "    AVG(EXTRACT(EPOCH FROM (merge_time - approval_time)) / 3600.0) AS mr_approval" +
                " FROM" +
                "    pr_stage_times";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("get_first_review", getFirstReview)
                .addValue("integration_id", integrationId);

        Map<String, Object> queryResult = namedParameterJdbcTemplate.queryForMap(sql, parameters);
        Map<String, Double> stageTimeMap = new HashMap<>();
        for (String key : queryResult.keySet()) {
            if (queryResult.get(key) == null) {
                stageTimeMap.put(key, 0d);
            } else {
                stageTimeMap.put(key, new BigDecimal(queryResult.get(key).toString()).doubleValue());
            }
        }
        stageTimeMap.put("mr_merged", 0d);
        return stageTimeMap;
    }
}
