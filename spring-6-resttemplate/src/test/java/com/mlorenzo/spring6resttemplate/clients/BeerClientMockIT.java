package com.mlorenzo.spring6resttemplate.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlorenzo.spring6resttemplate.config.OAuth2ClientInterceptor;
import com.mlorenzo.spring6resttemplate.config.RestTemplateBuilderConfig;
import com.mlorenzo.spring6resttemplate.models.BeerDTO;
import com.mlorenzo.spring6resttemplate.models.BeerDTOPageImpl;
import com.mlorenzo.spring6resttemplate.models.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

// Nota: En estos tests, las llamadas del cliente RestTemplate son manejadas por un servidor "mockeado", es decir,
// no requieren tener un servidor real levantado

// En este caso, esta anotación levanta un simple contexto de Spring con un bean correspondiente a la
// clase BeerClientImpl, otro correspondiente al cliente RestTemplate y otro correspondiente a un Mock del servidor
@RestClientTest(BeerClientImpl.class)
// Para que se use el cliente RestTemplate que hemos configurado en la clase de configuración "RestTemplateBuilderConfig"
// en lugar del cliente RestTemplate por defecto que instancia Spring Boot
@Import(RestTemplateBuilderConfig.class)
class BeerClientMockIT {
    static final String URL = "http://localhost:8080";
    static final String TOKEN_VALUE = "test";

    // Se comenta porque el cliente RestTemplate ya no está configurado con Autenticación Básica
    //static final String BASIC_AUTH_HEADER_VALUE = "Basic dXNlcjE6cGFzc3dvcmQ=";
    static final String BEARER_AUTH_HEADER_VALUE = "Bearer " + TOKEN_VALUE;

    @Autowired
    BeerClient beerClient;

    @Autowired
    MockRestServiceServer server;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    BeerDTO beerDTO;
    String payload;

    // Como la clase de estos tests no utiliza tod*o el contexto de Spring, creamos esta clase de configuración
    // para crear los componentes de Spring requeridos por el cliente RestTemplate. Este cliente RestTemplate
    // usa el interceptor OAuth2ClientInterceptor que lo configura con OAuth2.
    @TestConfiguration
    static class TestConfig {

        @Bean
        ClientRegistrationRepository clientRegistrationRepository() {
            return new InMemoryClientRegistrationRepository(ClientRegistration
                    .withRegistrationId(OAuth2ClientInterceptor.CLIENT_REGISTRATION_ID)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .clientId("test")
                    .clientSecret("test")
                    .tokenUri("test")
                    .build()
            );
        }

        @Bean
        OAuth2AuthorizedClientService oAuth2AuthorizedClientService(final ClientRegistrationRepository
                                                                            clientRegistrationRepository) {
            return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
        }

