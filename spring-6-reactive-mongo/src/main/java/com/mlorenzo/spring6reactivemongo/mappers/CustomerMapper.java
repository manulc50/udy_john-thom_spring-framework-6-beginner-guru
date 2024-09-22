package com.mlorenzo.spring6reactivemongo.mappers;

import com.mlorenzo.spring6reactivemongo.domains.Customer;
import com.mlorenzo.spring6reactivemongo.models.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO customerToCustomerDTO(Customer customer);
    Customer customerDTOToCustomer(CustomerDTO customerDTO);
}
