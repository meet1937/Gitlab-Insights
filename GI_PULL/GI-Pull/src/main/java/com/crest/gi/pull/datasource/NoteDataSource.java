package com.md.gi.pull.datasource;

import com.md.gi.pull.model.Note;
import com.md.gi.pull.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@Slf4j
public class NoteDataSource {

    @Autowired
    private ProjectDataSource projectDataSource;
    @Autowired
    private MergeRequestDataSource mergeRequestDataSource;
    @Autowired
    @Qualifier("webClient")
    private WebClient webClient;

    private static final int MAX_REQUESTS_BEFORE_RESET = 125;
    private int requestCount = 0;


    /**
     * Method to fetch all the notes of all merge requests of all projects
     *
     * @param baseUrl         url of the api you want to fetch data from
     * @param gitLabAccessKey Access key
     * @return returns all the notes accessible to current user
     */
    public Stream<Note> fetch(String baseUrl, String gitLabAccessKey) {
        Stream<Project> projects = projectDataSource.fetch(null, null, baseUrl, gitLabAccessKey);

        return projects
                .flatMap(project -> getProjectEvents(project, baseUrl, gitLabAccessKey));

    }

    /**
     * Method to fetch all the notes of a particular project
     *
     * @param project         Project object for which to fetch all the notes
     * @param baseUrl         url of the api you want to fetch data from
     * @param gitLabAccessKey Access key
     * @return returns all the notes of given project
     */
    private Stream<Note> getProjectEvents(Project project,
                                          String baseUrl,
                                          String gitLabAccessKey) {

        return mergeRequestDataSource.fetchProjectMergeRequests(project.getId(), baseUrl, gitLabAccessKey)
                .flatMap(mergeRequest -> getMergeRequestEvents(project.getId(), mergeRequest.getIid(), baseUrl, gitLabAccessKey));
    }

    /**
     * Method to fetch all the notes of a particular Merge request
     *
     * @param projectId       ID of the project to which that merge request belongs
     * @param mergeRequestIid IID of the merge request
     * @param baseUrl         Url of the api you want to fetch data from
     * @param gitLabAccessKey Access key
     * @return all the notes of merge request
     */
    public Stream<Note> getMergeRequestEvents(Long projectId, Long mergeRequestIid,
                                              String baseUrl, String gitLabAccessKey) {
        Integer totalPages = getTotalPages(projectId, mergeRequestIid, baseUrl, gitLabAccessKey).block();
        return IntStream.rangeClosed(1, totalPages)
                .mapToObj(page -> getEvents(projectId, mergeRequestIid, baseUrl, gitLabAccessKey, page))
                .flatMap(Flux::toStream)
                .sequential();
    }


    /**
     * Method to fetch total number of pages
     *
     * @param projectId       ID of the project to which that merge request belongs
     * @param mergeRequestIid IID of the merge request
     * @param baseUrl         Url of the api you want to fetch data from
     * @param gitLabAccessKey Access key
     * @return Mono of total pages
     */
    private Mono<Integer> getTotalPages(Long projectId,
                                        Long mergeRequestIid,
                                        String baseUrl,
                                        String gitLabAccessKey) {


        String uri = baseUrl + "/api/v4/projects/{project_id}/merge_requests/{merge_request_iid}/notes" +
                "?page={page}" +
                "&per_page={per_page}";


        Map<String, Object> params = Map.of(
                "project_id", projectId,
                "merge_request_iid", mergeRequestIid,
                "per_page", 100,
                "page", 1
        );

        return getClient()
                .get()
                .uri(uri, params)
                .header("Authorization", "Bearer " + gitLabAccessKey)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(response -> Mono.justOrEmpty(response.headers().header("X-Total-Pages")))
                .map(headers -> Integer.parseInt(headers.get(0)));
    }

    private Flux<Note> getEvents(Long projectId,
                                 Long mergeRequestIid,
                                 String baseUrl,
                                 String gitLabAccessKey,
                                 Integer page) {

        String uri = baseUrl + "/api/v4/projects/{project_id}/merge_requests/{merge_request_iid}/notes" +
                "?page={page}" +
                "&per_page={per_page}";

        Map<String, Object> params = Map.of(
                "project_id", projectId,
                "merge_request_iid", mergeRequestIid,
                "per_page", 100,
                "page", page
        );

        return getClient()
                .get()
                .uri(uri, params)
                .header("Authorization", "Bearer " + gitLabAccessKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Note.class);
    }

    /* As number of open HTTP streams (per HTTP 2 connection) exceeds 125, we would exceed the MAX_CONCURRENT_STREAMS
       limit and thus creating a new web client for further requests */
    public WebClient getClient() {
        if (requestCount >= MAX_REQUESTS_BEFORE_RESET) {
            requestCount = 0;
            return webClient = WebClient.builder().build();
        }
        requestCount++;
        return webClient;
    }
}

