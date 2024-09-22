package com.mlorenzo.spring6reactivemongo.services;

import com.mlorenzo.spring6reactivemongo.models.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BeerService {
    Flux<BeerDTO> getAll();
    Flux<BeerDTO> getAllByStyle(String style);
    Mono<BeerDTO> getById(String id);
    Mono<BeerDTO> getFirstByName(String name);
    Mono<BeerDTO> save(Mono<BeerDTO> monoBeerDTO);
    Mono<Void> update(String id, Mono<BeerDTO> monoBeerDTO);
    Mono<Void> patch(String id, Mono<Map<String, Object>> monoFields);
    Mono<Void> delete(String id);
}
