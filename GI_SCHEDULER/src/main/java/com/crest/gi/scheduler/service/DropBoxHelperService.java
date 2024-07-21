package com.md.gi.scheduler.service;

import com.md.gi.utils.dropbox.DropboxService;
import com.dropbox.core.DbxException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DropBoxHelperService {
  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  DropboxService dropboxService;

  /**
   * To write in dropbox service
   *
   * @param path
   * @param methodName
   * @param object
   * @throws DbxException
   * @throws IOException
   */
  public void writeToDropBox(String path, String methodName, Stream<?> object) {
    try {
      log.info("Starting Dropbox process for :" + ":" + methodName);
      String pathWithMethodName = path + methodName + "/";
      AtomicInteger chunkIndex = new AtomicInteger(1);
      // checking that file size not exceed 5 mb
      int chunkSize = 5000000;
      AtomicInteger currentChunkSize = new AtomicInteger();
      AtomicReference<List<Object>> store = new AtomicReference<>(new ArrayList<>());
      object.forEach(
          obj -> {
            try {
              int currentSize = objectMapper.writeValueAsString(obj).getBytes().length;
              store.get().add(obj);
              if (currentChunkSize.get() + currentSize > chunkSize) {
                byte[] input = objectMapper.writeValueAsString(store).getBytes();
                dropboxService.writeToDropboxHelper(
                    pathWithMethodName + chunkIndex + ".json", input);
                chunkIndex.getAndIncrement();
                currentChunkSize.set(0);
                store.set(new ArrayList<>());
              }
              currentChunkSize.addAndGet(currentSize);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          });
      if (currentChunkSize.get() > 0) {
        byte[] temp =
            new ObjectMapper().findAndRegisterModules().writeValueAsString(store).getBytes();
        dropboxService.writeToDropboxHelper(pathWithMethodName + chunkIndex + ".json", temp);
      }
      log.info("Data Saved for :" + methodName);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }
}
