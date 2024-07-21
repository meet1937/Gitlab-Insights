package com.md.gi.pull.datasource;

import com.md.gi.pull.model.User;
import org.junit.jupiter.api.Assertions;
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
import java.util.List;
import java.util.stream.Stream;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDataSourceTest {
  @Mock WebClient webClient;
  @Mock WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
  @InjectMocks UserDataSource userDataSource;
  @Mock WebClient.RequestHeadersSpec requestHeadersSpecMock;
  @Mock Mono<ClientResponse> monoClientResponse;

  @Mock WebClient.ResponseSpec responseSpec;
  @Mock Mono<List<String>> listMono;
  List<String> list;
  @Mock Mono<Integer> result;
  User user;


  @Test
  void fetch() {
    user=new User();
    String baseUrl = "https://gitlab.cdsys.local";
    String accessKey = "gitlabToken";
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
    when(responseSpec.bodyToFlux(User.class)).thenReturn(Flux.just(user));
    Stream<User> result = userDataSource.fetch(baseUrl, accessKey);
    Assertions.assertNotNull(result);
    Assertions.assertArrayEquals(result.toArray(),new User[]{user});
  }
}
