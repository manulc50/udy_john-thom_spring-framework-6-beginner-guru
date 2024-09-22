package com.mlorenzo.spring6datarest.bootstrap;

import com.mlorenzo.spring6datarest.domains.Beer;
import com.mlorenzo.spring6datarest.domains.BeerStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mlorenzo.spring6datarest.repositories.BeerRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BeerLoader {
    private static final String BEER_1_UPC = "0631234200036";
    private static final String BEER_2_UPC = "9122089364369";
    private static final String BEER_3_UPC = "0083783375213";
    private static final String BEER_4_UPC = "4666337557578";
    private static final String BEER_5_UPC = "8380495518610";
    private static final String BEER_6_UPC = "5677465691934";
    private static final String BEER_7_UPC = "5463533082885";
    private static final String BEER_8_UPC = "5339741428398";
    private static final String BEER_9_UPC = "1726923962766";
    private static final String BEER_10_UPC = "8484957731774";
    private static final String BEER_11_UPC = "6266328524787";
    private static final String BEER_12_UPC = "7490217802727";
    private static final String BEER_13_UPC = "8579613295827";
    private static final String BEER_14_UPC = "2318301340601";
    private static final String BEER_15_UPC = "9401790633828";
    private static final String BEER_16_UPC = "4813896316225";
    private static final String BEER_17_UPC = "3431272499891";
    private static final String BEER_18_UPC = "2380867498485";
    private static final String BEER_19_UPC = "4323950503848";
    private static final String BEER_20_UPC = "4006016803570";
    private static final String BEER_21_UPC = "9883012356263";
    private static final String BEER_22_UPC = "0583668718888";
    private static final String BEER_23_UPC = "9006801347604";
    private static final String BEER_24_UPC = "0610275742736";
    private static final String BEER_25_UPC = "6504219363283";
    private static final String BEER_26_UPC = "7245173761003";
    private static final String BEER_27_UPC = "0326984155094";
    private static final String BEER_28_UPC = "1350188843012";
    private static final String BEER_29_UPC = "0986442492927";
    private static final String BEER_30_UPC = "8670687641074";

    private final BeerRepository beerRepository;

    @Bean
    public CommandLineRunner loadBeers() {
        return args -> loadBeerObjects();
    }

    private void loadBeerObjects() {
        log.debug("Loading initial data. Count is: {}", beerRepository.count() );
        if(beerRepository.count() == 0) {
            Random random = new Random();

            beerRepository.save(Beer.builder()
                    .name("Mango Bobs")
                    .style(BeerStyle.ALE)
                    .upc(BEER_1_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Galaxy Cat")
                    .style(BeerStyle.PALE_ALE)
                    .upc(BEER_2_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("No Hammers On The Bar")
                    .style(BeerStyle.WHEAT)
                    .upc(BEER_3_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Blessed")
                    .style(BeerStyle.STOUT)
                    .upc(BEER_4_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Adjunct Trail")
                    .style(BeerStyle.STOUT)
                    .upc(BEER_5_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Very GGGreenn")
                    .style(BeerStyle.IPA)
                    .upc(BEER_6_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Double Barrel Hunahpu's")
                    .style(BeerStyle.STOUT)
                    .upc(BEER_7_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Very Hazy")
                    .style(BeerStyle.IPA)
                    .upc(BEER_8_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("SR-71")
                    .style(BeerStyle.STOUT)
                    .upc(BEER_9_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Pliny the Younger")
                    .style(BeerStyle.IPA)
                    .upc(BEER_10_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Blessed")
                    .style(BeerStyle.STOUT)
                    .upc(BEER_11_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("King Krush")
                    .style(BeerStyle.IPA)
                    .upc(BEER_12_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("PBS Porter")
                    .style(BeerStyle.PORTER)
                    .upc(BEER_13_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Pinball Porter")
                    .style(BeerStyle.STOUT)
                    .upc(BEER_14_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Golden Budda")
                    .style(BeerStyle.STOUT)
                    .upc(BEER_15_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Grand Central Red")
                    .style(BeerStyle.LAGER)
                    .upc(BEER_16_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Pac-Man")
                    .style(BeerStyle.STOUT)
                    .upc(BEER_17_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Ro Sham Bo")
                    .style(BeerStyle.IPA)
                    .upc(BEER_18_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Summer Wheatly")
                    .style(BeerStyle.WHEAT)
                    .upc(BEER_19_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Java Jill")
                    .style(BeerStyle.LAGER)
                    .upc(BEER_20_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Bike Trail Pale")
                    .style(BeerStyle.PALE_ALE)
                    .upc(BEER_21_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("N.Z.P")
                    .style(BeerStyle.IPA)
                    .upc(BEER_22_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Stawberry Blond")
                    .style(BeerStyle.WHEAT)
                    .upc(BEER_23_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Loco")
                    .style(BeerStyle.PORTER)
                    .upc(BEER_24_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Spocktoberfest")
                    .style(BeerStyle.STOUT)
                    .upc(BEER_25_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Beach Blond Ale")
                    .style(BeerStyle.ALE)
                    .upc(BEER_26_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Bimini Twist IPA")
                    .style(BeerStyle.IPA)
                    .upc(BEER_27_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Rod Bender Red Ale")
                    .style(BeerStyle.ALE)
                    .upc(BEER_28_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("Floating Dock")
                    .style(BeerStyle.SAISON)
                    .upc(BEER_29_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            beerRepository.save(Beer.builder()
                    .name("El Hefe")
                    .style(BeerStyle.WHEAT)
                    .upc(BEER_30_UPC)
                    .price(new BigDecimal(BigInteger.valueOf(random.nextInt(10000)), 2))
                    .quantityOnHand(random.nextInt(5000))
                    .build());

            log.debug("Beer Records loaded: {}", beerRepository.count());
        }
    }
}
