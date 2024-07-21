package com.md.gi.pull.datasource;

import static org.mockito.Mockito.*;

import com.md.gi.pull.model.MergeRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
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

@ExtendWith(MockitoExtension.class)
class MergeRequestDataSourceTest {
  @Mock
  WebClient webClient;
  @Mock
  WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
  @InjectMocks
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
  MergeRequest mergeRequest;

  @BeforeEach
  void setUp() {
    list = List.of("1", "2", "3");
    listMono = Mono.justOrEmpty(list);
    result = Mono.just(1);
    mergeRequest=new MergeRequest();
  }

  @Test
  void testFetch() {
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
    when(responseSpec.bodyToFlux(MergeRequest.class)).thenReturn(Flux.just(mergeRequest));
    Stream<MergeRequest> result = mergeRequestDataSource.fetch(from, to, baseUrl, accessKey);
    Assertions.assertNotNull(result);
    Assertions.assertArrayEquals(Stream.of(mergeRequest).toArray(),result.toArray());
  }
  @Test
  void testFetchProjectMergeRequests(){
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
    when(responseSpec.bodyToFlux(MergeRequest.class)).thenReturn(Flux.just(mergeRequest));
    Stream<MergeRequest> result = mergeRequestDataSource.fetchProjectMergeRequests(10L, baseUrl, accessKey);
    Assertions.assertNotNull(result);
    Assertions.assertArrayEquals(Stream.of(mergeRequest).toArray(),result.toArray());
  }
}
