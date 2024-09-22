package com.mlorenzo.spring6restmvc.bootstrap;

import com.mlorenzo.spring6restmvc.repositories.BeerRepository;
import com.mlorenzo.spring6restmvc.repositories.CustomerRepository;
import com.mlorenzo.spring6restmvc.services.BeerCsvService;
import com.mlorenzo.spring6restmvc.services.BeerCsvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(BeerCsvServiceImpl.class)
class BootstrapDataIT {

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerCsvService beerCsvService;

    BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(beerRepository, customerRepository, beerCsvService);
    }

    @Test
    void runTest() throws Exception {
        bootstrapData.run(null);
        assertEquals(2413, beerRepository.count());
        assertEquals(3, customerRepository.count());
    }
}