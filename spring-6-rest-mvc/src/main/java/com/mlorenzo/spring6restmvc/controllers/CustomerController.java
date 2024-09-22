package com.mlorenzo.spring6restmvc.controllers;

import com.mlorenzo.spring6restmvc.models.CustomerDTO;
import com.mlorenzo.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    public static final String CUSTOMER_PATH = "/api/v1/customers";
    public static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{customerId}";

    private final CustomerService customerService;

    @GetMapping(CUSTOMER_PATH)
    public List<CustomerDTO> getAll() {
        return customerService.getAll();
    }

    @GetMapping(CUSTOMER_PATH_ID)
    public CustomerDTO getById(@PathVariable UUID customerId) {
        return customerService.getById(customerId);
    }

    // Nota:Para realizar las validaciones de beans(DTO's), puede usarse tanto la anotación de Jakarta @Valid como la
    // anotación de Spring @Validated

    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity<Void> create(@RequestBody @Validated CustomerDTO customerDTO) {
        CustomerDTO savedCustomer = customerService.save(customerDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, String.format("%s/%s", CUSTOMER_PATH, savedCustomer.getId().toString()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<Void> update(@PathVariable("customerId") UUID id,
                                       @RequestBody @Validated CustomerDTO customerDTO) {
        customerService.update(id, customerDTO);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(CUSTOMER_PATH_ID)
    public void patch(@PathVariable(value = "customerId") UUID id, @RequestBody CustomerDTO customerDTO) {
        customerService.patch(id, customerDTO);
    }

    @DeleteMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<Void> deleteById(@PathVariable(name = "customerId") UUID id) {
        customerService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
