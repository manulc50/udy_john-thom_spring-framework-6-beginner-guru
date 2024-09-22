package com.mlorenzo.spring6reactive.repositories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlorenzo.spring6reactive.config.DatabaseConfig;
import com.mlorenzo.spring6reactive.domains.Beer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

// Esta anotación levanta un contexto simple de Spring con la capa de persistencia(repositorios)
@DataR2dbcTest
// Añadimos al contexto de Spring un bean de la clase de configuración "DatabaseConfig" para que se tenga en cuenta
// la anotación @EnableR2dbcAuditing y así poder crear de forma automática las fechas de auditoria
@Import(DatabaseConfig.class)
class BeerRepositoryIT {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    ReactiveTransactionManager reactiveTransactionManager;

    @Test
    void testSave() {
        // Para iniciar una transacción con rollback. De esta forma, el registro no se creará en la base de datos
        StepVerifier.create(TransactionalOperator.create(reactiveTransactionManager)
                        .execute(status -> {
                            status.setRollbackOnly();
                            return beerRepository.save(getTestBeer());
                        })
                )
                .expectNextMatches(beer -> beer.getId() != null)
                .verifyComplete();
    }

    @Test
    void testCreateJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(getTestBeer()));
    }

    private static Beer getTestBeer() {
        return Beer.builder()
                .name("Space Dust")
                .style("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("123231")
                .build();
    }

}