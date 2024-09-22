package com.mlorenzo.spring6webapp.controllers;

import com.mlorenzo.spring6webapp.services.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping({"/", "/books"})
    public String getBooks(Model model) {
        model.addAttribute("books", bookService.getAll());
        return "books";
    }
}
