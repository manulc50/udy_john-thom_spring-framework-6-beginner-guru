package com.mlorenzo.spring6webapp.services;

import com.mlorenzo.spring6webapp.domains.Book;
import com.mlorenzo.spring6webapp.repositories.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Iterable<Book> getAll() {
        return bookRepository.findAll();
    }
}
