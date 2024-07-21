package com.md.gi.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StageTimeQuery {
    Double creationToReview;
    Double reviewToApproval;
    Double approvalToMerged;
}
