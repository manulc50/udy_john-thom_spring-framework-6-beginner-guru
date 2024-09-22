package com.mlorenzo.spring6restmvc.services;

import com.mlorenzo.spring6restmvc.models.CustomerDTO;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDTO> getAll();
    CustomerDTO getById(UUID id);
    CustomerDTO save(CustomerDTO customerDto);
    void update(UUID id, CustomerDTO customerDto);
    void patch(UUID id, CustomerDTO customerDto);
    void deleteById(UUID id);
}
