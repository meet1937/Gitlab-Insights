package com.crest.gi.pull.controller;

import com.crest.gi.pull.model.Data;

import java.time.LocalDateTime;
import java.util.List;

public interface Controller {
    public List<Data> ingestion(LocalDateTime from, LocalDateTime to, String baseUrl, String gitLabAccessKey);
}
