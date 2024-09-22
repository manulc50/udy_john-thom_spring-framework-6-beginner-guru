package com.mlorenzo.spring6restmvc.mappers;

import com.mlorenzo.spring6restmvc.entities.Customer;
import com.mlorenzo.spring6restmvc.models.CustomerDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface CustomerMapper {
    Customer customerDtoToCustomer(CustomerDTO customerDto);
    CustomerDTO customerToCustomerDto(Customer customer);
    List<Customer> customersDtoListToCustomersList(List<CustomerDTO> customersDTO);
    List<CustomerDTO> customersListToCustomersDtoList(List<Customer> customers);
}
