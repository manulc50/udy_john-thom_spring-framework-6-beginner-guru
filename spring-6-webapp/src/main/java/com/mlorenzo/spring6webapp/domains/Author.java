package com.mlorenzo.spring6webapp.domains;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue // Por defecto, la estrategia a usar es AUTO
    private Long id;

    private String firstName;
    private String lastName;

    @ManyToMany
    // Opcional porque estamos usando los valores por defecto
    @JoinTable(joinColumns = @JoinColumn(name = "authors_id"), inverseJoinColumns = @JoinColumn(name = "books_id"))
    private Set<Book> books;

    public Author() {
        books = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
