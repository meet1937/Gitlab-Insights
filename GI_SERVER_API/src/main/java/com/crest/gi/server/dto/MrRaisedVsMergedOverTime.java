package com.crest.gi.server.dto;

import lombok.Data;

import java.util.List;

@Data
public class MrRaisedVsMergedOverTime {
    List<RaisedVsMerged> day;
    List<RaisedVsMerged> week;
    List<RaisedVsMerged> month;
}


