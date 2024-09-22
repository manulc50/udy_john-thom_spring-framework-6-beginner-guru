package com.mlorenzo.spring6di.controllers;

import com.mlorenzo.spring6di.services.GreetingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Sin usar inyección de dependencias con Spring

class SetterInjectedControllerTest {
    SetterInjectedController setterInjectedController;

    @BeforeEach
    void setUp() {
        setterInjectedController = new SetterInjectedController();
        setterInjectedController.setGreetingService(new GreetingServiceImpl());
    }

    @Test
    void sayHelloTest() {
        System.out.println(setterInjectedController.sayHello());
    }
}