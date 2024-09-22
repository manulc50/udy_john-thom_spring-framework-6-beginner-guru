package com.mlorenzo.spring6datarest.repositories;

import com.mlorenzo.spring6datarest.domains.Beer;
import com.mlorenzo.spring6datarest.domains.BeerStyle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
import java.util.UUID;

// Con el atributo "path" de esta anotación, podemos cambiar la ruta por defecto que se corresponde con el nombre de
// la clase entidad en minúscula y en plural
// Con el atributo "collectionResourceRel" de esta anotación, podemos cambiar el nombre por defecto del atributo o
// propiedad que se asocia a colecciones de un determinado recurso. Por defecto, también se utiliza el nombre de la
// clase entidad en minúscula y en plural
@RepositoryRestResource(path = "beer", collectionResourceRel = "beer")
public interface BeerRepository extends JpaRepository<Beer, UUID> {
    Page<Beer> findAllByName(String name, Pageable pageable);
    Page<Beer> findAllByStyle(BeerStyle style, Pageable pageable);
    Page<Beer> findAllByNameAndStyle(String name, BeerStyle style, Pageable pageable);
    Optional<Beer> findByUpc(String upc);
}
