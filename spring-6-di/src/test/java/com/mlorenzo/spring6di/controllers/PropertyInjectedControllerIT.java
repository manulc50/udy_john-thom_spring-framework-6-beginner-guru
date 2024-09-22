package com.mlorenzo.spring6di.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PropertyInjectedControllerIT {

    @Autowired
    PropertyInjectedController propertyInjectedController;

    @Test
    void sayHelloTest() {
        System.out.println(propertyInjectedController.sayHello());
    }

}