package com.crest.gi.pull.service;

import com.crest.gi.pull.controller.Controller;
import com.crest.gi.pull.model.Data;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PullService {
    @Autowired
    private ApplicationContext context;

    /**
     * Method to ingest the data
     *
     * @param controllerName controller for which you want to retrieve data
     * @param from           time from which you want to ingest the data
     * @param to             time till you want the data to be ingested
     * @param baseUrl        url of the api you want to fetch data from
     * @param accessKey      access key
     */
    public List<Data> ingestion(String controllerName, LocalDateTime from,
                                LocalDateTime to, String baseUrl, String accessKey) {

        final String urlRegex = "^(https?://)[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(?:/\\S*)?$";

        if (!Pattern.matches(urlRegex, baseUrl)) {
            throw new IllegalArgumentException("Invalid url");
        }

        try {
            String controllerString = controllerName.toLowerCase() + "Controller";
            Controller controller = (Controller) context.getBean(controllerString);
            return controller.ingestion(from, to, baseUrl, accessKey);

        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalArgumentException("Invalid controller");

        } catch (WebClientRequestException e) {
            throw new IllegalArgumentException("Invalid url");

        } catch (WebClientResponseException e) {
            throw new IllegalArgumentException("Invalid Access Key");
        }
    }
}
