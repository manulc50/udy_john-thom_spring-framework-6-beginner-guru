package com.mlorenzo.spring6restmvc.controllers;

import com.mlorenzo.spring6restmvc.models.BeerDTO;
import com.mlorenzo.spring6restmvc.models.BeerStyle;
import com.mlorenzo.spring6restmvc.services.BeerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@RestController
public class BeerController {
    public static final String BEER_PATH = "/api/v1/beers";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";
    public static final String DEFAULT_PAGE = "1";
    public static final String DEFAULT_PAGE_SIZE = "25";

    private final BeerService beerService;

    @GetMapping(BEER_PATH)
    public Page<BeerDTO> getAll(@RequestParam(required = false) String name,
                                @RequestParam(required = false) BeerStyle style,
                                @RequestParam(required = false) Boolean showInventory,
                                @RequestParam(defaultValue = DEFAULT_PAGE) Integer pageNumber,
                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize) {
        return beerService.getAll(name, style, showInventory, pageNumber, pageSize);
    }

    @GetMapping(BEER_PATH_ID)
    public BeerDTO getById(@PathVariable("beerId") UUID id) {
        log.debug("Get Beer By Id - in controller");
        return beerService.getById(id);
    }

    // Nota:Para realizar las validaciones de beans(DTO's), puede usarse tanto la anotación de Jakarta @Valid como la
    // anotación de Spring @Validated

    @PostMapping(BEER_PATH)
    public ResponseEntity<Void> create(@Valid @RequestBody BeerDTO beerDTO) {
        BeerDTO savedBeer = beerService.save(beerDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.LOCATION, String.format("%s/%s", BEER_PATH, savedBeer.getId().toString()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(BEER_PATH_ID)
    public void update(@PathVariable(value = "beerId") UUID id, @Valid @RequestBody BeerDTO beerDTO) {
        beerService.update(id, beerDTO);
    }

    @PatchMapping(BEER_PATH_ID)
    public ResponseEntity<Void> patch(@PathVariable(name = "beerId") UUID id, @RequestBody BeerDTO beerDTO) {
        beerService.patch(id, beerDTO);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(BEER_PATH_ID)
    public void deleteById(@PathVariable UUID beerId) {
        beerService.deleteById(beerId);
    }

    // Este método puede ser privado o público
    // Se comenta porque ahora usamos un manejador global de excepciones para todos los controladores
    /*@ExceptionHandler(NotFoundException.class)
    private ResponseEntity<Void> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }*/
}
