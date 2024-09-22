package com.mlorenzo.spring6resttemplate.clients;

import com.mlorenzo.spring6resttemplate.models.BeerDTO;
import com.mlorenzo.spring6resttemplate.models.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface BeerClient {
    Page<BeerDTO> getAll();
    Page<BeerDTO> getAll(String name);
    Page<BeerDTO> getAll(String name, BeerStyle style,Boolean showInventory, Integer pageNumber, Integer pageSize);
    BeerDTO getById(UUID id);
    BeerDTO create(BeerDTO beerDTO);
    BeerDTO update(BeerDTO beerDTO);
    void delete(UUID id);
}
