package com.mlorenzo.spring6reactiveexamples.repositories;

import com.mlorenzo.spring6reactiveexamples.domains.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository {
    Mono<Person> getById(final Integer id);
    Flux<Person> getAll();
}
