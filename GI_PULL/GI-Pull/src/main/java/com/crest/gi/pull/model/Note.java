package com.md.gi.pull.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Note {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("body")
    private String body;
    @JsonProperty("attachment")
    private String attachment;
    @JsonProperty("author")
    private User author;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    @JsonProperty("system")
    private Boolean system;
    @JsonProperty("noteable_id")
    private Long noteableId;
    @JsonProperty("noteable_type")
    private String noteableType;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("commit_id")
    private String commitId;
    @JsonProperty("position")
    private Position position;
    @JsonProperty("resolvable")
    private Boolean resolvable;
    @JsonProperty("resolved")
    private Boolean resolved;
    @JsonProperty("resolved_by")
    private User resolvedBy;
    @JsonProperty("resolved_at")
    private LocalDateTime resolvedAt;
    @JsonProperty("confidential")
    private Boolean confidential;
    @JsonProperty("internal")
    private Boolean internal;
    @JsonProperty("noteable_iid")
    private Long noteableIid;
}

class Position {
    @JsonProperty("base_sha")
    private String baseSha;
    @JsonProperty("start_sha")
    private String startSha;
    @JsonProperty("head_sha")
    private String headSha;
    @JsonProperty("old_path")
    private String oldPath;
    @JsonProperty("new_path")
    private String newPath;
    @JsonProperty("position_type")
    private String positionType;
    @JsonProperty("old_line")
    private Long oldLine;
    @JsonProperty("new_line")
    private Long newLine;
    @JsonProperty("line_range")
    private LineRange lineRange;

}

class LineRange {
    @JsonProperty("start")
    private Start start;
    @JsonProperty("end")
    private End end;
}

class Start {
    @JsonProperty("line_code")
    private String lineCode;
    @JsonProperty("type")
    private String type;
    @JsonProperty("old_line")
    private Long oldLine;
    @JsonProperty("new_line")
    private Long newLine;
}

class End {
    @JsonProperty("line_code")
    private String lineCode;
    @JsonProperty("type")
    private String type;
    @JsonProperty("old_line")
    private Long oldLine;
    @JsonProperty("new_line")
    private Long newLine;
}
