package com.crest.gi.pull.datasource;

import com.crest.gi.pull.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class ProjectDataSource {

    @Autowired
    @Qualifier("webClient")
    private WebClient webClient;

    /**
     * Method to fetch all the Updated Projects in given time range
     *
     * @param from            time from which you want to fetch MRs
     * @param to              time till you want to fetch MRs
     * @param baseUrl         url of the api you want to fetch data from
     * @param gitLabAccessKey Access key
     * @return List of all Projects updated
     */
    public Stream<Project> fetch(LocalDateTime from, LocalDateTime to, String baseUrl, String gitLabAccessKey) {
        Integer totalPages = getTotalPages(from, to, baseUrl, gitLabAccessKey).block();
        return IntStream.rangeClosed(1, totalPages)
                .mapToObj(page -> getProjects(from, to, baseUrl, gitLabAccessKey, page))
                .flatMap(Flux::toStream);
    }

    /**
     * Method to fetch total number of pages
     *
     * @param from            time from which you want to fetch MRs
     * @param to              time till you want to fetch MRs
     * @param baseUrl         URL of the API you want to fetch data from
     * @param gitLabAccessKey Access key
     * @return Mono of total pages
     */
    private Mono<Integer> getTotalPages(LocalDateTime from, LocalDateTime to, String baseUrl, String gitLabAccessKey) {

        String uri = baseUrl + "/api/v4/projects" +
                "?simple={simple}&per_page={per_page}" +
                "&page={page}&updated_after={updated_after}" +
                "&updated_before={updated_before}" +
                "&order_by={order_by}";

        Map<String, Object> params = new HashMap<>();
        params.put("simple", true);
        params.put("per_page", 100);
        params.put("page", 1);
        params.put("updated_after", from);
        params.put("updated_before", to);
        params.put("order_by", "updated_at");

        return webClient
                .get()
                .uri(uri, params)
                .header("Authorization", "Bearer " + gitLabAccessKey)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(response -> Mono.justOrEmpty(response.headers().header("X-Total-Pages")))
                .map(headers -> Integer.parseInt(headers.get(0)));
    }

    /**
     * Method to fetch MRs page wise for project
     *
     * @param page            page number of Project api response
     * @param from            time from which you want to fetch MRs
     * @param to              time till you want to fetch MRs
     * @param baseUrl         url of the api you want to fetch data from
     * @param gitLabAccessKey gitLabAccessKey Access key
     */
    private Flux<Project> getProjects(LocalDateTime from,
                                      LocalDateTime to,
                                      String baseUrl,
                                      String gitLabAccessKey,
                                      Integer page) {

        String uri = baseUrl + "/api/v4/projects" +
                "?simple={simple}&per_page={per_page}" +
                "&page={page}&updated_after={updated_after}" +
                "&updated_before={updated_before}" +
                "&order_by={order_by}";

        Map<String, Object> params = new HashMap<>();
        params.put("simple", true);
        params.put("per_page", 100);
        params.put("page", page);
        params.put("updated_after", from);
        params.put("updated_before", to);
        params.put("order_by", "updated_at");

        return webClient
                .get()
                .uri(uri, params)
                .header("Authorization", "Bearer " + gitLabAccessKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Project.class);
    }
}
