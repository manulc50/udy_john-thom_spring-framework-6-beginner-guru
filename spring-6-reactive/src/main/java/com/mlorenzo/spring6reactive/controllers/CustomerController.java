package com.mlorenzo.spring6reactive.controllers;

import com.mlorenzo.spring6reactive.models.CustomerDTO;
import com.mlorenzo.spring6reactive.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/customers")
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public Flux<CustomerDTO> finAll() {
        return customerService.findAll();
    }

    @GetMapping("/{customerId}")
    public Mono<CustomerDTO> findById(@PathVariable("customerId") Integer id) {
        return customerService.findById(id);
    }


    // Nota:Para realizar las validaciones de beans(DTO's), puede usarse tanto la anotación de Jakarta @Valid como la
    // anotación de Spring @Validated

    @PostMapping
    public Mono<ResponseEntity<Void>> create(@RequestBody @Valid CustomerDTO customerDTO) {
        return customerService.create(customerDTO)
                .map(savedCustomerDTO -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set(HttpHeaders.LOCATION, String.format("/api/v2/customers/%d", savedCustomerDTO.getId()));
                    return new ResponseEntity<>(headers, HttpStatus.CREATED);
                });
    }

    @PutMapping("/{customerId}")
    public Mono<ResponseEntity<Void>> update(@PathVariable(name = "customerId") Integer id,
                                             @Valid @RequestBody CustomerDTO customerDTO) {
        return customerService.update(id, customerDTO)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Void>> patch(@PathVariable Integer id, @RequestBody CustomerDTO customerDTO) {
        return customerService.patch(id, customerDTO)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable Integer id) {
        return customerService.deleteById(id);
    }
}
