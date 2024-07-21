package com.crest.gi.pull.controller;

import com.crest.gi.pull.datasource.*;
import com.crest.gi.pull.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component("gitlabController")
public class GitlabController implements Controller {

    private final MergeRequestDataSource mergeRequestDataSource;
    private final ProjectDataSource projectDataSource;
    private final UserDataSource userDataSource;
    private final NoteDataSource noteDataSource;

    @Autowired
    public GitlabController(MergeRequestDataSource mergeRequestDataSource,
                            ProjectDataSource projectDataSource,
                            UserDataSource userDataSource,
                            NoteDataSource noteDataSource) {
        this.mergeRequestDataSource = mergeRequestDataSource;
        this.projectDataSource = projectDataSource;
        this.userDataSource = userDataSource;
        this.noteDataSource = noteDataSource;
    }

    /**
     * Method to ingest data from the gitlab
     */
    public List<Data> ingestion(LocalDateTime from, LocalDateTime to, String baseUrl, String gitLabAccessKey) {
        List<Data> dataList = new ArrayList<>();

        Stream<MergeRequest> mergeRequests = mergeRequestDataSource.fetch(from, to, baseUrl, gitLabAccessKey);
        Data mergeRequestData = new Data("mergeRequests", mergeRequests);
        dataList.add(mergeRequestData);

        Stream<Project> projects = projectDataSource.fetch(from, to, baseUrl, gitLabAccessKey);
        Data projectData = new Data("projects", projects);
        dataList.add(projectData);

        Stream<User> users = userDataSource.fetch(baseUrl, gitLabAccessKey);
        Data userData = new Data("users", users);
        dataList.add(userData);

        Stream<Note> notes = noteDataSource.fetch(baseUrl, gitLabAccessKey);

        // filtering notes and fetching only review and approval notes
        Stream<Note> filteredNotes = notes
                .filter(note -> !note.getSystem() || "approved this merge request".equals(note.getBody()));
        Data notesData = new Data("notes", filteredNotes);
        dataList.add(notesData);

        return dataList;
    }
}