package com.mlorenzo.spring6webapp.repositories;

import com.mlorenzo.spring6webapp.domains.Author;
import org.springframework.data.repository.CrudRepository;

public interface AuthorRepository extends CrudRepository<Author, Long> {
}
