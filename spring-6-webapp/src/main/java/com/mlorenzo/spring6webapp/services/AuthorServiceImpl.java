package com.mlorenzo.spring6webapp.services;

import com.mlorenzo.spring6webapp.domains.Author;
import com.mlorenzo.spring6webapp.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Iterable<Author> getAll() {
        return authorRepository.findAll();
    }
}
