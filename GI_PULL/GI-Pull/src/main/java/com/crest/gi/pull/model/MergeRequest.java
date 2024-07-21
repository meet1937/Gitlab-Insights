package com.md.gi.pull.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MergeRequest {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("iid")
    private Long iid;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("state")
    private String state;
    @JsonProperty("merged_by")
    private User mergedBy;
    @JsonProperty("merge_user")
    private User mergeUser;
    @JsonProperty("merged_at")
    private LocalDateTime mergedAt;
    @JsonProperty("prepared_at")
    private LocalDateTime preparedAt;
    @JsonProperty("closed_by")
    private User closedBy;
    @JsonProperty("closed_at")
    private LocalDateTime closedAt;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    @JsonProperty("target_branch")
    private String targetBranch;
    @JsonProperty("source_branch")
    private String sourceBranch;
    @JsonProperty("upvotes")
    private Integer upvotes;
    @JsonProperty("downvotes")
    private Integer downvotes;
    @JsonProperty("author")
    private User author;
    @JsonProperty("assignee")
    private User assignee;
    @JsonProperty("assignees")
    private List<User> assignees;
    @JsonProperty("reviewers")
    private List<User> reviewers;
    @JsonProperty("source_project_id")
    private Long sourceProjectId;
    @JsonProperty("target_project_id")
    private Long targetProjectId;
    @JsonProperty("labels")
    private List<String> labels;
    @JsonProperty("draft")
    private Boolean draft;
    @JsonProperty("work_in_progress")
    private Boolean workInProgress;
    @JsonProperty("milestone")
    private Milestone milestone;
    @JsonProperty("merge_when_pipeline_succeeds")
    private Boolean mergeWhenPipelineSucceeds;
    @JsonProperty("merge_status")
    private String mergeStatus;
    @JsonProperty("detailed_merge_status")
    private String detailedMergeStatus;
    @JsonProperty("sha")
    private String sha;
    @JsonProperty("merge_commit_sha")
    private String mergeCommitSha;
    @JsonProperty("squash_commit_sha")
    private String squashCommitSha;
    @JsonProperty("user_notes_count")
    private Integer userNotesCount;
    @JsonProperty("discussion_locked")
    private Boolean discussionLocked;
    @JsonProperty("should_remove_source_branch")
    private Boolean shouldRemoveSourceBranch;
    @JsonProperty("force_remove_source_branch")
    private Boolean forceRemoveSourceBranch;
    @JsonProperty("allow_collaboration")
    private Boolean allowCollaboration;
    @JsonProperty("allow_maintainer_to_push")
    private Boolean allowMaintainerToPush;
    @JsonProperty("web_url")
    private String webUrl;
    @JsonProperty("reference")
    private String reference;
    @JsonProperty("references")
    private References references;
    @JsonProperty("time_stats")
    private TimeStats timeStats;
    @JsonProperty("squash")
    private Boolean squash;
    @JsonProperty("task_completion_status")
    private TaskCompletionStatus taskCompletionStatus;
    @JsonProperty("has_conflicts")
    private Boolean hasConflicts;
    @JsonProperty("blocking_discussions_resolved")
    private Boolean blockingDiscussionsResolved;
}

class TaskCompletionStatus {
    @JsonProperty("count")
    private Integer count;
    @JsonProperty("completed_count")
    private Integer completedCount;
}

class TimeStats {
    @JsonProperty("time_estimate")
    private Integer timeEstimate;
    @JsonProperty("total_time_spent")
    private Integer totalTimeSpent;
    @JsonProperty("human_time_estimate")
    private String humanTimeEstimate;
    @JsonProperty("human_total_time_spent")
    private String humanTotalTimeSpent;
}

class References {
    @JsonProperty("short")
    private String shortReference;
    @JsonProperty("relative")
    private String relative;
    @JsonProperty("full")
    private String fullReference;
}

class Milestone {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("iid")
    private Long iid;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("state")
    private String state;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    @JsonProperty("due_date")
    private LocalDateTime dueDate;
    @JsonProperty("start_date")
    private LocalDateTime startDate;
    @JsonProperty("web_url")
    private String webUrl;
}


