package com.mlorenzo.spring6reactivemongo.web.fn;

import com.mlorenzo.spring6reactivemongo.domains.Customer;
import com.mlorenzo.spring6reactivemongo.models.BeerDTO;
import com.mlorenzo.spring6reactivemongo.models.CustomerDTO;
import com.mlorenzo.spring6reactivemongo.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@SpringBootTest
@AutoConfigureWebTestClient
class CustomerRouterConfigIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void getAllTest() {
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    @Test
    void getAllByStyleTest() {
        Customer savedCustomer = getSavedCustomerTest("TEST");
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(UriComponentsBuilder
                        .fromPath(CustomerRouterConfig.CUSTOMER_PATH)
                        .queryParam("name", savedCustomer.getName())
                        .build().toUri())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .expectBody().jsonPath("$.size()").value(equalTo(1));
    }

    @Test
    void getByIdTest() {
        Customer savedCustomer = getSavedCustomerTest("Customer 5");
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(CustomerRouterConfig.CUSTOMER_ID_PATH, savedCustomer.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .expectBody(BeerDTO.class);
    }

    @Test
    void testGetByIdNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(CustomerRouterConfig.CUSTOMER_ID_PATH, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreate() {
        webTestClient.mutateWith(mockOAuth2Login())
                .post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                // Versión simplificada de la expresión "() -> getCustomerTest()"
                .body(Mono.fromSupplier(this::getCustomerTest), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION);
    }

    @Test
    void testCreateBadData() {
        CustomerDTO customerDTO = getCustomerTest();
        customerDTO.setName("");
        webTestClient.mutateWith(mockOAuth2Login())
                .post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdate() {
        Customer savedCustomer = getSavedCustomerTest("Customer 5");
        CustomerDTO customerDTO = getCustomerTest();
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri(CustomerRouterConfig.CUSTOMER_ID_PATH, savedCustomer.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testUpdateBadData() {
        Customer savedCustomer = getSavedCustomerTest("Customer 5");
        CustomerDTO customerDTO = getCustomerTest();
        customerDTO.setName("");
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri(CustomerRouterConfig.CUSTOMER_ID_PATH, savedCustomer.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri(CustomerRouterConfig.CUSTOMER_ID_PATH, 999)
                .body(Mono.just(getCustomerTest()), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testPatch() {
        Customer savedCustomer = getSavedCustomerTest("Customer 5");
        webTestClient.mutateWith(mockOAuth2Login())
                .patch().uri(CustomerRouterConfig.CUSTOMER_ID_PATH, savedCustomer.getId())
                .body(Mono.just(getCustomerTest()), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testPatchNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .patch().uri(CustomerRouterConfig.CUSTOMER_ID_PATH, 999)
                .body(Mono.just(getCustomerTest()), CustomerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDelete() {
        Customer savedCustomer = getSavedCustomerTest("Customer 5");
        webTestClient.mutateWith(mockOAuth2Login())
                .delete().uri(CustomerRouterConfig.CUSTOMER_ID_PATH, savedCustomer.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .delete().uri(CustomerRouterConfig.CUSTOMER_ID_PATH, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    private CustomerDTO getCustomerTest() {
        return CustomerDTO.builder()
                .name("Customer 4")
                .build();
    }

    private Customer getSavedCustomerTest(final String name) {
        Customer customer = Customer.builder()
                .name(name)
                .build();
        return customerRepository.save(customer).block();
    }

}