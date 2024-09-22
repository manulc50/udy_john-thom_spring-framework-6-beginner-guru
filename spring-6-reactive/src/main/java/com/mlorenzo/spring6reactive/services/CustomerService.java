package com.mlorenzo.spring6reactive.services;

import com.mlorenzo.spring6reactive.models.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerDTO> findAll();
    Mono<CustomerDTO> findById(Integer id);
    Mono<CustomerDTO> create(CustomerDTO customerDTO);
    Mono<Void> update(Integer id, CustomerDTO customerDTO);
    Mono<Void> patch(Integer id, CustomerDTO customerDTO);
    Mono<Void> deleteById(Integer id);
}
