package com.mlorenzo.spring6di.controllers.environment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"prod", "EN"})
@SpringBootTest
class EnvironmentControllerIT {

    @Autowired
    EnvironmentController environmentController;

    @Test
    void getEnvironmentTest() {
        System.out.println(environmentController.getEnvironment());
    }

}