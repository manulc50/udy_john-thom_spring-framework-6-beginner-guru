package com.mlorenzo.spring6webapp.controllers;

import com.mlorenzo.spring6webapp.services.AuthorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // Si no se indica ningún método http en esta anotación, por defecto admite cualquiera de ellos
    @RequestMapping(path = "/authors", method = RequestMethod.GET)
    public String getAuthors(Model model) {
        model.addAttribute("authors", authorService.getAll());
        return "authors";
    }
}
