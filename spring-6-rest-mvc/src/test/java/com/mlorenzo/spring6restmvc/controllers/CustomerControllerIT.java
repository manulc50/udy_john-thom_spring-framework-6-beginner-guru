package com.mlorenzo.spring6restmvc.controllers;

import com.mlorenzo.spring6restmvc.entities.Customer;
import com.mlorenzo.spring6restmvc.exceptions.NotFoundException;
import com.mlorenzo.spring6restmvc.mappers.CustomerMapper;
import com.mlorenzo.spring6restmvc.models.CustomerDTO;
import com.mlorenzo.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class CustomerControllerIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerMapper customerMapper;

    @Test
    void listCustomersTest() {
        List<CustomerDTO> listCustomers = customerController.getAll();
        assertEquals(3, listCustomers.size());
    }

    // Nota: Se han anotado los test que hacen modificaciones en la base de datos con las anotaciones @Transactional
    // y @Rollback para que esas modicaciones no se lleven a cabo en la base de datos al finalizar las ejecuciones de
    // cada uno de ellos y así evitar que afecten a otros test.

    @Rollback
    @Transactional
    @Test
    void emptyCustomersListTest() {
        customerRepository.deleteAll();
        List<CustomerDTO> listCustomers = customerController.getAll();
        assertEquals(0, listCustomers.size());
    }

    @Test
    void getByIdTest() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO customerDTO = customerController.getById(customer.getId());
        assertNotNull(customerDTO);
        assertEquals(customer.getId(), customerDTO.getId());
    }

    @Test
    void getByIdNotFoundTest() {
        assertThrows(NotFoundException.class, () -> customerController.getById(UUID.randomUUID()));
    }

    @Rollback
    @Transactional
    @Test
    void saveNewCustomerTest() {
        // El objeto de tipo CustomerDTO a crear va sin los campos "id", "createdDate" y "updateDate"
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("New Customer")
                .build();
        ResponseEntity<Void> responseEntity = customerController.create(customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        String[] locationSplit = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationSplit[4]);
        Optional<Customer> oSavedCustomer = customerRepository.findById(savedUUID);
        assertThat(oSavedCustomer.isPresent()).isTrue();
    }

    @Rollback
    @Transactional
    @Test
    void updateExistingCustomer() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO customerDTO = customerMapper.customerToCustomerDto(customer);
        // El objeto de tipo CustomerDTO a actualizar va sin los campos "id", "createdDate" y "updateDate"
        customerDTO.setId(null);
        customerDTO.setCreatedDate(null);
        customerDTO.setUpdateDate(null);
        final String customerName = "UPDATED";
        customerDTO.setName(customerName);
        ResponseEntity<Void> responseEntity = customerController.update(customer.getId(), customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Optional<Customer> oUpdatedCustomer = customerRepository.findById(customer.getId());
        assertThat(oUpdatedCustomer.isPresent()).isTrue();
        assertThat(oUpdatedCustomer.get().getName()).isEqualTo(customerName);
    }

    @Test
    void updateCustomerNotFoundTest() {
        assertThrows(NotFoundException.class,
                () -> customerController.update(UUID.randomUUID(), CustomerDTO.builder().build()));
    }

    @Rollback
    @Transactional
    @Test
    void patchExistingCustomer() {
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO customerDTO = customerMapper.customerToCustomerDto(customer);
        // El objeto de tipo CustomerDTO a actualizar va sin los campos "id", "createdDate" y "updateDate"
        customerDTO.setId(null);
        customerDTO.setCreatedDate(null);
        customerDTO.setUpdateDate(null);
        final String customerName = "UPDATED";
        customerDTO.setName(customerName);
        customerController.patch(customer.getId(), customerDTO);
        Optional<Customer> oPatchedCustomer = customerRepository.findById(customer.getId());
        assertThat(oPatchedCustomer.isPresent()).isTrue();
        assertThat(oPatchedCustomer.get().getName()).isEqualTo(customerName);
    }

    @Test
    void patchCustomerNotFoundTest() {
        assertThrows(NotFoundException.class,
                () -> customerController.patch(UUID.randomUUID(), CustomerDTO.builder().build()));
    }

    @Rollback
    @Transactional
    @Test
    void deleteExistingCustomerTest() {
        Customer customer = customerRepository.findAll().get(0);
        ResponseEntity<Void> responseEntity = customerController.deleteById(customer.getId());
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Optional<Customer> oFoundCustomer = customerRepository.findById(customer.getId());
        assertTrue(oFoundCustomer.isEmpty());
    }

    @Test
    void deleteCustomerNotFoundTest() {
        assertThrows(NotFoundException.class, () -> customerController.deleteById(UUID.randomUUID()));
    }
}