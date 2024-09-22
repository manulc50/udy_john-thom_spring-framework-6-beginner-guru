package com.mlorenzo.spring6reactivemongo.web.fn;

import com.mlorenzo.spring6reactivemongo.models.CustomerDTO;
import com.mlorenzo.spring6reactivemongo.services.CustomerService;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@Component
public class CustomerHandler {
    private final CustomerService customerService;
    private final Validator validator;

    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        Flux<CustomerDTO> fluxCustomerDTO;
        if(serverRequest.queryParam("name").isPresent())
            fluxCustomerDTO = customerService.getAllByName(serverRequest.queryParam("name").get());
        else
            fluxCustomerDTO = customerService.getAll();
        return ServerResponse.ok().body(fluxCustomerDTO, CustomerDTO.class);
    }

    public Mono<ServerResponse> getById(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .body(customerService.getById(serverRequest.pathVariable("customerId")), CustomerDTO.class);
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        // Versión simplificada de la expresión "customerDTO -> this.validate(customerDTO)"
        return customerService.save(serverRequest.bodyToMono(CustomerDTO.class).doOnNext(this::validate))
                .flatMap(savedCustomerDTO -> ServerResponse
                        .created(UriComponentsBuilder
                                .fromPath(CustomerRouterConfig.CUSTOMER_ID_PATH).build(savedCustomerDTO.getId()))
                        .build()
                );
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest) {
        return customerService.update(serverRequest.pathVariable("customerId"),
                        // Versión simplificada de la expresión "customerDTO -> this.validate(customerDTO)"
                        serverRequest.bodyToMono(CustomerDTO.class).doOnNext(this::validate))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> patch(ServerRequest serverRequest) {
        return customerService.patch(serverRequest.pathVariable("customerId"), serverRequest.bodyToMono(CustomerDTO.class))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> delete(ServerRequest serverRequest) {
        return customerService.delete(serverRequest.pathVariable("customerId"))
                .then(ServerResponse.noContent().build());
    }

    private void validate(CustomerDTO customerDTO) {
        Errors errors = new BeanPropertyBindingResult(customerDTO, "customerDTO");
        validator.validate(customerDTO, errors);
        if(errors.hasErrors())
            throw new ServerWebInputException(errors.toString());
    }
}
