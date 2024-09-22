package com.mlorenzo.spring6resttemplate.clients;

import com.mlorenzo.spring6resttemplate.models.BeerDTO;
import com.mlorenzo.spring6resttemplate.models.BeerDTOPageImpl;
import com.mlorenzo.spring6resttemplate.models.BeerStyle;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
public class BeerClientImpl implements BeerClient {
    public static final String BEER_PATH = "/api/v1/beers";
    public static final String BEER_ID_PATH = "/api/v1/beers/{id}";

    private final RestTemplate restTemplate;

    public BeerClientImpl(final RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Page<BeerDTO> getAll() {
        return this.getAll(null, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> getAll(String name) {
        return this.getAll(name, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> getAll(String name, BeerStyle style, Boolean showInventory,
                                Integer pageNumber, Integer pageSize) {
        /*ResponseEntity<String> stringResponse = restTemplate
                .getForEntity(GET_BEER_PATH, String.class);
        ResponseEntity<Map> mapResponse = restTemplate
                .getForEntity(GET_BEER_PATH, Map.class);
        ResponseEntity<JsonNode> jsonResponse = restTemplate
                .getForEntity(GET_BEER_PATH, JsonNode.class);
        jsonResponse.getBody().findPath("content").elements()
                .forEachRemaining(node -> System.out.println(node.get("name").asText()));*/
        // Usamos el método "fromPath" porque hemos configurado la url base en el componente de Spring RestTemplateBuilder
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(BEER_PATH);
        if(name != null)
            uriComponentsBuilder.queryParam("name", name);
        if(style != null)
            uriComponentsBuilder.queryParam("style", style);
        if(showInventory != null)
            uriComponentsBuilder.queryParam("showInventory", showInventory);
        if(pageNumber != null)
            uriComponentsBuilder.queryParam("pageNumber", pageNumber);
        if(pageSize != null)
            uriComponentsBuilder.queryParam("showInventory", pageSize);
        ResponseEntity<BeerDTOPageImpl> response = restTemplate
                .getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);
        return response.getBody();
    }

    @Override
    public BeerDTO getById(UUID id) {
        return restTemplate.getForObject(BEER_ID_PATH, BeerDTO.class, id);
    }

    @Override
    public BeerDTO create(BeerDTO beerDTO) {
        // Se comenta porque este caso es para cuando el recurso creado se devuelve en la respuesta http
        //ResponseEntity<BeerDTO> response = restTemplate.postForEntity(BEER_PATH, beerDTO, BeerDTO.class);
        // Nuestro caso no devuelve el recurso creado en la respuesta http, sino que devuelve únicamente
        // la cabecera "Location"
        URI uri = restTemplate.postForLocation(BEER_PATH, beerDTO);
        return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO update(BeerDTO beerDTO) {
        restTemplate.put(BEER_ID_PATH, beerDTO, beerDTO.getId());
        return getById(beerDTO.getId());
    }

    @Override
    public void delete(UUID id) {
        restTemplate.delete(BEER_ID_PATH, id);
    }
}
