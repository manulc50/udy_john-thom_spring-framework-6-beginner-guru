package com.mlorenzo.spring6di.controllers;

import com.mlorenzo.spring6di.services.GreetingService;
import org.springframework.stereotype.Controller;

@Controller
public class MyController {
    private final GreetingService greetingService;

    public MyController(final GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    public String sayHello() {
        System.out.println("I'm in the controller");
        return "Hello everyone!";
    }
}
