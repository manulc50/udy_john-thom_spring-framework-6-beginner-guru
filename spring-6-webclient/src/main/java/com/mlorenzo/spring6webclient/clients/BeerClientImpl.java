package com.mlorenzo.spring6webclient.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.mlorenzo.spring6webclient.models.BeerDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class BeerClientImpl implements BeerClient {
    private static final String BEER_PATH = "/api/v3/beers";
    private static final String BEER_ID_PATH = BEER_PATH + "/{beerId}";

    private final WebClient webClient;

    public BeerClientImpl(final WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<String> getAllV1() {
        return webClient.get().uri(BEER_PATH)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Flux<Map<String, Object>> getAllV2() {
        return webClient.get().uri(BEER_PATH)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Mono<JsonNode> getAllV3() {
        return webClient.get().uri(BEER_PATH)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    @Override
    public Flux<BeerDTO> getAllV4() {
        return webClient.get().uri(BEER_PATH)
                .retrieve()
                .bodyToFlux(BeerDTO.class);
    }

    @Override
    public Flux<BeerDTO> getByStyle(String style) {
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path(BEER_PATH)
                        .queryParam("style", style)
                        .build()
                )
                .retrieve()
                .bodyToFlux(BeerDTO.class);
    }

    @Override
    public Mono<BeerDTO> getById(String id) {
        return webClient.get().uri(BEER_ID_PATH, id)
                .retrieve()
                .bodyToMono(BeerDTO.class);
    }

    @Override
    public Mono<BeerDTO> create(BeerDTO beerDTO) {
        return webClient.post().uri(BEER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(beerDTO)
                .retrieve()
                .toBodilessEntity()
                .map(voidResponseEntity -> voidResponseEntity.getHeaders().get(HttpHeaders.LOCATION).get(0))
                .flatMap(location -> {
                    final String id = location.split("/")[4];
                    return getById(id);
                });
    }

    @Override
    public Mono<BeerDTO> update(String id, BeerDTO beerDTO) {
        return webClient.put().uri(BEER_ID_PATH, id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(beerDTO)
                .retrieve()
                .toBodilessEntity()
                .then(getById(id));
    }

    @Override
    public Mono<BeerDTO> patch(String id, Map<String, Object> mapFields) {
        return webClient.patch().uri(BEER_ID_PATH, id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapFields)
                .retrieve()
                .toBodilessEntity()
                .then(getById(id));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return webClient.delete().uri(uriBuilder -> uriBuilder.path(BEER_ID_PATH).build(id))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
