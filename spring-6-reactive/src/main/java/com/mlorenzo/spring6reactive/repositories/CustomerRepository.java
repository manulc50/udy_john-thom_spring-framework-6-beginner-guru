package com.mlorenzo.spring6reactive.repositories;

import com.mlorenzo.spring6reactive.domains.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}
