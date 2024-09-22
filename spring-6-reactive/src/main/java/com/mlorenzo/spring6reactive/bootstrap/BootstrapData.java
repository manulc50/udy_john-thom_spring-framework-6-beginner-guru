package com.mlorenzo.spring6reactive.bootstrap;

import com.mlorenzo.spring6reactive.domains.Beer;
import com.mlorenzo.spring6reactive.domains.Customer;
import com.mlorenzo.spring6reactive.repositories.BeerRepository;
import com.mlorenzo.spring6reactive.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class BootstrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        beerRepository.count().subscribe(beerCount -> {
            if(beerCount == 0)
                loadBeerData();
        });

        customerRepository.count().subscribe(count -> {
            if(count == 0)
                loadCustomerData();
        });
    }

    private void loadBeerData() {
        Beer beer1 = Beer.builder()
                .name("Galaxy Cat")
                .style("PALE_ALE")
                .upc("12356111")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(122)
                .build();
        Beer beer2 = Beer.builder()
                .name("Crank")
                .style("PALE_ALE")
                .upc("12356222")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .build();
        Beer beer3 = Beer.builder()
                .name("Sunshine City")
                .style("IPA")
                .upc("12356777")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(144)
                .build();
        beerRepository.saveAll(List.of(beer1, beer2, beer3))
                .then(beerRepository.count())
                .subscribe(count -> System.out.printf("Beer count is: %d\n", count));
    }

    private void loadCustomerData() {
        Customer customer1 = Customer.builder()
                .name("Customer 1")
                .build();
        Customer customer2 = Customer.builder()
                .name("Customer 2")
                .build();
        Customer customer3 = Customer.builder()
                .name("Customer 3")
                .build();
        customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3))
                .then(customerRepository.count())
                .subscribe(count -> System.out.printf("Customer count is: %d\n", count));
    }
}
