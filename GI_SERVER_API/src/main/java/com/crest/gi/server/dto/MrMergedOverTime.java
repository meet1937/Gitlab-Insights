package com.crest.gi.server.dto;

import lombok.Data;

import java.util.List;

@Data
public class MrMergedOverTime {
    List<Merged> day;
    List<Merged> week;
    List<Merged> month;
}

