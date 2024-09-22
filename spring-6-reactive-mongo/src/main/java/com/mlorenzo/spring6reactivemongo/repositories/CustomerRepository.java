package com.mlorenzo.spring6reactivemongo.repositories;

import com.mlorenzo.spring6reactivemongo.domains.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
    // También puede ser usar el nombre del método "findByName"
    Flux<Customer> findAllByName(String name);
}
