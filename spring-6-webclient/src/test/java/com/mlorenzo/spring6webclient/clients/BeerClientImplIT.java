package com.mlorenzo.spring6webclient.clients;

import com.mlorenzo.spring6webclient.models.BeerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

@SpringBootTest
class BeerClientImplIT {

    @Autowired
    BeerClient beerClient;

    @Test
    void getAllV1Test() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        beerClient.getAllV1()
                .doOnTerminate(() -> atomicBoolean.set(true))
                // Versión simplificada de la expresión "response -> System.out.println(response)"
                .subscribe(System.out::println);
        await().untilTrue(atomicBoolean);
    }

    @Test
    void getAllV2Test() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        beerClient.getAllV2()
                .doOnTerminate(() -> atomicBoolean.set(true))
                // Versión simplificada de la expresión "map -> System.out.println(map)"
                .subscribe(System.out::println);
        await().untilTrue(atomicBoolean);
    }

    @Test
    void getAllV3Test() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        beerClient.getAllV3()
                .doOnTerminate(() -> atomicBoolean.set(true))
                .subscribe(jsonNode -> System.out.println(jsonNode.toPrettyString()));
        await().untilTrue(atomicBoolean);
    }

    @Test
    void getAllV4Test() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        beerClient.getAllV4()
                .doOnTerminate(() -> atomicBoolean.set(true))
                // Versión simplificada de la expresión "beerDTO -> System.out.println(beerDTO)"
                .subscribe(System.out::println);
        await().untilTrue(atomicBoolean);
    }

    @Test
    void getByStyleTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        beerClient.getByStyle("PALE_ALE")
                .doOnTerminate(() -> atomicBoolean.set(true))
                // Versión simplificada de la expresión "beerDTO -> System.out.println(beerDTO)"
                .subscribe(System.out::println);
        await().untilTrue(atomicBoolean);
    }

    @Test
    void getByIdTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        beerClient.getAllV4()
                .flatMap(beerDTO -> beerClient.getById(beerDTO.getId()))
                .doOnTerminate(() -> atomicBoolean.set(true))
                // Versión simplificada de la expresión "beerDTO -> System.out.println(beerDTO)"
                .subscribe(System.out::println);
        await().untilTrue(atomicBoolean);
    }

    @Test
    void createTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        BeerDTO beerDTO = BeerDTO.builder()
                .name("Mongo Boobs")
                .price(new BigDecimal("10.99"))
                .style("IPA")
                .quantityOnHand(500)
                .upc("123123")
                .build();
        beerClient.create(beerDTO)
                .doOnTerminate(() -> atomicBoolean.set(true))
                // Versión simplificada de la expresión "beerDTO -> System.out.println(beerDTO)"
                .subscribe(System.out::println);
        await().untilTrue(atomicBoolean);
    }

    @Test
    void updateTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        beerClient.getAllV4()
                .next()
                .doOnNext(beerDTO -> beerDTO.setName("New Beer Name"))
                .flatMap(beerDTO -> beerClient.update(beerDTO.getId(), beerDTO))
                .doOnTerminate(() -> atomicBoolean.set(true))
                // Versión simplificada de la expresión "beerDTO -> System.out.println(beerDTO)"
                .subscribe(System.out::println);
        await().untilTrue(atomicBoolean);
    }

    @Test
    void patchTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        Map<String, Object> mapFields = Map.of("style", "New Beer Style");
        beerClient.getAllV4()
                .next()
                .flatMap(beerDTO -> beerClient.patch(beerDTO.getId(), mapFields))
                .doOnTerminate(() -> atomicBoolean.set(true))
                // Versión simplificada de la expresión "beerDTO -> System.out.println(beerDTO)"
                .subscribe(System.out::println);
        await().untilTrue(atomicBoolean);
    }

    @Test
    void deleteTest() {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        beerClient.getAllV4()
                .next()
                .flatMap(beerDTO -> beerClient.deleteById(beerDTO.getId()))
                .doOnTerminate(() -> atomicBoolean.set(true))
                .subscribe();
        await().untilTrue(atomicBoolean);
    }
}