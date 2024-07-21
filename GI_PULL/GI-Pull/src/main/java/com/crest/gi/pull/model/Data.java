package com.md.gi.pull.model;

import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@lombok.Data
@AllArgsConstructor
public class Data {
    private String dataType;
    private Stream<?> dataList;
}
