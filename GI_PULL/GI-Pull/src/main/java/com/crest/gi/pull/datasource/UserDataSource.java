package com.crest.gi.pull.datasource;

import com.crest.gi.pull.model.User;
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
public class UserDataSource {

    @Autowired
    @Qualifier("webClient")
    private WebClient webClient;

    /**
     * Method to fetch all the users
     *
     * @param baseUrl         url of the api you want to fetch data from
     * @param gitLabAccessKey Access key
     * @return List of all Users
     */
    public Stream<User> fetch(String baseUrl, String gitLabAccessKey) {
        Integer totalPages = getTotalPages(baseUrl, gitLabAccessKey).block();

        return IntStream.rangeClosed(1, totalPages)
                .mapToObj(page -> getUsers(baseUrl, gitLabAccessKey, page))
                .flatMap(Flux::toStream);
    }

    /**
     * Method to fetch total number of pages
     *
     * @param baseUrl         URL of the API you want to fetch data from
     * @param gitLabAccessKey Access key
     * @return Mono of total pages
     */
    private Mono<Integer> getTotalPages(String baseUrl, String gitLabAccessKey) {

        String uri = baseUrl + "/api/v4/users" +
                "?without_project_bots={without_project_bots}" +
                "&per_page={per_page}&page={page}";

        Map<String, Object> params = Map.of(
                "without_project_bots", true,
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

    /**
     * Method to fetch Users page wise
     *
     * @param page            page number of User api response
     * @param baseUrl         url of the api you want to fetch data from
     * @param gitLabAccessKey gitLabAccessKey Access key
     */
    private Flux<User> getUsers(String baseUrl,
                                String gitLabAccessKey,
                                int page) {

        String uri = baseUrl + "/api/v4/users" +
                "?without_project_bots={without_project_bots}" +
                "&per_page={per_page}&page={page}";

        Map<String, Object> params = Map.of(
                "without_project_bots", true,
                "per_page", 100,
                "page", page
        );

        return webClient
                .get()
                .uri(uri, params)
                .header("Authorization", "Bearer " + gitLabAccessKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(User.class);
    }

}
