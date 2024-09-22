package com.mlorenzo.spring6restmvc.services;

import com.mlorenzo.spring6restmvc.entities.Customer;
import com.mlorenzo.spring6restmvc.exceptions.NotFoundException;
import com.mlorenzo.spring6restmvc.mappers.CustomerMapper;
import com.mlorenzo.spring6restmvc.models.CustomerDTO;
import com.mlorenzo.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPAImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDTO> getAll() {
        return customerMapper.customersListToCustomersDtoList(customerRepository.findAll());
    }

    @Override
    public CustomerDTO getById(UUID id) {
        Optional<Customer> oCustomer = customerRepository.findById(id);
        if(oCustomer.isEmpty())
            throw new NotFoundException();
        return customerMapper.customerToCustomerDto(oCustomer.get());
    }

    @Override
    public CustomerDTO save(CustomerDTO customerDto) {
        return customerMapper.customerToCustomerDto(customerRepository
                .save(customerMapper.customerDtoToCustomer(customerDto)));
    }

    @Override
    public void update(UUID id, CustomerDTO customerDto) {
        Optional<Customer> oCustomer = customerRepository.findById(id);
        if(oCustomer.isPresent()) {
            final Customer customer = oCustomer.get();
            BeanUtils.copyProperties(customerDto, customer , "id", "version", "createdDate", "updateDate");
            customerRepository.save(customer);
        }
        else
            throw new NotFoundException();
    }

    @Override
    public void patch(UUID id, CustomerDTO customerDto) {
        customerRepository.findById(id)
                .ifPresentOrElse(customer -> {
                    final String customerName = customerDto.getName();
                    boolean isUpdated = false;
                    if(StringUtils.hasText(customerName)) {
                        customer.setName(customerName);
                        isUpdated = true;
                    }
                    if(isUpdated)
                        customerRepository.save(customer);
                },() -> { throw new NotFoundException(); });
    }

    @Override
    public void deleteById(UUID id) {
        if(customerRepository.existsById(id))
            customerRepository.deleteById(id);
        else
            throw new NotFoundException();
    }
}
