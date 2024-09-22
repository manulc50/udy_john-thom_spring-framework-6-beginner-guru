package com.mlorenzo.spring6reactive.services;

import com.mlorenzo.spring6reactive.models.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BeerService {
    Flux<BeerDTO> getAll();
    Mono<BeerDTO> getById(Integer id);
    Mono<BeerDTO> create(BeerDTO beerDTO);
    Mono<Void> update(Integer id, BeerDTO beerDTO);
    Mono<Void> patch(Integer id, Map<String, Object> fields);
    Mono<Void> deleteById(Integer id);
}
