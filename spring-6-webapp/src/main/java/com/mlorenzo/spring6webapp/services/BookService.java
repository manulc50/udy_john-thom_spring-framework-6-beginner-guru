package com.mlorenzo.spring6webapp.services;

import com.mlorenzo.spring6webapp.domains.Book;

public interface BookService {
    Iterable<Book> getAll();
}
