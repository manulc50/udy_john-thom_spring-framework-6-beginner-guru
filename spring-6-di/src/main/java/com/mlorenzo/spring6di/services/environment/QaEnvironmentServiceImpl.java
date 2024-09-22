package com.mlorenzo.spring6di.services.environment;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("qa")
@Service
public class QaEnvironmentServiceImpl implements EnvironmentService {

    @Override
    public String getEnvironment() {
        return "qa";
    }
}
