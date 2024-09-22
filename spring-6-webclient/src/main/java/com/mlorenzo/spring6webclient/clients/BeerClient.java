package com.mlorenzo.spring6webclient.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.mlorenzo.spring6webclient.models.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BeerClient {
    Mono<String> getAllV1();
    Flux<Map<String, Object>> getAllV2();
    Mono<JsonNode> getAllV3();
    Flux<BeerDTO> getAllV4();
    Flux<BeerDTO> getByStyle(String style);
    Mono<BeerDTO> getById(String id);
    Mono<BeerDTO> create(BeerDTO beerDTO);
    Mono<BeerDTO> update(String id, BeerDTO beerDTO);
    Mono<BeerDTO> patch(String id, Map<String, Object> mapFields);
    Mono<Void> deleteById(String id);
}
