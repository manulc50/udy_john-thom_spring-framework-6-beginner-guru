package com.mlorenzo.spring6reactivemongo.web.fn;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@AllArgsConstructor
@Configuration
public class BeerRouterConfig {
    public static final String BEER_PATH = "/api/v3/beers";
    public static final String BEER_ID_PATH = BEER_PATH + "/{beerId}";

    private final BeerHandler beerHandler;

    @Bean
    public RouterFunction<ServerResponse> beerRoutes() {
        return route()
                // Versión simplificada de la expresión "serverRequest -> beerHandler.getAll(serverRequest)"
                .GET(BEER_PATH, beerHandler::getAll)
                // Versión simplificada de la expresión "serverRequest -> beerHandler.getById(serverRequest)"
                .GET(BEER_ID_PATH, beerHandler::getById)
                // Versión simplificada de la expresión "serverRequest -> beerHandler.create(serverRequest)"
                .POST(BEER_PATH, beerHandler::create)
                // Versión simplificada de la expresión "serverRequest -> beerHandler.update(serverRequest)"
                .PUT(BEER_ID_PATH, beerHandler::update)
                // Versión simplificada de la expresión "serverRequest -> beerHandler.patch(serverRequest)"
                .PATCH(BEER_ID_PATH, beerHandler::patch)
                // Versión simplificada de la expresión "serverRequest -> beerHandler.delete(serverRequest)"
                .DELETE(BEER_ID_PATH, beerHandler::delete)
                .build();
    }
}
