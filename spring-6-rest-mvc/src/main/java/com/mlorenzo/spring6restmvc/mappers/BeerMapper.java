package com.mlorenzo.spring6restmvc.mappers;

import com.mlorenzo.spring6restmvc.entities.Beer;
import com.mlorenzo.spring6restmvc.models.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    Beer beerDtoToBeer(BeerDTO beerDTO);
    BeerDTO beerToBeerDto(Beer beer);
}
