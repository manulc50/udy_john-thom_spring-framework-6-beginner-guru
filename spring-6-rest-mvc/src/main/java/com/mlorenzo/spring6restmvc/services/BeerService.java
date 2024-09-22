package com.mlorenzo.spring6restmvc.services;

import com.mlorenzo.spring6restmvc.models.BeerDTO;
import com.mlorenzo.spring6restmvc.models.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface BeerService {
    Page<BeerDTO> getAll(String name, BeerStyle style, Boolean showInventory, Integer pageNumber, Integer pageSize);
    BeerDTO getById(UUID id);
    BeerDTO save(BeerDTO beerDto);
    void update(UUID id, BeerDTO beerDto);
    void patch(UUID id, BeerDTO beerDto);
    void deleteById(UUID id);
}
