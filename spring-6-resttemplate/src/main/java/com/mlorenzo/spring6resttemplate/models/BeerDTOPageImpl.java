package com.mlorenzo.spring6resttemplate.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

// Por defecto, Jackson no sabe como deserializar("unmarshall") un Json a un objeto de tipo Page(Page es la interfaz
// y PageImpl su implementación). Por esta razón, usamos esta clase para decirle a Jackson cómo tiene que realizar
// la deserialización

// Indicamos a Jackson que ignore del Json el campo "pageable" ya que no interesa su contenido y, además, no hay
// ningún mapeo en el constructor para ese campo
@JsonIgnoreProperties(ignoreUnknown = true, value = "pageable")
public class BeerDTOPageImpl extends PageImpl<BeerDTO> {

    // Jackson usará este constructor para la deserialización debido a que está anotado con la anotación @JsonCreator
    // Mediante la anotación @JsonProperty, asociamos y mapeamos campos del Json con los argumentos de entrada
    // del constructor
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BeerDTOPageImpl(@JsonProperty("content") List<BeerDTO> content,
                           @JsonProperty("number") int page,
                           @JsonProperty("size") int size,
                           @JsonProperty("totalElements") long total) {
        super(content, PageRequest.of(page, size), total);
    }

    public BeerDTOPageImpl(List<BeerDTO> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public BeerDTOPageImpl(List<BeerDTO> content) {
        super(content);
    }
}
