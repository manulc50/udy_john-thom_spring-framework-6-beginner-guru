package com.mlorenzo.spring6di.services.environment;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("uat")
@Service
public class UatEnvironmentServiceImpl implements EnvironmentService {

    @Override
    public String getEnvironment() {
        return "uat";
    }
}
