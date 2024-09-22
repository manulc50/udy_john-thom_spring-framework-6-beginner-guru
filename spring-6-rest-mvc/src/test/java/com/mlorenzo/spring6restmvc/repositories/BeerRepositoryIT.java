package com.mlorenzo.spring6restmvc.repositories;

import com.mlorenzo.spring6restmvc.bootstrap.BootstrapData;
import com.mlorenzo.spring6restmvc.entities.Beer;
import com.mlorenzo.spring6restmvc.models.BeerStyle;
import com.mlorenzo.spring6restmvc.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
// La interfaz BeerCsvService es una dependencia requerida por la clase BootstrapData, así que tenemos que importar
// también una implementación suya(BeerCsvServiceImpl)
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryIT {

    @Autowired
    BeerRepository beerRepository;

    @Test
    void testGetBeersByName() {
        Page<Beer> beerPage = beerRepository.findAllByNameIsLikeIgnoreCase("%IPA%", null);
        assertThat(beerPage.getContent().size()).isEqualTo(336);
    }

    @Test
    void testGetBeersByStyle() {
        Page<Beer> beerPage = beerRepository.findAllByStyle(BeerStyle.IPA, null);
        assertThat(beerPage.getContent().size()).isEqualTo(548);
    }

    @Test
    void testGetBeersByNameAndStyle() {
        Page<Beer> beerPage = beerRepository.findAllByNameIsLikeIgnoreCaseAndStyle("%IPA%",
                BeerStyle.IPA, null);
        assertThat(beerPage.getContent().size()).isEqualTo(310);
    }

    @Test
    void testSaveBeer() {
        Beer savedBeer = beerRepository.save(Beer.builder()
                .name("My Beer")
                .style(BeerStyle.PALE_ALE)
                .upc("234234234234")
                .price(new BigDecimal("11.99"))
                .createdDate(LocalDateTime.now())
                .build());
        // Aquí forzamos el guardado de la entidad para que pueda producirse las validaciones de la capa de persistencia
        // antes de que finalice la ejecución del test
        beerRepository.flush();
        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }

    @Test
    void testSaveBeerNameTooLong() {
        assertThrows(ConstraintViolationException.class, () -> {;
            beerRepository.save(Beer.builder()
                    .name("My Beer 01234567890123456789012345678901234567890123456789012345678901234567890123456789")
                    .style(BeerStyle.PALE_ALE)
                    .upc("234234234234")
                    .price(new BigDecimal("11.99"))
                    .createdDate(LocalDateTime.now())
                    .build());
            // Aquí forzamos el guardado de la entidad para que pueda producirse las validaciones de la capa de
            // persistencia antes de que finalice la ejecución del test
            beerRepository.flush();
        });
    }

}