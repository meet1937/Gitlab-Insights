package com.crest.gi.utils.dropbox;

import com.dropbox.core.DbxRequestConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DropboxConfig {
    @Bean
    public DbxRequestConfig dbxRequestConfig() {
        return DbxRequestConfig.newBuilder("Gitlab_Insights").build();
    }
}
