package com.mlorenzo.spring6reactive.controllers;

import com.mlorenzo.spring6reactive.models.BeerDTO;
import com.mlorenzo.spring6reactive.services.BeerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v2/beers")
public class BeerController {
    private final BeerService beerService;

    @GetMapping
    public Flux<BeerDTO> getAll() {
        return beerService.getAll();
    }

    @GetMapping("/{beerId}")
    public Mono<BeerDTO> getById(@PathVariable("beerId") Integer id) {
        return beerService.getById(id);
    }

    // Nota:Para realizar las validaciones de beans(DTO's), puede usarse tanto la anotación de Jakarta @Valid como la
    // anotación de Spring @Validated

    @PostMapping
    public Mono<ResponseEntity<Void>> create(@Validated @RequestBody BeerDTO beerDTO) {
        return beerService.create(beerDTO)
                .map(savedBeerDTO -> ResponseEntity.created(UriComponentsBuilder
                        .fromPath(String.format("/api/v2/beers/%d", savedBeerDTO.getId())).build().toUri()).build());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{beerId}")
    public Mono<Void> update(@PathVariable(value = "beerId") Integer id, @RequestBody @Validated BeerDTO beerDTO) {
        return beerService.update(id, beerDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{beerId}")
    public Mono<Void> patch(@PathVariable(name = "beerId") Integer id, @RequestBody Map<String, Object> fields){
        return beerService.patch(id, fields);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Integer id) {
        return beerService.deleteById(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
