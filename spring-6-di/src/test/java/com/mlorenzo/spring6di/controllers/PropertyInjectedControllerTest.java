package com.mlorenzo.spring6di.controllers;

import com.mlorenzo.spring6di.services.GreetingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Sin usar inyección de dependencias con Spring

class PropertyInjectedControllerTest {
    PropertyInjectedController propertyInjectedController;

    @BeforeEach
    void setUp() {
        propertyInjectedController = new PropertyInjectedController();
        propertyInjectedController.greetingService = new GreetingServiceImpl();
    }

    @Test
    void sayHelloTest() {
        System.out.println(propertyInjectedController.sayHello());
    }

}