package com.mlorenzo.spring6webapp.domains;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue // Por defecto, la estrategia a usar es AUTO
    private Long id;

    private String title;
    private String isbn;

    @ManyToMany(mappedBy = "books")
    private Set<Author> authors;

    @ManyToOne
    // Opcional porque estamos usando el valor por defecto
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    public Book() {
        authors = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}
