package com.mlorenzo.spring6resttemplate.clients;

import com.mlorenzo.spring6resttemplate.models.BeerDTO;
import com.mlorenzo.spring6resttemplate.models.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Nota: Estos tests requieren que haya un servidor real levantado

@SpringBootTest
class BeerClientImplIT {

    @Autowired
    BeerClient beerClient;

    @Test
    void getAllTest() {
        List<BeerDTO> listBeers = beerClient.getAll().getContent();
        assertTrue(listBeers.size() > 0);
    }

    @Test
    void getAllWithNameTest() {
        List<BeerDTO> listBeers = beerClient.getAll("ALE").getContent();
        assertTrue(listBeers.size() > 0);
    }

    @Test
    void getByIdTest() {
        BeerDTO beerDto = beerClient.getAll().getContent().get(0);
        BeerDTO foundBeerDTO = beerClient.getById(beerDto.getId());
        assertNotNull(foundBeerDTO);
    }

    @Test
    void createTest() {
        BeerDTO beerDTO = BeerDTO.builder()
                .name("Mango Bobs")
                .price(new BigDecimal("10.99"))
                .style(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123456")
                .build();
        BeerDTO savedBeerDTO = beerClient.create(beerDTO);
        assertNotNull(savedBeerDTO);
    }

    @Test
    void updateTest() {
        BeerDTO beerDTO = BeerDTO.builder()
                .name("Mango Bobs 2")
                .price(new BigDecimal("10.99"))
                .style(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123456")
                .build();
        BeerDTO savedBeerDTO = beerClient.create(beerDTO);
        final String newName = "Mango Bobs 3";
        savedBeerDTO.setName(newName);
        BeerDTO updatedBeerDTO = beerClient.update(savedBeerDTO);
        assertEquals(newName, updatedBeerDTO.getName());
    }

    @Test
    void deleteTest() {
        BeerDTO beerDTO = BeerDTO.builder()
                .name("Mango Bobs 2")
                .price(new BigDecimal("10.99"))
                .style(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123456")
                .build();
        BeerDTO savedBeerDTO = beerClient.create(beerDTO);
        beerClient.delete(savedBeerDTO.getId());
        assertThrows(HttpClientErrorException.class, () -> beerClient.getById(savedBeerDTO.getId()));
    }
}