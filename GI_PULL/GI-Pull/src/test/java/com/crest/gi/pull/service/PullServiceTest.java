package com.md.gi.pull.service;

import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import com.md.gi.pull.controller.GitlabController;
import com.md.gi.pull.model.Data;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@ExtendWith(MockitoExtension.class)
class PullServiceTest {
  @Mock
  ApplicationContext context;
  @InjectMocks
  PullService pullService;

  @Mock
  GitlabController controller;
  @Test
   void testIngestion_ValidInput_ReturnsDataList() {
    // Mocking dependencies
    GitlabController mockController = mock(GitlabController.class);

    LocalDateTime from = LocalDateTime.now().minusDays(7);
    LocalDateTime to = LocalDateTime.now();
    String baseUrl = "https://example.com";
    String accessKey = "dummyAccessKey";

    // Stubbing controller method to return dummy data
    List<Data> dummyDataList = List.of(new Data("dummy", null));
    when(mockController.ingestion(from, to, baseUrl, accessKey)).thenReturn(dummyDataList);
    List<Data> dataList =
            mockController.ingestion(from, to, baseUrl, accessKey);
    assertFalse(dataList.isEmpty());
    assertEquals(dummyDataList, dataList);
  }

  @Test
   void testIngestion_InvalidURL_ThrowsIllegalArgumentException() {
    GitlabController ingestionService = mock(GitlabController.class);

    LocalDateTime from = LocalDateTime.now().minusDays(7);
    LocalDateTime to = LocalDateTime.now();
    String baseUrl = "invalidUrl";
    String accessKey = "dummyAccessKey";

    assertThrows(
        IllegalArgumentException.class,
        () -> pullService.ingestion("gitlab",from, to, baseUrl, accessKey));
  }

  @Test
   void testIngestion_InvalidController_ThrowsIllegalArgumentException() {
    LocalDateTime from = LocalDateTime.now().minusDays(7);
    LocalDateTime to = LocalDateTime.now();
    String baseUrl = "https://example.com";
    String accessKey = "dummyAccessKey";
    when(context.getBean("xyzController")).thenThrow(NoSuchBeanDefinitionException.class);
    assertThrows(
        IllegalArgumentException.class,
        () -> pullService.ingestion("xyz",from, to, baseUrl, accessKey));
  }

  @Test
   void testIngestion_InvalidURLFormat_ThrowsIllegalArgumentException() {
    LocalDateTime from = LocalDateTime.now().minusDays(7);
    LocalDateTime to = LocalDateTime.now();
    String baseUrl = "https://example.com";
    String accessKey = "dummyAccessKey";
    when(context.getBean("gitlabController")).thenReturn(controller);
    when(controller.ingestion(from, to, "https://example.com", accessKey)).thenThrow(WebClientRequestException.class);
    assertThrows(
        IllegalArgumentException.class,
        () -> pullService.ingestion( "gitlab",from, to, baseUrl, accessKey));
  }

  @Test
   void testIngestion_WebClientResponseException_ThrowsIllegalArgumentException() {
    LocalDateTime from = LocalDateTime.now().minusDays(7);
    LocalDateTime to = LocalDateTime.now();
    String baseUrl = "https://gitlab.cdsys.local.com";
    String accessKey = "dummyAccessKey";
    when(context.getBean("gitlabController")).thenReturn(controller);
    when(controller.ingestion(from, to,baseUrl, accessKey))
        .thenThrow(WebClientResponseException.class);
    assertThrows(
        IllegalArgumentException.class,
        () -> pullService.ingestion("gitlab", from, to, baseUrl, accessKey));
  }
}
