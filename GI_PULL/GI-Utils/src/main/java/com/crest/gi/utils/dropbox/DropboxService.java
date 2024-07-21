package com.md.gi.utils.dropbox;

import com.md.gi.utils.dto.AuthTokenDropBoxDto;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class DropboxService {
    DbxClientV2 client = null;
    private static String accessToken = "";
    @Autowired
    WebClient webClient;
    @Autowired
    DbxRequestConfig config;
    @Value("${dropbox_app_key}")
    private String appKey;
    @Value("${dropbox_app_secret}")
    private String appSecret;
    @Value("${dropbox_refresh_token}")
    private String refreshToken;

    /**
     * Dropbox helper for wrting into database
     *
     * @param path
     * @param data
     */
    public void writeToDropboxHelper(String path , byte[] data) {
        try (InputStream in = new ByteArrayInputStream(data)) {
            client.files()
                    .uploadBuilder(path)
                    .uploadAndFinish(in);
        } catch (IOException | DbxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method will help to read from given path in dropbox
     * @param paths
     * @return
     * @throws IOException
     * @throws DbxException
     */
    public String readFromDropbox(String paths) throws IOException, DbxException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        client.files().download(paths).download(out);
        return out.toString();
    }

    /**
     * This method will return the number of files in given path
     * @param path
     * @return
     * @throws DbxException
     */
    public Integer numberOfFiles(String path) throws DbxException {
        try {
            ListFolderResult result = client.files().listFolder(path);
            return result.getEntries().size();
        }catch (ListFolderErrorException e){
            return 0;
        }

    }

    /**
     * This method will refresh the access token after every hour
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void getRefreshToken() {

        ResponseEntity<AuthTokenDropBoxDto> responseEntity = webClient
                .post()
                .uri("https://api.dropbox.com/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("refresh_token" , refreshToken)
                        .with("grant_type" , "refresh_token")
                        .with("client_id" , appKey)
                        .with("client_secret" , appSecret)
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(AuthTokenDropBoxDto.class).block();
        AuthTokenDropBoxDto authTokenDropBoxDto = responseEntity.getBody();
        accessToken = authTokenDropBoxDto.getAccessToken();
        client = new DbxClientV2(config , accessToken);
    }
}
