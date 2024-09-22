package com.mlorenzo.spring6di.controllers.i18n;

import com.mlorenzo.spring6di.services.GreetingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

@Controller
public class Myi18nController {

    private final GreetingService greetingService;

    public Myi18nController(@Qualifier("i18nService") final GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    public String sayHello() {
        return greetingService.sayGreeting();
    }
}
