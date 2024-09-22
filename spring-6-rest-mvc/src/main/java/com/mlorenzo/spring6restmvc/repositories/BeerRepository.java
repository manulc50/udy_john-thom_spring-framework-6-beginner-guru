package com.mlorenzo.spring6restmvc.repositories;

import com.mlorenzo.spring6restmvc.entities.Beer;
import com.mlorenzo.spring6restmvc.models.BeerStyle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
    Page<Beer> findAllByNameIsLikeIgnoreCase(String name, Pageable page);
    Page<Beer> findAllByStyle(BeerStyle style, Pageable page);
    Page<Beer> findAllByNameIsLikeIgnoreCaseAndStyle(String name, BeerStyle style, Pageable page);
}
