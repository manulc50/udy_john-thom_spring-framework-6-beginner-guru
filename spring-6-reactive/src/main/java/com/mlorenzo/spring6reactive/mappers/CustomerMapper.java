package com.mlorenzo.spring6reactive.mappers;

import com.mlorenzo.spring6reactive.domains.Customer;
import com.mlorenzo.spring6reactive.models.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer customerDTOToCustomerEntity(CustomerDTO customerDTO);
    CustomerDTO customerEntityToCustomerDTO(Customer customer);
}
