package com.mlorenzo.spring6restmvc.controllers;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static com.atlassian.oai.validator.whitelist.rule.WhitelistRules.messageHasKey;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BeerControllerRestAssuredIT {

    @LocalServerPort
    Integer localPort;

    // Filtro que valida los datos de la petición y de la respuesta de una petición http con respecto a una
    // especificación OpenAPI indicada(En nuestro caso, la especificación indicada se encuentra en el archivo "oa3.yml")
    OpenApiValidationFilter filter = new OpenApiValidationFilter(OpenApiInteractionValidator
            .createForSpecificationUrl("oa3.yml")
            // Para que la validación del formato de la fecha de la respuesta de la petición http sea ignorada
            .withWhitelist(ValidationErrorsWhitelist.create()
                    .withRule("Ignore date format",
                            messageHasKey("validation.response.body.schema.format.date-time")))
            .build());

    @TestConfiguration
    static class TestConfig {

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests().anyRequest().permitAll();
            return http.build();
        }
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = localPort;
    }

    @Test
    void listBeersTest() {
        given().contentType(ContentType.JSON)
                .when()
                // Apilcamos el filtro de validación contra la especificación OpenAPI en esta petición http
                .filter(filter)
                .get("/api/v1/beers")
                .then()
                .assertThat().statusCode(200);
    }
}
