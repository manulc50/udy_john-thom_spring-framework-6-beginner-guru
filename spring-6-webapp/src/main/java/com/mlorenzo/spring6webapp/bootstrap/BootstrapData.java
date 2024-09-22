package com.mlorenzo.spring6webapp.bootstrap;

import com.mlorenzo.spring6webapp.domains.Author;
import com.mlorenzo.spring6webapp.domains.Book;
import com.mlorenzo.spring6webapp.domains.Publisher;
import com.mlorenzo.spring6webapp.repositories.AuthorRepository;
import com.mlorenzo.spring6webapp.repositories.BookRepository;
import com.mlorenzo.spring6webapp.repositories.PublisherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BootstrapData {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;

    public BootstrapData(AuthorRepository authorRepository, BookRepository bookRepository,
                         PublisherRepository publisherRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
    }

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            Author eric = new Author();
            eric.setFirstName("Eric");
            eric.setLastName("Evans");

            Book ddd = new Book();
            ddd.setTitle("Domain Driven Design");
            ddd.setIsbn("123456");

            authorRepository.save(eric);
            bookRepository.save(ddd);

            Author rod = new Author();
            rod.setFirstName("Rod");
            rod.setLastName("Johnson");

            Book noEjb = new Book();
            noEjb.setTitle("J2EE Development without EJB");
            noEjb.setIsbn("54757585");

            authorRepository.save(rod);
            bookRepository.save(noEjb);

            Publisher myPublisher = new Publisher();
            myPublisher.setName("My Publisher");
            myPublisher.setAddress("My Publisher Address");
            myPublisher.setCity("My Publisher City");
            myPublisher.setState("My Publisher State");
            myPublisher.setZipCode("My Publisher ZipCode");

            publisherRepository.save(myPublisher);

            eric.getBooks().add(ddd);
            rod.getBooks().add(noEjb);
            ddd.setPublisher(myPublisher);
            noEjb.setPublisher(myPublisher);
            authorRepository.saveAll(List.of(eric, rod));
            bookRepository.saveAll(List.of(ddd, noEjb));

            System.out.println("In Bootstrap");
            System.out.println("Author count: " + authorRepository.count());
            System.out.println("Book count: " + bookRepository.count());
            System.out.println("Publisher count: " + publisherRepository.count());
        };
    }
}
