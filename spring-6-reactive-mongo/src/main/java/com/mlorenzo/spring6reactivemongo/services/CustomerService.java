package com.mlorenzo.spring6reactivemongo.services;

import com.mlorenzo.spring6reactivemongo.models.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerDTO> getAll();
    Flux<CustomerDTO> getAllByName(String name);
    Mono<CustomerDTO> getById(String id);
    //Mono<CustomerDTO> getFirstByName(String name);
    Mono<CustomerDTO> save(Mono<CustomerDTO> monoBeerDTO);
    Mono<Void> update(String id, Mono<CustomerDTO> monoCustomerDTO);
    Mono<Void> patch(String id, Mono<CustomerDTO> monoCustomerDTO);
    Mono<Void> delete(String id);
}
