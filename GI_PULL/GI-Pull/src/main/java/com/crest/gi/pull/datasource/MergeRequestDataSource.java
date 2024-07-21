package com.crest.gi.pull.datasource;

import com.crest.gi.pull.model.MergeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class MergeRequestDataSource {
    @Autowired
    @Qualifier("webClient")
    private WebClient webClient;

    /**
     * Method to fetch all the Merge Requests in the given time range
     *
     * @param from            time from which you want to fetch MRs
     * @param to              time till you want to fetch MRs
     * @param baseUrl         URL of the API you want to fetch data from
     * @param gitLabAccessKey Access key
     * @return Flux of all MRs
     */
    public Stream<MergeRequest> fetch(LocalDateTime from, LocalDateTime to, String baseUrl, String gitLabAccessKey) {
        Integer totalPages = getTotalPages(from, to, baseUrl, gitLabAccessKey).block();
        return IntStream.rangeClosed(1, totalPages)
                .mapToObj(page -> getMergeRequests(from, to, baseUrl, gitLabAccessKey, page))
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

        String uri = baseUrl + "/api/v4/merge_requests" +
                "?scope={scope}&per_page={per_page}" +
                "&updated_after={updated_after}" +
                "&updated_before={updated_before}" +
                "&page={page}";

        Map<String, Object> params = Map.of(
                "scope", "all",
                "per_page", 100,
                "updated_after", from,
                "updated_before", to,
                "page", 1
        );

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
     * Method to fetch MRs for a given page
     *
     * @param from            time from which you want to fetch MRs
     * @param to              time till you want to fetch MRs
     * @param baseUrl         URL of the API you want to fetch data from
     * @param gitLabAccessKey Access key
     * @param page            page number
     * @return List of MRs for the given page
     */
    private Flux<MergeRequest> getMergeRequests(LocalDateTime from,
                                                LocalDateTime to,
                                                String baseUrl,
                                                String gitLabAccessKey,
                                                int page) {
        String uri = baseUrl + "/api/v4/merge_requests" +
                "?scope={scope}&per_page={per_page}" +
                "&updated_after={updated_after}" +
                "&updated_before={updated_before}" +
                "&page={page}";

        Map<String, Object> params = Map.of(
                "scope", "all",
                "per_page", 100,
                "updated_after", from,
                "updated_before", to,
                "page", page
        );

        return webClient
                .get()
                .uri(uri, params)
                .header("Authorization", "Bearer " + gitLabAccessKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(MergeRequest.class);
    }

    public Stream<MergeRequest> fetchProjectMergeRequests(Long projectId, String baseUrl, String gitLabAccessKey) {
        Integer totalPages = getTotalProjectMrPages(projectId, baseUrl, gitLabAccessKey).block();
        return IntStream.rangeClosed(1, totalPages)
                .mapToObj(page -> getProjectMergeRequests(projectId, baseUrl, gitLabAccessKey, page))
                .flatMap(Flux::toStream);

    }

    private Mono<Integer> getTotalProjectMrPages(Long projectId, String baseUrl, String gitLabAccessKey) {

        String uri = baseUrl + "/api/v4/projects/{project_id}/merge_requests" +
                "?per_page={per_page}" +
                "&page={page}";

        Map<String, Object> params = Map.of(
                "project_id", projectId,
                "per_page", 100,
                "page", 1
        );

        return webClient
                .get()
                .uri(uri, params)
                .header("Authorization", "Bearer " + gitLabAccessKey)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(response -> Mono.justOrEmpty(response.headers().header("X-Total-Pages")))
                .map(headers -> Integer.parseInt(headers.get(0)));
    }

    private Flux<MergeRequest> getProjectMergeRequests(Long projectId, String baseUrl, String gitLabAccessKey, int page) {

        String uri = baseUrl + "/api/v4/projects/{project_id}/merge_requests" +
                "?per_page={per_page}" +
                "&page={page}";

        Map<String, Object> params = Map.of(
                "project_id", projectId,
                "per_page", 100,
                "page", page
        );

        return webClient
                .get()
                .uri(uri, params)
                .header("Authorization", "Bearer " + gitLabAccessKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(MergeRequest.class);
    }
}