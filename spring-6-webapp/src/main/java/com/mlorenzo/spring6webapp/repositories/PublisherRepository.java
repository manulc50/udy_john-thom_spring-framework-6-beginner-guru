package com.mlorenzo.spring6webapp.repositories;

import com.mlorenzo.spring6webapp.domains.Publisher;
import org.springframework.data.repository.CrudRepository;

public interface PublisherRepository extends CrudRepository<Publisher, Long> {
}
