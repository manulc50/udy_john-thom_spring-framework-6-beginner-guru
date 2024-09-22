package com.mlorenzo.spring6di.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SetterInjectedControllerIT {

    @Autowired
    SetterInjectedController setterInjectedController;

    @Test
    void sayHelloTest() {
        System.out.println(setterInjectedController.sayHello());
    }
}