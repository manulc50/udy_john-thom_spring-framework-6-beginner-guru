package com.mlorenzo.spring6restmvc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlorenzo.spring6restmvc.config.SecurityConfig;
import com.mlorenzo.spring6restmvc.exceptions.NotFoundException;
import com.mlorenzo.spring6restmvc.models.BeerDTO;
import com.mlorenzo.spring6restmvc.services.BeerService;
import com.mlorenzo.spring6restmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.*;

@WebMvcTest(BeerController.class)
@Import(SecurityConfig.class)
class BeerWebMvcIT {
    static final JwtRequestPostProcessor JWT_REQUEST_POST_PROCESSOR = jwt().jwt(jwt -> {
        jwt.claims(claims -> claims.put("scope", "message.read message.write"))
                .subject("messaging-client")
                .notBefore(Instant.now().minusSeconds(5));
    });

    @Autowired
    MockMvc mockMvc;

    // Inyectamos el bean de Spring de tipo ObjectMapper con la configuración por defecto establecida por
    // Spring Boot para Jackson
    @Autowired
    ObjectMapper objectMapper;

    // Esta anotación crea un Mock de tipo BeerService y lo añade al contexto de Spring como un bean para que pueda ser
    // inyectado en el controlador BeerController
    @MockBean
    BeerService beerService;

    // Para obtener datos de prueba
    BeerServiceImpl beerServiceImpl;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<BeerDTO> beerDTOArgumentCaptor;

    // Se comentan porque ya no usamos Autenticación Básica
    /*@Value("${spring.security.user.name}")
    String username;*/

    /*@Value("${spring.security.user.password}")
    String password;*/

    @BeforeEach
    void setUp() {
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void noAuthTest() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getListBeersTest() throws Exception {
        given(beerService.getAll(null, null, null, 1, 25))
                .willReturn(beerServiceImpl.getAll(null, null, null, 1, 25));
        mockMvc.perform(get(BeerController.BEER_PATH)
                        // Se comenta porque ahora esta aplicación es un servidor de recursos OAuth2 que usa tokens JWT
                        //.with(httpBasic(username, password)))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.length()", is(3)));
    }

    @Test
    void getBeerByIdTest() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.getAll(null, null, null,
                null, null).getContent().get(0);
        when(beerService.getById(any(UUID.class))).thenReturn(beerDTO);
        mockMvc.perform(get(BeerController.BEER_PATH_ID, beerDTO.getId().toString())
                        //.with(httpBasic(username, password)))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(beerDTO.getId().toString())))
                .andExpect((jsonPath("$.name", is(beerDTO.getName()))));
    }

    @Test
    void getBeerByIdNotFoundTest() throws Exception {
        given(beerService.getById(any(UUID.class))).willThrow(NotFoundException.class);
        mockMvc.perform(get(BeerController.BEER_PATH_ID, UUID.randomUUID().toString())
                        //.with(httpBasic(username, password)))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isNotFound());
    }

    @Test
    void createNewBeerTest() throws Exception {
        BeerDTO savedBeerDTO = beerServiceImpl.getAll(null, null, null,
                null, null).getContent().get(0);
        // El objeto de tipo BeerDTO a crear va sin los campos "id", "createdDate" y "updateDate"
        BeerDTO newBeer = BeerDTO.builder()
                .name(savedBeerDTO.getName())
                .style(savedBeerDTO.getStyle())
                .upc(savedBeerDTO.getUpc())
                .quantityOnHand(savedBeerDTO.getQuantityOnHand())
                .price(savedBeerDTO.getPrice())
                .build();
        when(beerService.save(any(BeerDTO.class))).thenReturn(savedBeerDTO);
        mockMvc.perform(post(BeerController.BEER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBeer))
                        //.with(httpBasic(username, password)))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @Test
    void createBeerWithoutRequiredFieldsTest() throws Exception {
        BeerDTO beerDTO = BeerDTO.builder().build();
        MvcResult mvcResult = mockMvc.perform(post(BeerController.BEER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTO))
                        //.with(httpBasic(username, password)))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(4)))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void updateBeerTest() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.getAll(null, null, null,
                null, null).getContent().get(0);
        final UUID beerId = beerDTO.getId();
        // El objeto de tipo BeerDTO a actualizar va sin los campos "id", "createdDate" y "updateDate"
        beerDTO.setId(null);
        beerDTO.setCreatedDate(null);
        beerDTO.setUpdateDate(null);
        mockMvc.perform(put(BeerController.BEER_PATH_ID, beerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTO))
                        //.with(httpBasic(username, password)))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isNoContent());
        // Si no se indica lo contrario, por defecto verifica que se llame una vez
        verify(beerService).update(any(UUID.class), any(BeerDTO.class));
    }

    @Test
    void updateBeerWithoutRequiredFieldsTest() throws Exception {
        UUID beerId = beerServiceImpl.getAll(null, null, null,
                null, null).getContent().get(0).getId();
        BeerDTO beerDTO = BeerDTO.builder().build();
        mockMvc.perform(put(BeerController.BEER_PATH_ID, beerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTO))
                        //.with(httpBasic(username, password)))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(4)))
                .andReturn();
        verifyNoInteractions(beerService);
    }

    @Test
    void patchBeerTest() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.getAll(null, null, null,
                null, null).getContent().get(0);
        Map<String, Object> beerMap = new HashMap<>();
        final String beerName = "beerName";
        beerMap.put("name", beerName);
        mockMvc.perform(patch(BeerController.BEER_PATH_ID, beerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap))
                        //.with(httpBasic(username, password)))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isNoContent());
        verify(beerService, times(1)).patch(uuidArgumentCaptor.capture(),
                beerDTOArgumentCaptor.capture());
        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(beerDTO.getId());
        assertThat(beerDTOArgumentCaptor.getValue().getName()).isEqualTo(beerName);
    }

    @Test
    void deleteBeerTest() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.getAll(null, null, null,
                null, null).getContent().get(0);
        mockMvc.perform(delete(BeerController.BEER_PATH_ID, beerDTO.getId().toString())
                        //.with(httpBasic(username, password)))
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isNoContent());
        // Se comenta porque ahora usamos una propiedad de la clase
        //ArgumentCaptor<UUID> argumentCaptor = ArgumentCaptor.forClass(UUID.class);
        // Capturamos el parámetro que se pasa al método "deleteById" para luego verificar que es el correcto
        verify(beerService).deleteById(uuidArgumentCaptor.capture());
        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(beerDTO.getId());
    }

}