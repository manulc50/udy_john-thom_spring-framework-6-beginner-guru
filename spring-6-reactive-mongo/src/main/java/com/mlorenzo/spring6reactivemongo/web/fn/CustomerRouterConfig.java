package com.mlorenzo.spring6reactivemongo.web.fn;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@RequiredArgsConstructor
@Configuration
public class CustomerRouterConfig {
    public static final String CUSTOMER_PATH = "/api/v3/customers";
    public static final String CUSTOMER_ID_PATH = CUSTOMER_PATH + "/{customerId}";

    private final CustomerHandler customerHandler;

    @Bean
    public RouterFunction<ServerResponse> customerRoutes() {
        return route()
                // Versión simplificada de la expresión "serverRequest -> customerHandler.getAll(serverRequest)"
                .GET(CUSTOMER_PATH, customerHandler::getAll)
                // Versión simplificada de la expresión "serverRequest -> customerHandler.getById(serverRequest)"
                .GET(CUSTOMER_ID_PATH, customerHandler::getById)
                // Versión simplificada de la expresión "serverRequest -> customerHandler.create(serverRequest)"
                .POST(CUSTOMER_PATH, customerHandler::create)
                // Versión simplificada de la expresión "serverRequest -> customerHandler.update(serverRequest)"
                .PUT(CUSTOMER_ID_PATH, customerHandler::update)
                // Versión simplificada de la expresión "serverRequest -> customerHandler.patch(serverRequest)"
                .PATCH(CUSTOMER_ID_PATH, customerHandler::patch)
                // Versión simplificada de la expresión "serverRequest -> customerHandler.delete(serverRequest)"
                .DELETE(CUSTOMER_ID_PATH, customerHandler::delete)
                .build();
    }
}
