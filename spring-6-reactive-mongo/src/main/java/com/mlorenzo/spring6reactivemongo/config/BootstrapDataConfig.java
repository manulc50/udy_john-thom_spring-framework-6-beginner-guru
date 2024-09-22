package com.mlorenzo.spring6reactivemongo.config;

import com.mlorenzo.spring6reactivemongo.domains.Beer;
import com.mlorenzo.spring6reactivemongo.domains.Customer;
import com.mlorenzo.spring6reactivemongo.repositories.BeerRepository;
import com.mlorenzo.spring6reactivemongo.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Configuration
public class BootstrapDataConfig {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    @Bean
    public CommandLineRunner bootstrapData() {
        return args -> {
            beerRepository.deleteAll()
                    // Versión simplificada de la expresión "() -> loadBeerData()"
                    .doOnTerminate(this::loadBeerData)
                    .subscribe();
            customerRepository.deleteAll()
                    // Versión simplificada de la expresión "() -> loadCustomerData()"
                    .doOnTerminate(this::loadCustomerData)
                    .subscribe();
        };
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
