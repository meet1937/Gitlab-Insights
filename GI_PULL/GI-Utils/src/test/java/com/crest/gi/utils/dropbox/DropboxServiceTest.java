package com.crest.gi.utils.dropbox;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DropboxServiceTest {
    @Mock
    DbxClientV2 client;
    @Mock
    Files files;
    @Mock
    WebClient webClient;
    @Mock
    DbxRequestConfig config;
    @InjectMocks
    DropboxService dropboxService;
    @Mock
    DbxUserFilesRequests dbxUserFilesRequests;
    @Mock
    UploadBuilder uploadBuilder;
    @Mock
    DbxDownloader<FileMetadata> dbxDownloader;
    @Mock
    InputStream in;
    @Mock
    ByteArrayOutputStream out;
    @Mock
    ListFolderResult listFolderResult;
    @Mock
    List<Metadata> list;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testWriteToDropboxHelperIOException() throws IOException, DbxException {
    String path =
        "/7a51f4e5-fccb-4b3b-9ff8-84bc985cf357/2024/MARCH/27/7c6151c3-909f-4f2e-a6aa-b795b5cdf172/users/1.json";
        when(client.files()).thenReturn(dbxUserFilesRequests);
        when(client.files().uploadBuilder(path)).thenReturn(uploadBuilder);
        when(uploadBuilder.uploadAndFinish(in)).thenThrow(IOException.class);
        Assertions.assertThrows(RuntimeException.class, () -> dropboxService.writeToDropboxHelper(path, new byte[]{(byte) 0}));
    }
    @Test
    void testWriteToDropboxHelperDbxException() throws IOException, DbxException {
        String path =
                "/7a51f4e5-fccb-4b3b-9ff8-84bc985cf357/2024/MARCH/27/7c6151c3-909f-4f2e-a6aa-b795b5cdf172/users/1.json";
        when(client.files()).thenReturn(dbxUserFilesRequests);
        when(client.files().uploadBuilder(path)).thenReturn(uploadBuilder);
        when(uploadBuilder.uploadAndFinish(in)).thenThrow(DbxException.class);
        Assertions.assertThrows(RuntimeException.class, () -> dropboxService.writeToDropboxHelper(path, new byte[]{(byte) 0}));
    }

    @Test
    void testReadFromDropbox() throws IOException, DbxException {
        String path =
                "/7a51f4e5-fccb-4b3b-9ff8-84bc985cf357/2024/MARCH/27/7c6151c3-909f-4f2e-a6aa-b795b5cdf172/users/1.json";
        String expected="Testing";
        when(client.files()).thenReturn(dbxUserFilesRequests);
        in.read(expected.toString().getBytes());
        dbxDownloader=new DbxDownloader<>(null,in);
        in.read(expected.toString().getBytes(StandardCharsets.UTF_8));
        when(dbxUserFilesRequests.download(path)).thenReturn(dbxDownloader);
        when(out.toString()).thenReturn(expected);
        String result = dropboxService.readFromDropbox(path);
        Assertions.assertEquals(expected, result);
    }
    @Test
    void testNumberOfFiles() throws DbxException {
        when(client.files()).thenReturn(dbxUserFilesRequests);
        when(dbxUserFilesRequests.listFolder("path")).thenReturn(listFolderResult);
        when(listFolderResult.getEntries()).thenReturn(list);
        when(list.size()).thenReturn(2);
        Integer result = dropboxService.numberOfFiles("path");
        Assertions.assertEquals(Integer.valueOf(2), result);
    }

    @Test
    void testNumberOfFilesListFolderException() throws DbxException {
        when(client.files()).thenReturn(dbxUserFilesRequests);
        when(dbxUserFilesRequests.listFolder("path")).thenThrow(ListFolderErrorException.class);
        Integer result = dropboxService.numberOfFiles("path");
        Assertions.assertEquals(Integer.valueOf(0), result);
    }



    @Test
    void testGetRefreshToken(){
        dropboxService.getRefreshToken();
    }
}

