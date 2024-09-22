package com.mlorenzo.spring6reactive.mappers;

import com.mlorenzo.spring6reactive.domains.Beer;
import com.mlorenzo.spring6reactive.models.BeerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BeerMapper {
    Beer beerDTOToBeerEntity(BeerDTO beerDTO);
    BeerDTO beerEntityToBeerDTO(Beer beer);
}
