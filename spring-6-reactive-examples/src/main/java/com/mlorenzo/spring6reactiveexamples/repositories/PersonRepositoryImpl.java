package com.mlorenzo.spring6reactiveexamples.repositories;

import com.mlorenzo.spring6reactiveexamples.domains.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonRepositoryImpl implements PersonRepository {
    private final Person michael = Person.builder().id(1).firstName("Michael").lastName("Weston").build();
    private final Person fiona = Person.builder().id(2).firstName("Fiona").lastName("Glenanne").build();
    private final Person sam = Person.builder().id(3).firstName("Sam").lastName("Axe").build();
    private final Person jesse = Person.builder().id(4).firstName("Jesse").lastName("Porter").build();

    @Override
    public Mono<Person> getById(final Integer id) {
        return getAll().filter(person -> person.getId().equals(id)).next();
    }

    @Override
    public Flux<Person> getAll() {
        return Flux.just(michael, fiona, sam, jesse);
    }
}
