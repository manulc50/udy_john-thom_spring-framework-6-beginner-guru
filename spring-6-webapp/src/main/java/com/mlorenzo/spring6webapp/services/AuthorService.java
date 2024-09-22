package com.mlorenzo.spring6webapp.services;

import com.mlorenzo.spring6webapp.domains.Author;

public interface AuthorService {
    Iterable<Author> getAll();
}
