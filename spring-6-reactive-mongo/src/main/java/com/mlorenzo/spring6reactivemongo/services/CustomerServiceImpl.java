package com.mlorenzo.spring6reactivemongo.services;

import com.mlorenzo.spring6reactivemongo.mappers.CustomerMapper;
import com.mlorenzo.spring6reactivemongo.models.CustomerDTO;
import com.mlorenzo.spring6reactivemongo.repositories.CustomerRepository;
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
    public Flux<CustomerDTO> getAll() {
        return customerRepository.findAll()
                // Versión simplificada de la expresión "customer -> customerMapper.customerToCustomerDTO(customer)"
                .map(customerMapper::customerToCustomerDTO);
    }

    @Override
    public Flux<CustomerDTO> getAllByName(String name) {
        return customerRepository.findAllByName(name)
                // Versión simplificada de la expresión "customer -> customerMapper.customerToCustomerDTO(customer)"
                .map(customerMapper::customerToCustomerDTO);
    }

    @Override
    public Mono<CustomerDTO> getById(String id) {
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                // Versión simplificada de la expresión "customer -> customerMapper.customerToCustomerDTO(customer)"
                .map(customerMapper::customerToCustomerDTO);
    }

    @Override
    public Mono<CustomerDTO> save(Mono<CustomerDTO> monoBeerDTO) {
        // Versión simplificada de la expresión "customerDTO -> customerMapper.customerDTOToCustomer(customerDTO)"
        return monoBeerDTO.map(customerMapper::customerDTOToCustomer)
                // Versión simplificada de la expresión "customer -> customerRepository.save(customer)"
                .flatMap(customerRepository::save)
                // Versión simplificada de la expresión "customer -> customerMapper.customerToCustomerDTO(customer)"
                .map(customerMapper::customerToCustomerDTO);

    }

    @Override
    public Mono<Void> update(String id, Mono<CustomerDTO> monoCustomerDTO) {
        return monoCustomerDTO.flatMap(customerDTO ->
                customerRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .flatMap(customer -> {
                            BeanUtils.copyProperties(customerDTO, customer, "id", "createdDate", "lastModifiedDate");
                            return customerRepository.save(customer);
                        })
        ).then();
    }

    @Override
    public Mono<Void> patch(String id, Mono<CustomerDTO> monoCustomerDTO) {
        return monoCustomerDTO.flatMap(customerDTO ->
                customerRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .flatMap(customer -> {
                            if(StringUtils.hasText(customerDTO.getName()))
                                customer.setName(customerDTO.getName());
                            return customerRepository.save(customer);
                        })
        ).then();
    }

    @Override
    public Mono<Void> delete(String id) {
        return customerRepository.existsById(id)
                .flatMap(isExists -> isExists
                        ? customerRepository.deleteById(id)
                        : Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                );
    }
}
