package com.mlorenzo.spring6reactivemongo.services;

import com.mlorenzo.spring6reactivemongo.models.BeerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerServiceImplIT {

    @Autowired
    BeerService beerService;

    @Test
    void testFindByStyle() {
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        final BeerDTO beerDTO = getTestBeer();
        beerService.save(Mono.just(beerDTO))
                .subscribe(savedBeerDTO -> beerService.getAllByStyle(savedBeerDTO.getStyle())
                        .doAfterTerminate(() -> atomicBoolean.set(true))
                        // Versión simplificada de la expresión "foundBeerDTO -> System.out.println(foundBeerDTO)"
                        .subscribe(System.out::println)
                );
        await().untilTrue(atomicBoolean);
    }

    @Test
    void testFindFirstByNameSubscribe() {
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        final BeerDTO beerDTO = getTestBeer();
        beerService.save(Mono.just(beerDTO))
                .subscribe(savedBeerDTO -> beerService.getFirstByName(savedBeerDTO.getName())
                        .subscribe(foundBeerDTO -> {
                            System.out.println(foundBeerDTO);
                            atomicBoolean.set(true);
                        })
                );
        await().untilTrue(atomicBoolean);
    }

    @Test
    void testSaveSubscribe() {
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        final AtomicReference<BeerDTO> atomicBeerDTO = new AtomicReference<>();
        beerService.save(Mono.just(getTestBeer()))
                .subscribe(savedBeerDTO -> {
                    atomicBeerDTO.set(savedBeerDTO);
                    atomicBoolean.set(true);
                });
        await().untilTrue(atomicBoolean);
        final BeerDTO savedBeerDTO = atomicBeerDTO.get();
        assertThat(savedBeerDTO).isNotNull();
        assertThat(savedBeerDTO.getId()).isNotNull();
    }

    @Test
    void testSaveBlock() {
        final BeerDTO savedBeerDTO = beerService.save(Mono.just(getTestBeer())).block();
        assertThat(savedBeerDTO).isNotNull();
        assertThat(savedBeerDTO.getId()).isNotNull();
    }

    @Test
    void testUpdateSubscribe() {
        final AtomicReference<BeerDTO> atomicBeerDTO = new AtomicReference<>();
        final BeerDTO beerDTO = getTestBeer();
        final String name = "New Beer Name";
        beerService.save(Mono.just(beerDTO))
                .subscribe(savedBeerDTO -> {
                    beerDTO.setName(name);
                    beerService.update(savedBeerDTO.getId(), Mono.just(beerDTO))
                            .then(beerService.getById(savedBeerDTO.getId()))
                            // Versión simplificada de la expresión "foundBeerDTO -> atomicBeerDTO.set(foundBeerDTO)"
                            .subscribe(atomicBeerDTO::set);
                });
        await().until(() -> atomicBeerDTO.get() != null);
        final BeerDTO foundBeerDTO = atomicBeerDTO.get();
        assertThat(foundBeerDTO).isNotNull();
        assertThat(foundBeerDTO.getName()).isEqualTo(name);
    }

    @Test
    void testUpdateBlock() {
        final BeerDTO beerDTO = getTestBeer();
        final BeerDTO savedBeerDTO = beerService.save(Mono.just(beerDTO)).block();
        final String name = "New Beer Name";
        beerDTO.setName(name);
        beerService.update(savedBeerDTO.getId(), Mono.just(beerDTO)).block();
        final BeerDTO foundBeerDTO = beerService.getById(savedBeerDTO.getId()).block();
        assertThat(foundBeerDTO).isNotNull();
        assertThat(foundBeerDTO.getName()).isEqualTo(name);
    }

    @Test
    void testDeleteBlock() {
        final BeerDTO savedBeerDTO = beerService.save(Mono.just(getTestBeer())).block();
        beerService.delete(savedBeerDTO.getId()).block();
        assertThrows(ResponseStatusException.class, () -> beerService.getById(savedBeerDTO.getId()).block());
    }

    private BeerDTO getTestBeer() {
        return BeerDTO.builder()
                .name("Space Dust")
                .style("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("123123")
                .build();
    }

}