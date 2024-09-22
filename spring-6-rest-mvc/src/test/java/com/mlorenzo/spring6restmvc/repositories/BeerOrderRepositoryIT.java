package com.mlorenzo.spring6restmvc.repositories;

import com.mlorenzo.spring6restmvc.bootstrap.BootstrapData;
import com.mlorenzo.spring6restmvc.entities.BeerOrder;
import com.mlorenzo.spring6restmvc.entities.BeerOrderShipment;
import com.mlorenzo.spring6restmvc.entities.Customer;
import com.mlorenzo.spring6restmvc.services.BeerCsvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
// La interfaz BeerCsvService es una dependencia requerida por la clase BootstrapData, así que tenemos que importar
// también una implementación suya(BeerCsvServiceImpl)
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerOrderRepositoryIT {

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.findAll().get(0);
    }

    @Test
    void beerOrderTest() {
        BeerOrderShipment beerOrderShipment = BeerOrderShipment.builder()
                .trackingNumber("1235r")
                .build();
        BeerOrder beerOrder = BeerOrder.builder()
                .customerRef("Test order")
                .customer(testCustomer)
                .beerOrderShipment(beerOrderShipment)
                .build();
        testCustomer.getBeerOrders().add(beerOrder);
        beerOrderShipment.setBeerOrder(beerOrder);
        BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);
        System.out.println(savedBeerOrder.getCustomerRef());
    }
}