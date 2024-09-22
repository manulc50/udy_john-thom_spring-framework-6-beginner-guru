package com.mlorenzo.spring6webapp.repositories;

import com.mlorenzo.spring6webapp.domains.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<Book, Long> {
}
