package com.mlorenzo.spring6restmvc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlorenzo.spring6restmvc.controllers.BeerController;
import com.mlorenzo.spring6restmvc.entities.Beer;
import com.mlorenzo.spring6restmvc.exceptions.NotFoundException;
import com.mlorenzo.spring6restmvc.mappers.BeerMapper;
import com.mlorenzo.spring6restmvc.models.BeerDTO;
import com.mlorenzo.spring6restmvc.models.BeerStyle;
import com.mlorenzo.spring6restmvc.repositories.BeerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Primary
@AllArgsConstructor
public class BeerServiceJPAImpl implements BeerService {
    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Page<BeerDTO> getAll(String name, BeerStyle style, Boolean showInventory,
                                Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
        Page<Beer> beerPage;
        if(StringUtils.hasText(name) && style != null) {
            // Nota: Usamos "%%" para escapar el caracter '%'
            beerPage = beerRepository.findAllByNameIsLikeIgnoreCaseAndStyle(String.format("%%%s%%", name),
                    style, pageRequest);
        }
        else if(StringUtils.hasText(name))
            beerPage = beerRepository.findAllByNameIsLikeIgnoreCase(String.format("%%%s%%", name), pageRequest);
        else if(style != null)
            beerPage = beerRepository.findAllByStyle(style, pageRequest);
        else
            beerPage = beerRepository.findAll(pageRequest);
        return beerPage.map(beer -> {
            if(showInventory != null && !showInventory)
                beer.setQuantityOnHand(null);
            return beerMapper.beerToBeerDto(beer);
        });
    }

    @Override
    public BeerDTO getById(UUID id) {
        return beerRepository.findById(id)
                // versión simplificada de la expresión "beer -> beerMapper.beerToBeerDto(beer)"
                .map(beerMapper::beerToBeerDto)
                // versión simplificada de la expresión "() -> new NotFoundException()"
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public BeerDTO save(BeerDTO beerDto) {
        return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(beerDto)));
    }

    @Override
    public void update(UUID id, BeerDTO beerDto) {
        beerRepository.findById(id)
                .map(beer -> {
                    BeanUtils.copyProperties(beerDto, beer, "id", "version", "createdDate", "updateDate");
                    return beerRepository.save(beer);
                })
                // versión simplificada de la expresión "() -> new NotFoundException()"
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public void patch(UUID id, BeerDTO beerDto) {
        beerRepository.findById(id)
                .ifPresentOrElse(beer -> {
                    final Map<String, Object> fields = objectMapper.convertValue(beerDto, Map.class);
                    AtomicBoolean isUpdated = new AtomicBoolean(false);
                    fields.forEach((key, value) -> {
                        if(value != null) {
                            Field field = ReflectionUtils.findField(Beer.class, key);
                            field.setAccessible(true);
                            ReflectionUtils.setField(field, beer, value);
                            isUpdated.set(true);
                        }
                    });
                    if(isUpdated.get())
                        beerRepository.save(beer);
                }, () -> { throw new NotFoundException(); });
    }

    @Override
    public void deleteById(UUID id) {
        if(beerRepository.existsById(id))
            beerRepository.deleteById(id);
        else
            throw new NotFoundException();
    }

    private PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        final int queryPageNumber = (pageNumber > 0)
                ? pageNumber - 1 : Integer.parseInt(BeerController.DEFAULT_PAGE);
        final int queryPageSize = (pageSize <= 0 ) ? Integer.parseInt(BeerController.DEFAULT_PAGE_SIZE)
                : (pageSize > 1000) ? 1000 : pageSize;
        // Por defecto, el ordenamiento es de forma ascendente
        return PageRequest.of(queryPageNumber, queryPageSize, Sort.by("name"));
    }
}
