package com.md.gi.pull.datasource;

import com.md.gi.pull.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectDataSourceTest {
    @InjectMocks
            @Spy
    ProjectDataSource projectDataSource;
    @Mock
    WebClient webClient;
    @Mock
    IntStream intStream;
    @Mock
    WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @Mock
    WebClient.RequestHeadersSpec requestHeadersSpecMock;
    @Mock
    Mono<ClientResponse> monoClientResponse;
    @Mock
    Mono<List<String>> listMono;
    @Mock
    WebClient.ResponseSpec responseSpec;
    Project project;
    @BeforeEach
    void setUp() {
        project=new Project();
    }
    @Test
    void testFetch(){
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
        when(responseSpec.bodyToFlux(Project.class)).thenReturn(Flux.just(project));
        Stream<Project> result=projectDataSource.fetch(from,to,baseUrl,accessKey);
        Assertions.assertNotNull(result);
        Assertions.assertArrayEquals(result.toArray(),new Project[]{project});
    }
}
