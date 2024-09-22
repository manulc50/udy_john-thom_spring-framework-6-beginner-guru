package com.mlorenzo.spring6restmvc.services;

import com.mlorenzo.spring6restmvc.exceptions.NotFoundException;
import com.mlorenzo.spring6restmvc.models.CustomerDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final Map<UUID, CustomerDTO> customerMap;

    public CustomerServiceImpl() {
        customerMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        CustomerDTO customer1 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .name("Customer 1")
                .email("customer1@test.com")
                .createdDate(now)
                .updateDate(now)
                .build();
        CustomerDTO customer2 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .name("Customer 2")
                .email("customer2@test.com")
                .createdDate(now)
                .updateDate(now)
                .build();
        CustomerDTO customer3 = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .name("Customer 3")
                .email("customer3@test.com")
                .createdDate(now)
                .updateDate(now)
                .build();
        customerMap.put(customer1.getId(), customer1);
        customerMap.put(customer2.getId(), customer2);
        customerMap.put(customer3.getId(), customer3);
    }

    @Override
    public List<CustomerDTO> getAll() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public CustomerDTO getById(UUID id) {
        Optional<CustomerDTO> oCustomer = Optional.ofNullable(customerMap.get(id));
        // Versión simplificada de la expresión "() -> new NotFoundException()"
        return oCustomer.orElseThrow(NotFoundException::new);
    }

    @Override
    public CustomerDTO save(CustomerDTO customerDto) {
        LocalDateTime now = LocalDateTime.now();
        customerDto.setId(UUID.randomUUID());
        customerDto.setCreatedDate(now);
        customerDto.setUpdateDate(now);
        customerMap.put(customerDto.getId(), customerDto);
        return customerDto;
    }

    @Override
    public void update(UUID id, CustomerDTO customerDto) {
        System.out.println("EEEE");
        CustomerDTO customerDB = getById(id);
        customerDB.setName(customerDto.getName());
        customerDB.setUpdateDate(LocalDateTime.now());
    }

    @Override
    public void patch(UUID id, CustomerDTO customerDto) {
        CustomerDTO customerDB = getById(id);
        boolean isUpdated = false;
        if(StringUtils.hasText(customerDto.getName())) {
            customerDB.setName(customerDto.getName());
            isUpdated = true;
        }
        if(isUpdated)
            customerDB.setUpdateDate(LocalDateTime.now());
    }

    @Override
    public void deleteById(UUID id) {
        customerMap.remove(id);
    }
}
