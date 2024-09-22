package com.mlorenzo.spring6reactivemongo.mappers;

import com.mlorenzo.spring6reactivemongo.domains.Beer;
import com.mlorenzo.spring6reactivemongo.models.BeerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BeerMapper {
    BeerDTO beerToBeerDTO(Beer beer);
    Beer beerDTOToBeer(BeerDTO beerDTO);
}
