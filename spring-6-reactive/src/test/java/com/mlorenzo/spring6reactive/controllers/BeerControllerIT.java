package com.mlorenzo.spring6reactive.controllers;

import com.mlorenzo.spring6reactive.domains.Beer;
import com.mlorenzo.spring6reactive.models.BeerDTO;
import com.mlorenzo.spring6reactive.repositories.BeerRepository;
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

import java.math.BigDecimal;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@SpringBootTest
// Esta anotación añade al contexto de Spring un bean de tipo WebTestClient
@AutoConfigureWebTestClient
// Para establecer un orden de ejecución de los tests
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BeerControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    BeerRepository beerRepository;

    @Test
    @Order(1)
    void testGetAll() {
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri("/api/v2/beers")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .expectBody()
                .jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    @Order(2)
    void testGetById() {
        Beer savedBeer = getSavedBeerTest();
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri("/api/v2/beers/{beerId}", savedBeer.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .expectBody(BeerDTO.class);
    }

    @Test
    @Order(3)
    void testGetByIdNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri("/api/v2/beers/{beerId}", 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void testCreate() {
        webTestClient.mutateWith(mockOAuth2Login())
                .post().uri("/api/v2/beers")
                // Versión simplificada de la expresión "() -> getBeerTest()"
                .body(Mono.fromSupplier(this::getBeerTest), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION);
    }

    @Test
    @Order(5)
    void testCreateBadData() {
        BeerDTO beerDTO = getBeerTest();
        beerDTO.setName("");
        webTestClient.mutateWith(mockOAuth2Login())
                .post().uri("/api/v2/beers")
                .body(Mono.just(beerDTO), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(6)
    void testUpdate() {
        Beer savedBeer = getSavedBeerTest();
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri("/api/v2/beers/{beerId}", savedBeer.getId())
                .body(Mono.just(getBeerTest()), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(7)
    void testUpdateBadData() {
        Beer savedBeer = getSavedBeerTest();
        BeerDTO beerDTO = getBeerTest();
        beerDTO.setStyle("");
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri("/api/v2/beers/{beerId}", savedBeer.getId())
                .body(Mono.just(beerDTO), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(8)
    void testUpdateNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri("/api/v2/beers/{beerId}", 999)
                .body(Mono.just(getBeerTest()), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(9)
    void testPatchNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .patch().uri("/api/v2/beers/{beerId}", 999)
                .body(Mono.just(getBeerTest()), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(10)
    void testDelete() {
        Beer savedBeer = getSavedBeerTest();
        webTestClient.mutateWith(mockOAuth2Login())
                .delete().uri("/api/v2/beers/{beerId}", savedBeer.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(11)
    void testDeleteNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .delete().uri("/api/v2/beers/{beerId}", 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    private BeerDTO getBeerTest() {
        return BeerDTO.builder()
                .name("Space Dust")
                .style("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("123123")
                .build();
    }

    private Beer getSavedBeerTest() {
        Beer beer = Beer.builder()
                .name("Space Dust")
                .style("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("123123")
                .build();
        return beerRepository.save(beer).block();
    }
}