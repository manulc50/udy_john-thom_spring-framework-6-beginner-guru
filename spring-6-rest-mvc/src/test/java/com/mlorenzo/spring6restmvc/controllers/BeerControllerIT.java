package com.mlorenzo.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlorenzo.spring6restmvc.entities.Beer;
import com.mlorenzo.spring6restmvc.exceptions.NotFoundException;
import com.mlorenzo.spring6restmvc.mappers.BeerMapper;
import com.mlorenzo.spring6restmvc.models.BeerDTO;
import com.mlorenzo.spring6restmvc.models.BeerStyle;
import com.mlorenzo.spring6restmvc.repositories.BeerRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class BeerControllerIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // Se comentan porque ya no usamos Autenticación Básica
    /*@Value("${spring.security.user.name}")
    String username;

    @Value("${spring.security.user.password}")
    String password;*/

    // Nota: Se han anotado los test que hacen modificaciones en la base de datos con las anotaciones @Transactional
    // y @Rollback para que esas modicaciones no se lleven a cabo en la base de datos al finalizar las ejecuciones de
    // cada uno de ellos y así evitar que afecten a otros test.

    @BeforeEach
    void setUp() {
        // Configuramos MockMvc con el Web Application Context para que tenga en cuenta el bean(s) que realiza
        // las validaciones tanto a nivel de controlador(DTO) como a nivel de persistencia(JPA).
        // Además, aplicamos el filtro de Spring Security
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    void listBeersByNameTest() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("name", "IPA")
                        .queryParam("pageSize", "800")
                        // Se comenta porque ahora esta aplicación es un servidor de recursos OAuth2 que usa tokens JWT
                        //.with(httpBasic(username, password)))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(336)));
    }

    @Test
    void listBeersByStyleTest() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("style", BeerStyle.IPA.name())
                        .queryParam("pageSize", "800")
                        //.with(httpBasic(username, password)))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(548)));
    }

    @Test
    void listBeersByNameAndStyleTest() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("name", "IPA")
                        .queryParam("style",  BeerStyle.IPA.name())
                        .queryParam("pageSize", "800")
                        //.with(httpBasic(username, password)))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(310)));
    }

    @Test
    void listBeersByNameAndStyleAndShowInventoryTest() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("name", "IPA")
                        .queryParam("style",  BeerStyle.IPA.name())
                        .queryParam("showInventory", "true")
                        .queryParam("pageSize", "800")
                        //.with(httpBasic(username, password)))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(310)))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void listBeersByNameAndStyleAndNotShowInventoryTest() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("name", "IPA")
                        .queryParam("style",  BeerStyle.IPA.name())
                        .queryParam("showInventory", "false")
                        .queryParam("pageSize", "800")
                        //.with(httpBasic(username, password)))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(310)))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.nullValue()));
    }

    @Test
    void listBeersByNameAndStyleAndShowInventoryWithPaginationTest() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("name", "IPA")
                        .queryParam("style",  BeerStyle.IPA.name())
                        .queryParam("showInventory", "true")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "50")
                        //.with(httpBasic(username, password)))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(50)))
                .andExpect(jsonPath("$.content.[0].quantityOnHand").value(IsNull.notNullValue()));
    }

    @Test
    void patchBeerBadNameTest() throws Exception {
        Beer beer = beerRepository.findAll().get(0);
        Map<String, Object> beerMap = new HashMap<>();
        // Nombre demasiado largo(Más de 50 caracteres)
        final String beerName = "beerName 012345678901234567890123456789012345678901234567890123456789";
        beerMap.put("name", beerName);
        mockMvc.perform(patch(BeerController.BEER_PATH_ID, beer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap))
                        //.with(httpBasic(username, password)))
                        .with(BeerWebMvcIT.JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    void noAuthTest() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .queryParam("name", "IPA")
                        .queryParam("pageSize", "800"))
                .andExpect(status().isUnauthorized());
    }

    // Nota: Los test que hay abajo no tienen en cuenta las validaciones porque no usan el MockMvc configurado con
    // el Web Application Context

    @Test
    void listBeersTest() {
        Page<BeerDTO> beerPage = beerController.getAll(null, null, null,
                1,2413);
        assertThat(beerPage.getContent().size()).isEqualTo(1000);
    }

    @Rollback
    @Transactional
    @Test
    void emptyListBeersTest() {
        beerRepository.deleteAll();
        Page<BeerDTO> beerPage = beerController.getAll(null, null, null,
                1, 25);
        assertThat(beerPage.getContent().size()).isEqualTo(0);
    }

    @Test
    void getByIdTest() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDto = beerController.getById(beer.getId());
        assertThat(beerDto).isNotNull();
        assertThat(beerDto.getId()).isEqualTo(beer.getId());
    }

    @Test
    void getByIdNotFoundTest() {
        assertThrows(NotFoundException.class, () -> beerController.getById(UUID.randomUUID()));
    }

    @Rollback
    @Transactional
    @Test
    void saveNewBeerTest() {
        // El objeto de tipo BeerDTO a crear va sin los campos "id", "version", "createdDate" y "updateDate"
        BeerDTO beerDTO = BeerDTO.builder()
                .name("New Beer")
                .build();
        ResponseEntity<Void> responseEntity = beerController.create(beerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        String[] locationSplit = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID savedUUID = UUID.fromString(locationSplit[4]);
        Optional<Beer> oSavedBeer = beerRepository.findById(savedUUID);
        assertTrue(oSavedBeer.isPresent());
    }

    @Rollback
    @Transactional
    @Test
    void updateExistingBeerTest() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);
        // El objeto de tipo BeerDTO a actualizar va sin los campos "id", "createdDate" y "updateDate"
        beerDTO.setId(null);
        beerDTO.setCreatedDate(null);
        beerDTO.setUpdateDate(null);
        final String beerName = "UPDATED";
        beerDTO.setName(beerName);
        beerController.update(beer.getId(), beerDTO);
        Optional<Beer> oUpdatedBeer = beerRepository.findById(beer.getId());
        assertThat(oUpdatedBeer.isPresent()).isTrue();
        assertThat(oUpdatedBeer.get().getName()).isEqualTo(beerName);
    }

    @Test
    void updateBeerNotFoundTest() {
        assertThrows(NotFoundException.class,
                () -> beerController.update(UUID.randomUUID(), BeerDTO.builder().build()));
    }

    @Rollback
    @Transactional
    @Test
    void deleteExistingBeerTest() {
        Beer beer = beerRepository.findAll().get(0);
        beerController.deleteById(beer.getId());
        Optional<Beer> oFoundBeer = beerRepository.findById(beer.getId());
        assertFalse(oFoundBeer.isPresent());
    }

    @Test
    void deleteBeerNotFoundTest() {
        assertThrows(NotFoundException.class, () -> beerController.deleteById(UUID.randomUUID()));
    }
}