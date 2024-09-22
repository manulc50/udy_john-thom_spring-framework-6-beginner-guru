package com.mlorenzo.spring6reactive.repositories;

import com.mlorenzo.spring6reactive.domains.Beer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BeerRepository extends ReactiveCrudRepository<Beer, Integer> {
}
