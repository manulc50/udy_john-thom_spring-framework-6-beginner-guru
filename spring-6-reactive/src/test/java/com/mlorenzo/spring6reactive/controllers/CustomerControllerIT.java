package com.mlorenzo.spring6reactive.controllers;

import com.mlorenzo.spring6reactive.domains.Customer;
import com.mlorenzo.spring6reactive.models.CustomerDTO;
import com.mlorenzo.spring6reactive.repositories.CustomerRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@SpringBootTest
// Esta anotación añade al contexto de Spring un bean de tipo WebTestClient
@AutoConfigureWebTestClient
// Para establecer un orden de ejecución de los tests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @Order(1)
    void testGetAll() {
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri("/api/v2/customers")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .expectBody()
                .jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    @Order(2)
    void testGetById() {
        Customer savedCustomer = getSavedCustomerTest();
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(uriBuilder -> uriBuilder.path("/api/v2/customers/{customerId}").build(savedCustomer.getId()))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .expectBody(CustomerDTO.class);
    }

    @Test
    @Order(3)
    void testGetByIdNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(uriBuilder -> uriBuilder.path("/api/v2/customers/{customerId}").build(999))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void testCreate() {
        webTestClient.mutateWith(mockOAuth2Login())
                .post().uri("/api/v2/customers")
                // Versión simplificada de la expresión "() -> getBeerTest()"
                .body(Mono.fromSupplier(this::getCustomerTest), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION);
    }

    @Test
    @Order(5)
    void testCreateBadData() {
        CustomerDTO customerDTO = getCustomerTest();
        customerDTO.setName("");
        webTestClient.mutateWith(mockOAuth2Login())
                .post().uri("/api/v2/customers")
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(6)
    void testUpdate() {
        Customer savedCustomer = getSavedCustomerTest();
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri(uriBuilder -> uriBuilder.path("/api/v2/customers/{customerId}").build(savedCustomer.getId()))
                .body(Mono.just(getCustomerTest()), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(7)
    void testUpdateBadData() {
        Customer savedCustomer = getSavedCustomerTest();
        CustomerDTO customerDTO = getCustomerTest();
        customerDTO.setName("");
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri(uriBuilder -> uriBuilder.path("/api/v2/customers/{customerId}").build(savedCustomer.getId()))
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(8)
    void testUpdateNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri(uriBuilder -> uriBuilder.path("/api/v2/customers/{customerId}").build(999))
                .body(Mono.just(getCustomerTest()), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(9)
    void testPatchNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .patch().uri(uriBuilder -> uriBuilder.path("/api/v2/customers/{customerId}").build(999))
                .body(Mono.just(getCustomerTest()), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(10)
    void testDelete() {
        Customer savedCustomer = getSavedCustomerTest();
        webTestClient.mutateWith(mockOAuth2Login())
                .delete().uri(uriBuilder -> uriBuilder.path("/api/v2/customers/{customerId}").build(savedCustomer.getId()))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(11)
    void testDeleteNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .delete().uri(uriBuilder -> uriBuilder.path("/api/v2/customers/{customerId}").build(999))
                .exchange()
                .expectStatus().isNotFound();
    }

    private CustomerDTO getCustomerTest() {
        return CustomerDTO.builder()
                .name("Jhon Doe")
                .build();
    }

    private Customer getSavedCustomerTest() {
        Customer customer = Customer.builder()
                .name("Jhon Doe")
                .build();
        return customerRepository.save(customer).block();
    }
}