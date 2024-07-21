package com.md.gi.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RaisedVsMerged {
    Long date;
    Integer raisedCount;
    Integer mergedCount;
}
