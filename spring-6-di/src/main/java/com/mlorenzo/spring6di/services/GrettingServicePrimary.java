package com.mlorenzo.spring6di.services;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class GrettingServicePrimary implements GreetingService {

    @Override
    public String sayGreeting() {
        return "Hello from the primary bean!!!";
    }
}
