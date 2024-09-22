package com.mlorenzo.spring6restmvc.bootstrap;

import com.mlorenzo.spring6restmvc.entities.Beer;
import com.mlorenzo.spring6restmvc.entities.Customer;
import com.mlorenzo.spring6restmvc.models.BeerCSVRecord;
import com.mlorenzo.spring6restmvc.models.BeerStyle;
import com.mlorenzo.spring6restmvc.repositories.BeerRepository;
import com.mlorenzo.spring6restmvc.repositories.CustomerRepository;
import com.mlorenzo.spring6restmvc.services.BeerCsvService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Component
public class BootstrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final BeerCsvService beerCsvService;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        final long countBeers = beerRepository.count();
        final long countCustomers = customerRepository.count();
        if(countBeers == 0)
            loadBeerData();
        if(countBeers < 10)
            loadCsvData();
        if(countCustomers == 0)
            loadCustomerData();
    }

    private void loadBeerData() {
        Beer beer1 = Beer.builder()
                .name("Galaxy Cat")
                .style(BeerStyle.PALE_ALE)
                .upc("12356111")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(122)
                .build();
        Beer beer2 = Beer.builder()
                .name("Crank")
                .style(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .build();
        Beer beer3 = Beer.builder()
                .name("Sunshine City")
                .style(BeerStyle.IPA)
                .upc("12356777")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(144)
                .build();
        beerRepository.saveAll(List.of(beer1, beer2, beer3));
    }

    private void loadCsvData() throws FileNotFoundException {
        File csvFile = ResourceUtils.getFile("classpath:csvdata/beers.csv");
        List<BeerCSVRecord> beerCSVRecords =  beerCsvService.convertCSV(csvFile);
        List<Beer> beers = beerCSVRecords.stream()
                .map(beerCSVRecord -> {
                    final BeerStyle beerStyle = switch (beerCSVRecord.getStyle()) {
                        case "American Pale Lager" -> BeerStyle.LAGER;
                        case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                                BeerStyle.ALE;
                        case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
                        case "American Porter" -> BeerStyle.PORTER;
                        case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
                        case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
                        case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
                        case "English Pale Ale" -> BeerStyle.PALE_ALE;
                        default -> BeerStyle.PILSNER;
                    };
                    return Beer.builder()
                            // En la base de datos, solo se permiten nombres de hasta 50 caracteres como máximo
                            .name(StringUtils.abbreviate(beerCSVRecord.getBeer(), 50))
                            .style(beerStyle)
                            .price(BigDecimal.TEN)
                            .upc(beerCSVRecord.getRow().toString())
                            .quantityOnHand(beerCSVRecord.getCount_x())
                            .build();
                })
                .collect(Collectors.toList());
        beerRepository.saveAll(beers);
    }

    private void loadCustomerData() {
        Customer customer1 = Customer.builder()
                .name("Customer 1")
                .email("customer1@test.com")
                .build();
        Customer customer2 = Customer.builder()
                .name("Customer 2")
                .email("customer2@test.com")
                .build();
        Customer customer3 = Customer.builder()
                .name("Customer 3")
                .email("customer3@test.com")
                .build();
        customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));
    }
}
