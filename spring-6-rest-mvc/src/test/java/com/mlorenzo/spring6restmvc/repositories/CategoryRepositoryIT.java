package com.mlorenzo.spring6restmvc.repositories;

import com.mlorenzo.spring6restmvc.bootstrap.BootstrapData;
import com.mlorenzo.spring6restmvc.entities.Beer;
import com.mlorenzo.spring6restmvc.entities.Category;
import com.mlorenzo.spring6restmvc.services.BeerCsvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Set;

@DataJpaTest
// La interfaz BeerCsvService es una dependencia requerida por la clase BootstrapData, así que tenemos que importar
// también una implementación suya(BeerCsvServiceImpl)
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class CategoryRepositoryIT {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BeerRepository beerRepository;

    Beer testBeer;

    @BeforeEach
    void setUp() {
        testBeer = beerRepository.findAll().get(0);
    }

    @Test
    void testAddCategory() {
        Category category = Category.builder()
                .description("Desc")
                .beers(Set.of(testBeer))
                .build();
        testBeer.getCategories().add(category);
        Category savedCategory = categoryRepository.save(category);
        System.out.println(savedCategory.getDescription());
    }
}