package com.mlorenzo.spring6reactivemongo.services;

import com.mlorenzo.spring6reactivemongo.domains.Beer;
import com.mlorenzo.spring6reactivemongo.mappers.BeerMapper;
import com.mlorenzo.spring6reactivemongo.models.BeerDTO;
import com.mlorenzo.spring6reactivemongo.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BeerServiceImpl implements BeerService {
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public Flux<BeerDTO> getAll() {
        return beerRepository.findAll()
                // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDTO(beer)"
                .map(beerMapper::beerToBeerDTO);
    }

    @Override
    public Flux<BeerDTO> getAllByStyle(String style) {
        return beerRepository.findByStyle(style)
                // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDTO(beer)"
                .map(beerMapper::beerToBeerDTO);
    }

    @Override
    public Mono<BeerDTO> getById(String id) {
        return beerRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDTO(beer)"
                .map(beerMapper::beerToBeerDTO);
    }

    @Override
    public Mono<BeerDTO> getFirstByName(String name) {
        return beerRepository.findFirstByName(name)
                // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDTO(beer)"
                .map(beerMapper::beerToBeerDTO);
    }

    @Override
    public Mono<BeerDTO> save(Mono<BeerDTO> monoBeerDTO) {
        // Versión simplificada de la expresión "beerDTO -> beerMapper.beerDTOToBeer(beerDTO)"
        return monoBeerDTO.map(beerMapper::beerDTOToBeer)
                // Versión simplificada de la expresión "beer -> beerRepository.save(beer)"
                .flatMap(beerRepository::save)
                // Versión simplificada de la expresión "beer -> beerMapper.beerToBeerDTO(beer)"
                .map(beerMapper::beerToBeerDTO);
    }

    @Override
    public Mono<Void> update(String id, Mono<BeerDTO> monoBeerDTO) {
        return monoBeerDTO.flatMap(beerDTO ->
                beerRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .flatMap(beer -> {
                            BeanUtils.copyProperties(beerDTO, beer, "id", "createdDate", "lastModifiedDate");
                            return beerRepository.save(beer);
                        })
        ).then();
    }

    @Override
    public Mono<Void> patch(String id, Mono<Map<String, Object>> monoFields) {
        return monoFields.flatMap(mapFields ->
                beerRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .flatMap(beer -> {
                            mapFields.forEach((key, value) -> {
                                Field field = ReflectionUtils.findField(Beer.class, key);
                                if(field != null) {
                                    field.setAccessible(true);
                                    ReflectionUtils.setField(field, beer, value);
                                }
                            });
                            return beerRepository.save(beer);
                        })
        ).then();
    }

    @Override
    public Mono<Void> delete(String id) {
        return beerRepository.existsById(id)
                .flatMap(isExists -> isExists
                        ? beerRepository.deleteById(id)
                        : Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                );
    }
}
