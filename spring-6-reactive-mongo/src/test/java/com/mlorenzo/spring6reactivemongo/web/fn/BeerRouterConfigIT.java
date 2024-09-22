package com.mlorenzo.spring6reactivemongo.web.fn;

import com.mlorenzo.spring6reactivemongo.domains.Beer;
import com.mlorenzo.spring6reactivemongo.models.BeerDTO;
import com.mlorenzo.spring6reactivemongo.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@SpringBootTest
@AutoConfigureWebTestClient
class BeerRouterConfigIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    BeerRepository beerRepository;

    @Test
    void getAllTest() {
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(BeerRouterConfig.BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    @Test
    void getAllByStyleTest() {
        Beer savedBeer = getSavedBeerTest("TEST");
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(UriComponentsBuilder
                        .fromPath(BeerRouterConfig.BEER_PATH)
                        .queryParam("style", savedBeer.getStyle())
                        .build().toUri())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .expectBody().jsonPath("$.size()").value(equalTo(1));
    }

    @Test
    void getByIdTest() {
        Beer savedBeer = getSavedBeerTest("IPA");
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(BeerRouterConfig.BEER_ID_PATH, savedBeer.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .expectBody(BeerDTO.class);
    }

    @Test
    void testGetByIdNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .get().uri(BeerRouterConfig.BEER_ID_PATH, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreate() {
        webTestClient.mutateWith(mockOAuth2Login())
                .post().uri(BeerRouterConfig.BEER_PATH)
                // Versión simplificada de la expresión "() -> getBeerTest()"
                .body(Mono.fromSupplier(this::getBeerTest), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION);
    }

    @Test
    void testCreateBadData() {
        BeerDTO beerDTO = getBeerTest();
        beerDTO.setName("");
        webTestClient.mutateWith(mockOAuth2Login())
                .post().uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(beerDTO), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdate() {
        Beer savedBeer = getSavedBeerTest("IPA");
        BeerDTO beerDTO = getBeerTest();
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri(BeerRouterConfig.BEER_ID_PATH, savedBeer.getId())
                .body(Mono.just(beerDTO), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testUpdateBadData() {
        Beer savedBeer = getSavedBeerTest("IPA");
        BeerDTO beerDTO = getBeerTest();
        beerDTO.setStyle("");
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri(BeerRouterConfig.BEER_ID_PATH, savedBeer.getId())
                .body(Mono.just(beerDTO), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .put().uri(BeerRouterConfig.BEER_ID_PATH, 999)
                .body(Mono.just(getBeerTest()), BeerDTO.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testPatch() {
        Beer savedBeer = getSavedBeerTest("IPA");
        Map<String, Object> mapFields = Map.of("name", "New Beer Name");
        webTestClient.mutateWith(mockOAuth2Login())
                .patch().uri(BeerRouterConfig.BEER_ID_PATH, savedBeer.getId())
                .body(Mono.just(mapFields), Map.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testPatchNotFound() {
        Map<String, Object> mapFields = Map.of("name", "New Beer Name");
        webTestClient.mutateWith(mockOAuth2Login())
                .patch().uri(BeerRouterConfig.BEER_ID_PATH, 999)
                .body(Mono.just(mapFields), Map.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDelete() {
        Beer savedBeer = getSavedBeerTest("IPA");
        webTestClient.mutateWith(mockOAuth2Login())
                .delete().uri(BeerRouterConfig.BEER_ID_PATH, savedBeer.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteNotFound() {
        webTestClient.mutateWith(mockOAuth2Login())
                .delete().uri(BeerRouterConfig.BEER_ID_PATH, 999)
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

    private Beer getSavedBeerTest(final String style) {
        Beer beer = Beer.builder()
                .name("Space Dust")
                .style(style)
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("123123")
                .build();
        return beerRepository.save(beer).block();
    }
}