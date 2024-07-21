package com.crest.gi.pull.datasource;

import com.crest.gi.pull.model.MergeRequest;
import com.crest.gi.pull.model.Note;
import com.crest.gi.pull.model.Project;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteDataSourceTest {
  @InjectMocks
  NoteDataSource noteDataSource;
  @Mock
  ProjectDataSource projectDataSource;
  @Mock
  WebClient webClient;
  @Mock
  WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
  @Mock
  MergeRequestDataSource mergeRequestDataSource;
  @Mock
  WebClient.RequestHeadersSpec requestHeadersSpecMock;
  @Mock
  Mono<ClientResponse> monoClientResponse;
  @Mock
  WebClient.ResponseSpec responseSpec;
  @Mock
  Mono<List<String>> listMono;
  List<String> list;
  @Mock
  Mono<Integer> result;
  @Mock
  IntStream instream;

  Project project;
  MergeRequest mergeRequest;
  Note note;

  @BeforeEach
  void setUp() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    project=objectMapper.findAndRegisterModules().readValue(new File("src/test/resources/Project.json") , Project. class);
    mergeRequest = objectMapper.findAndRegisterModules().readValue(new File("src/test/resources/MergeRequest.json"), MergeRequest.class);
    note=new Note();
  }

  @Test
  void fetch() {
    String baseUrl = "https://gitlab.cdsys.local";
    String accessKey = "gitlabToken";
    when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(anyString(), anyMap())).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.header(any(), any())).thenReturn(requestHeadersSpecMock);
    Flux<DataBuffer> fluxOfDataBuffers = Flux.empty();
    monoClientResponse =
            Mono.just(
                    ClientResponse.create(HttpStatusCode.valueOf(200))
                            .header("X-Total-Pages", "1")
                            .body(fluxOfDataBuffers)
                            .build());
    when(requestHeadersSpecMock.exchange()).thenReturn(monoClientResponse);
    when(requestHeadersSpecMock.accept(MediaType.APPLICATION_JSON))
            .thenReturn(requestHeadersSpecMock);
    when(projectDataSource.fetch(null, null, baseUrl, accessKey)).thenReturn(Stream.of(project));
    when(mergeRequestDataSource.fetchProjectMergeRequests(project.getId(), baseUrl, accessKey)).thenReturn(Stream.of(mergeRequest));
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(Note.class)).thenReturn(Flux.just(note));
    Stream<Note> result = noteDataSource.fetch(baseUrl, accessKey);
    Assertions.assertNotNull(result);
    Assertions.assertArrayEquals(result.toArray(),new Note[]{note});
  }

  @Test
  void getMergeRequestEvents(){
    String baseUrl = "https://gitlab.cdsys.local";
    String accessKey = "gitlabToken";
    LocalDateTime from = LocalDateTime.now().minusDays(90);
    LocalDateTime to = LocalDateTime.now();
    Map<String, Object> params =
            Map.of(
                    "scope",
                    "all",
                    "per_page",
                    100,
                    "updated_after",
                    from,
                    "updated_before",
                    to,
                    "page",
                    1);
    listMono = Mono.just(List.of("1", "2"));
    when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(anyString(), anyMap())).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.header(any(), any())).thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.accept(MediaType.APPLICATION_JSON))
            .thenReturn(requestHeadersSpecMock);
    Flux<DataBuffer> fluxOfDataBuffers = Flux.empty();
    monoClientResponse =
            Mono.just(
                    ClientResponse.create(HttpStatusCode.valueOf(200))
                            .header("X-Total-Pages", "1")
                            .body(fluxOfDataBuffers)
                            .build());
    when(requestHeadersSpecMock.exchange()).thenReturn(monoClientResponse);
    when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(Note.class)).thenReturn(Flux.just(note));
    Stream<Note> result=noteDataSource.getMergeRequestEvents(project.getId(),mergeRequest.getIid(),baseUrl,accessKey);
    Assertions.assertNotNull(result);
    Assertions.assertArrayEquals(result.toArray(),new Note[]{note});
  }
}
