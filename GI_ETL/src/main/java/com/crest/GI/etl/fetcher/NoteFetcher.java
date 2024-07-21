package com.crest.GI.etl.fetcher;

import com.crest.GI.etl.dto.NoteDto;
import com.crest.GI.etl.loader.NoteLoader;
import com.crest.GI.etl.mapper.GitlabMapper;
import com.crest.GI.etl.model.Note;
import com.crest.gi.utils.dropbox.DropboxService;
import com.dropbox.core.DbxException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
public class NoteFetcher {

    @Autowired
    DropboxService dropboxService;
    @Autowired
    NoteLoader noteLoader;
    @Autowired
    GitlabMapper mapper;
    @Autowired
    ObjectMapper objectMapper;

    public void fetch(
            String folderPath,
            UUID integrationId,
            String schemaName,
            Map<Long, UUID> projectIds)
            throws DbxException {
        int count = dropboxService.numberOfFiles(folderPath) + 1;
        try{
            for(int idx = 1; idx <count; idx++) {
                List<NoteDto> list =
                        objectMapper.readValue(
                                dropboxService.readFromDropbox(folderPath + idx + ".json"),
                                new TypeReference<List<NoteDto>>() {
                                });
                List<Note> notes=mapper.toNote(list);
                noteLoader.insert(notes,integrationId, projectIds, schemaName);
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DbxException e) {
            throw new RuntimeException(e);
        }
    }
}
