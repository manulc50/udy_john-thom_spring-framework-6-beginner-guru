package com.mlorenzo.spring6reactivemongo.web.fn;

import com.mlorenzo.spring6reactivemongo.models.BeerDTO;
import com.mlorenzo.spring6reactivemongo.services.BeerService;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@AllArgsConstructor
@Component
public class BeerHandler {
    private final BeerService beerService;
    private final Validator validator;

    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        Flux<BeerDTO> fluxBeerDTO;
        if(serverRequest.queryParam("style").isPresent())
            fluxBeerDTO = beerService.getAllByStyle(serverRequest.queryParam("style").get());
        else
            fluxBeerDTO = beerService.getAll();
        return ServerResponse.ok().body(fluxBeerDTO, BeerDTO.class);
    }

    public Mono<ServerResponse> getById(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .body(beerService.getById(serverRequest.pathVariable("beerId")), BeerDTO.class);
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        // Versión simplificada de la expresión "beerDTO -> this.validate(beerDTO)"
        return beerService.save(serverRequest.bodyToMono(BeerDTO.class).doOnNext(this::validate))
                .flatMap(savedBeerDTO -> ServerResponse
                        .created(UriComponentsBuilder
                                .fromPath(BeerRouterConfig.BEER_ID_PATH).build(savedBeerDTO.getId()))
                        .build()
                );
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        return beerService.update(serverRequest.pathVariable("beerId"),
                        // Versión simplificada de la expresión "beerDTO -> this.validate(beerDTO)"
                        serverRequest.bodyToMono(BeerDTO.class).doOnNext(this::validate))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patch(ServerRequest serverRequest) {
        ParameterizedTypeReference<Map<String, Object>> parameterizedTypeReference = new ParameterizedTypeReference<>() {};
        return beerService.patch(serverRequest.pathVariable("beerId"), serverRequest.bodyToMono(parameterizedTypeReference))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        return beerService.delete(serverRequest.pathVariable("beerId"))
                .then(ServerResponse.noContent().build());
    }

    private void validate(BeerDTO beerDTO) {
        Errors errors = new BeanPropertyBindingResult(beerDTO, "beerDTO");
        validator.validate(beerDTO, errors);
        if(errors.hasErrors())
            throw new ServerWebInputException(errors.toString());
    }
}
