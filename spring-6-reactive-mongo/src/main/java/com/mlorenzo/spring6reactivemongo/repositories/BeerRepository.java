package com.mlorenzo.spring6reactivemongo.repositories;

import com.mlorenzo.spring6reactivemongo.domains.Beer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BeerRepository extends ReactiveMongoRepository<Beer, String> {
    Mono<Beer> findFirstByName(String name);

    // También puede ser usar el nombre del método "findAllByStyle"
    Flux<Beer> findByStyle(String style);
}