        @Bean
        OAuth2ClientInterceptor oAuth2ClientInterceptor(final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager,
                                                        final ClientRegistrationRepository clientRegistrationRepository) {
            return new OAuth2ClientInterceptor(oAuth2AuthorizedClientManager,clientRegistrationRepository);
        }

    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        ClientRegistration clientRegistration = clientRegistrationRepository
                .findByRegistrationId(OAuth2ClientInterceptor.CLIENT_REGISTRATION_ID);
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                TOKEN_VALUE, Instant.MIN, Instant.MAX);
        when(oAuth2AuthorizedClientManager.authorize(any(OAuth2AuthorizeRequest.class)))
                .thenReturn(new OAuth2AuthorizedClient(clientRegistration, "test", oAuth2AccessToken));
        beerDTO = getBeerDTO();
        payload = objectMapper.writeValueAsString(beerDTO);
    }

    @Test
    void getAllTest() throws JsonProcessingException {
        final String payload = objectMapper.writeValueAsString(getPage());
        // Configuramos el comportamiento del Mock del servidor
        // Cuando haya una llamada del cliente RestTemplate que sea una petición http GET a la ruta URL + BEER_PATH
        // y exista la cabecera AUTHORIZATION con el valor BASIC_AUTH_HEADER_VALUE, el Mock del servidor devolverá
        // un Json con el payload creado anteriormente
        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(String.format("%s%s", URL, BeerClientImpl.BEER_PATH)))
                // Se comenta porque el cliente RestTemplate ya no está configurado con Autenticación Básica
                //.andExpect(header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER_VALUE))
                .andExpect(header(HttpHeaders.AUTHORIZATION, BEARER_AUTH_HEADER_VALUE))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));
        final List<BeerDTO> listBeers = beerClient.getAll().getContent();
        assertTrue(listBeers.size() > 0);
        server.verify();
    }

    @Test
    void getAllWithQueryParamTest() throws JsonProcessingException {
        final String queryParamName = "name";
        final String queryParanValue = "ALE";
        final URI uri = UriComponentsBuilder.fromHttpUrl(String.format("%s%s", URL, BeerClientImpl.BEER_PATH))
                .queryParam(queryParamName, queryParanValue)
                .build().toUri();
        final String payload = objectMapper.writeValueAsString(getPage());
        // Configuramos el comportamiento del Mock del servidor
        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(uri))
                // Se comenta porque el cliente RestTemplate ya no está configurado con Autenticación Básica
                //.andExpect(header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER_VALUE))
                .andExpect(header(HttpHeaders.AUTHORIZATION, BEARER_AUTH_HEADER_VALUE))
                .andExpect(queryParam(queryParamName, queryParanValue))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));
        final List<BeerDTO> listBeers = beerClient.getAll(queryParanValue).getContent();
        assertTrue(listBeers.size() > 0);
        server.verify();
    }

    @Test
    void getByIdTest() {
        // Configuramos el comportamiento del Mock del servidor
        mockGetByIdOperation();
        final BeerDTO foundBeerDTO = beerClient.getById(beerDTO.getId());
        assertThat(foundBeerDTO.getId()).isEqualTo(beerDTO.getId());
        server.verify();
    }

    @Test
    void createTest() {
        final URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.BEER_ID_PATH).build(beerDTO.getId());
        // Configuramos el comportamiento del Mock del servidor
        server.expect(method(HttpMethod.POST))
                .andExpect(requestTo(String.format("%s%s", URL, BeerClientImpl.BEER_PATH)))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                // Se comenta porque el cliente RestTemplate ya no está configurado con Autenticación Básica
                //.andExpect(header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER_VALUE))
                .andExpect(header(HttpHeaders.AUTHORIZATION, BEARER_AUTH_HEADER_VALUE))
                .andRespond(withCreatedEntity(uri));
        mockGetByIdOperation();
        final BeerDTO foundBeerDTO = beerClient.create(beerDTO);
        assertThat(foundBeerDTO.getId()).isEqualTo(beerDTO.getId());
        server.verify();
    }

    @Test
    void updateTest() {
        // Configuramos el comportamiento del Mock del servidor
        server.expect(method(HttpMethod.PUT))
                .andExpect(requestToUriTemplate(String.format("%s%s", URL, BeerClientImpl.BEER_ID_PATH), beerDTO.getId()))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                // Se comenta porque el cliente RestTemplate ya no está configurado con Autenticación Básica
                //.andExpect(header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER_VALUE))
                .andExpect(header(HttpHeaders.AUTHORIZATION, BEARER_AUTH_HEADER_VALUE))
                .andRespond(withNoContent());
        mockGetByIdOperation();
        final BeerDTO foundBeerDTO = beerClient.update(beerDTO);
        assertEquals(beerDTO.getId(), foundBeerDTO.getId());
        server.verify();
    }

    @Test
    void deleteTest() {
        // Configuramos el comportamiento del Mock del servidor
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(String.format("%s%s", URL, BeerClientImpl.BEER_ID_PATH), beerDTO.getId()))
                // Se comenta porque el cliente RestTemplate ya no está configurado con Autenticación Básica
                //.andExpect(header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER_VALUE))
                .andExpect(header(HttpHeaders.AUTHORIZATION, BEARER_AUTH_HEADER_VALUE))
                .andRespond(withNoContent());
        beerClient.delete(beerDTO.getId());
        server.verify();
    }

    @Test
    void deleteNotFoundTest() {
        // Configuramos el comportamiento del Mock del servidor
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(String.format("%s%s", URL, BeerClientImpl.BEER_ID_PATH), beerDTO.getId()))
                // Se comenta porque el cliente RestTemplate ya no está configurado con Autenticación Básica
                //.andExpect(header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER_VALUE))
                .andExpect(header(HttpHeaders.AUTHORIZATION, BEARER_AUTH_HEADER_VALUE))
                .andRespond(withResourceNotFound());
        assertThrows(HttpClientErrorException.class, () -> beerClient.delete(beerDTO.getId()));
        server.verify();
    }

    private void mockGetByIdOperation() {
        // Configuramos el comportamiento del Mock del servidor
        server.expect(method(HttpMethod.GET))
                .andExpect(requestToUriTemplate(String.format("%s%s", URL, BeerClientImpl.BEER_ID_PATH), beerDTO.getId()))
                // Se comenta porque el cliente RestTemplate ya no está configurado con Autenticación Básica
                //.andExpect(header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER_VALUE))
                .andExpect(header(HttpHeaders.AUTHORIZATION, BEARER_AUTH_HEADER_VALUE))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));
    }

    private BeerDTO getBeerDTO() {
        return BeerDTO.builder()
                .id(UUID.randomUUID())
                .name("Mango Bobs")
                .price(new BigDecimal("10.99"))
                .style(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123456")
                .build();
    }

    private BeerDTOPageImpl getPage() {
        return new BeerDTOPageImpl(Collections.singletonList(getBeerDTO()), 1, 25, 1);
    }
}