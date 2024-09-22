package com.mlorenzo.spring6reactive.services;

import com.mlorenzo.spring6reactive.mappers.CustomerMapper;
import com.mlorenzo.spring6reactive.models.CustomerDTO;
import com.mlorenzo.spring6reactive.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Flux<CustomerDTO> findAll() {
        return customerRepository.findAll()
                // Versión simplificada de la expresión "customer -> customerMapper.customerEntityToCustomerDTO(customer)"
                .map(customerMapper::customerEntityToCustomerDTO);
    }

    @Override
    public Mono<CustomerDTO> findById(Integer id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                // Versión simplificada de la expresión "customer -> customerMapper.customerEntityToCustomerDTO(customer)"
                .map(customerMapper::customerEntityToCustomerDTO);
    }

    @Override
    public Mono<CustomerDTO> create(CustomerDTO customerDTO) {
        return customerRepository.save(customerMapper.customerDTOToCustomerEntity(customerDTO))
                // Versión simplificada de la expresión "customer -> customerMapper.customerEntityToCustomerDTO(customer)"
                .map(customerMapper::customerEntityToCustomerDTO);
    }

    @Override
    public Mono<Void> update(Integer id, CustomerDTO customerDTO) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(customer -> {
                    BeanUtils.copyProperties(customerDTO, customer, "id", "createdDate", "lastModifiedDate");
                    return customerRepository.save(customer);
                })
                .then();
    }

    @Override
    public Mono<Void> patch(Integer id, CustomerDTO customerDTO) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(customer -> {
                    if(StringUtils.hasText(customerDTO.getName()))
                        customer.setName(customerDTO.getName());
                    return customerRepository.save(customer);
                })
                .then();
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return customerRepository.existsById(id)
                .flatMap(isExists -> isExists
                        ? customerRepository.deleteById(id)
                        : Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))
                );
    }
}
