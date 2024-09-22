package com.mlorenzo.spring6reactiveexamples.repositories;

import com.mlorenzo.spring6reactiveexamples.domains.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PersonRepositoryImplTest {
    PersonRepository personRepository = new PersonRepositoryImpl();

    @Test
    void testGetByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);
        // No debe hacerse porque estamos bloqueando
        System.out.println(personMono.block());
    }

    @Test
    void testGetByIdSubscriber() {
        Mono<Person> personMono = personRepository.getById(1);
        // Versión simplificada de la expresión "person -> System.out.println(person)"
        personMono.subscribe(System.out::println);
    }

    @Test
    void testMonoMapOperation() {
        Mono<Person> personMono = personRepository.getById(1);
        // Versión simplificada de la expresión "person -> person.getFirstName()"
        personMono.map(Person::getFirstName)
                // Versión simplificada de la expresión "firstName -> System.out.println(firstName)"
                .subscribe(System.out::println);
    }

    @Test
    void testGetAllBlockFirst() {
        Flux<Person> personFlux = personRepository.getAll();
        // No debe hacerse porque estamos bloqueando
        System.out.println(personFlux.blockFirst());
    }

    @Test
    void testGetAllSubscribe() {
        Flux<Person> personFlux = personRepository.getAll();
        // Versión simplificada de la expresión "person -> System.out.println(person)"
        personFlux.subscribe(System.out::println);
    }

    @Test
    void testFluxMapOperation() {
        Flux<Person> personFlux = personRepository.getAll();
        // Versión simplificada de la expresión "person -> person.getFirstName()"
        personFlux.map(Person::getFirstName)
                // Versión simplificada de la expresión "firstName -> System.out.println(firstName)"
                .subscribe(System.out::println);
    }

    @Test
    void testFluxToList() {
        Flux<Person> personFlux = personRepository.getAll();
        // Versión simplificada de la expresión "listPersons -> System.out.println(listPersons)"
        personFlux.collectList().subscribe(System.out::println);
    }

    @Test
    void testFilterOnNameFlux() {
        personRepository.getAll()
                .filter(persons -> persons.getFirstName().equals("Fiona"))
                .subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testFilterOnNameMono() {
        personRepository.getAll()
                .filter(persons -> persons.getFirstName().equals("Fiona")).next()
                .subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testPersonNotFound() {
        final Integer id = 8;
        personRepository.getAll()
                .filter(person -> person.getId().equals(id)).single()
                .doOnError(error -> {
                    System.out.println("Error ocurred in Flux");
                    System.out.println(error.toString());
                })
                // Versión simplificada de la expresión "person -> System.out.println(person)"
                .subscribe(System.out::println,
                        error -> {
                            System.out.println("Error ocurred in the Mono");
                            System.out.println(error.toString());
                        });

    }

    @Test
    void testGetByIdFound() {
        Mono<Person> personMono = personRepository.getById(1);
        // Versión simplificada de la expresión "hasElement -> assertTrue(hasElement)"
        personMono.hasElement().subscribe(Assertions::assertTrue);
    }

    @Test
    void testGetByIdNotFound() {
        Mono<Person> personMono = personRepository.getById(17);
        // Versión simplificada de la expresión "hasElement -> assertFalse(hasElement)"
        personMono.hasElement().subscribe(Assertions::assertFalse);
    }

    @Test
    void testGetByIdFoundStepVerifier() {
        StepVerifier.create(personRepository.getById(3))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetByIdNotFoundStepVerifier() {
        StepVerifier.create(personRepository.getById(70))
                .expectNextCount(0)
                .verifyComplete();
    }

}