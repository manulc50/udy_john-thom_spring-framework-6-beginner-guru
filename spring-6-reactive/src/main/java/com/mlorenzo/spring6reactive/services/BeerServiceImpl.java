package com.mlorenzo.spring6reactive.services;

import com.mlorenzo.spring6reactive.domains.Beer;
import com.mlorenzo.spring6reactive.mappers.BeerMapper;
import com.mlorenzo.spring6reactive.models.BeerDTO;
import com.mlorenzo.spring6reactive.repositories.BeerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.Map;

@AllArgsConstructor
@Service
public class BeerServiceImpl implements BeerService {
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public Flux<BeerDTO> getAll() {
        return beerRepository.findAll()
                // Versión simplificada de la expresión "beer -> beerMapper.beerEntitytoBeerDTO(beer)"
                .map(beerMapper::beerEntityToBeerDTO);
    }

    @Override
    public Mono<BeerDTO> getById(Integer id) {
        return beerRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                // Versión simplificada de la expresión "beer -> beerMapper.beerEntitytoBeerDTO(beer)"
                .map(beerMapper::beerEntityToBeerDTO);
    }

    @Override
    public Mono<BeerDTO> create(BeerDTO beerDTO) {
        return beerRepository.save(beerMapper.beerDTOToBeerEntity(beerDTO))
                // Versión simplificada de la expresión "beer -> beerMapper.beerEntitytoBeerDTO(beer)"
                .map(beerMapper::beerEntityToBeerDTO);
    }

    @Override
    public Mono<Void> update(Integer id, BeerDTO beerDTO) {
        return beerRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beer -> {
                    BeanUtils.copyProperties(beerDTO, beer, "id", "createdDate", "lastModifiedDate");
                    return beerRepository.save(beer);
                })
                .then();
    }

    @Override
    public Mono<Void> patch(Integer id, Map<String, Object> fields) {
        return beerRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(beer -> {
                    fields.forEach((key, value) -> {
                        Field field = ReflectionUtils.findField(Beer.class, key);
                        if(field != null) {
                            field.setAccessible(true);
                            ReflectionUtils.setField(field, beer, value);
                        }
                    });
                    return beerRepository.save(beer);
                })
                .then();
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        return beerRepository.existsById(id)
                .flatMap(isExists -> isExists
                        ? beerRepository.deleteById(id)
                        : Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                );
    }
}
