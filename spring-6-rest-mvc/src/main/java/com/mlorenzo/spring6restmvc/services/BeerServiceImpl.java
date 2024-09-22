package com.mlorenzo.spring6restmvc.services;

import com.mlorenzo.spring6restmvc.exceptions.NotFoundException;
import com.mlorenzo.spring6restmvc.models.BeerDTO;
import com.mlorenzo.spring6restmvc.models.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {
    private final Map<UUID, BeerDTO> beerMap;

    public BeerServiceImpl() {
        beerMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BeerDTO beer1 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .name("Galaxy Cat")
                .style(BeerStyle.PALE_ALE)
                .upc("12356111")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(122)
                .createdDate(now)
                .updateDate(now)
                .build();
        BeerDTO beer2 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .name("Crank")
                .style(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .createdDate(now)
                .updateDate(now)
                .build();
        BeerDTO beer3 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .name("Sunshine City")
                .style(BeerStyle.IPA)
                .upc("12356777")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(144)
                .createdDate(now)
                .updateDate(now)
                .build();
        beerMap.put(beer1.getId(), beer1);
        beerMap.put(beer2.getId(), beer2);
        beerMap.put(beer3.getId(), beer3);
    }

    @Override
    public Page<BeerDTO> getAll(String name, BeerStyle style, Boolean showInventory,
                                Integer pageNumber, Integer pageSize) {
        return new PageImpl<>(List.copyOf(beerMap.values()));
    }

    @Override
    public BeerDTO getById(UUID id) {
        log.debug("Get Beer Id - in service, id: {}", id.toString());
        Optional<BeerDTO> oBeer = Optional.ofNullable(beerMap.get(id));
        // Versión simplificada de la expresión "() -> new NotFoundException()"
        return oBeer.orElseThrow(NotFoundException::new);
    }

    @Override
    public BeerDTO save(BeerDTO beerDto) {
        LocalDateTime now = LocalDateTime.now();
        beerDto.setId(UUID.randomUUID());
        beerDto.setCreatedDate(now);
        beerDto.setUpdateDate(now);
        beerMap.put(beerDto.getId(), beerDto);
        return beerDto;
    }

    @Override
    public void update(UUID id, BeerDTO beerDto) {
        BeerDTO beerDB = getById(id);
        BeanUtils.copyProperties(beerDto, beerDB, "id", "version", "createdDate", "updateDate");
        beerDB.setUpdateDate(LocalDateTime.now());
    }

    @Override
    public void patch(UUID id, BeerDTO beerDto) {
        BeerDTO beerDB = getById(id);
        boolean isUpdated = false;
        if(StringUtils.hasText(beerDto.getName())) {
            beerDB.setName(beerDto.getName());
            isUpdated = true;
        }
        if(beerDto.getStyle() != null) {
            beerDB.setStyle(beerDto.getStyle());
            isUpdated = true;
        }
        if(StringUtils.hasText(beerDto.getUpc())) {
            beerDB.setUpc(beerDto.getUpc());
            isUpdated = true;
        }
        if(beerDto.getQuantityOnHand() != null) {
            beerDB.setQuantityOnHand(beerDto.getQuantityOnHand());
            isUpdated = true;
        }
        if(beerDto.getPrice() != null) {
            beerDB.setPrice(beerDto.getPrice());
            isUpdated = true;
        }
        if(isUpdated)
            beerDB.setUpdateDate(LocalDateTime.now());
    }

    @Override
    public void deleteById(UUID id) {
        beerMap.remove(id);
    }
}
