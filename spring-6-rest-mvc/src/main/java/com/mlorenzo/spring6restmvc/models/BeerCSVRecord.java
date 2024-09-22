package com.mlorenzo.spring6restmvc.models;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class BeerCSVRecord {

    // Nota: La anotación @CsvBindByName asocia propiedades de una clase con cabeceras de un CSV a través de sus
    // nombres. Si no coinciden, debe usarse el atributo "column" especificándo el nombre de la cabecera

    @CsvBindByName
    private Integer row;

    @CsvBindByName(column = "count.x")
    private Integer count_x;

    @CsvBindByName
    private String abv;

    @CsvBindByName
    private String ibu;

    @CsvBindByName
    private Integer id;

    @CsvBindByName
    private String beer;

    @CsvBindByName
    private String style;

    @CsvBindByName(column = "beer_id")
    private Integer breweryId;

    @CsvBindByName
    private Float ounces;

    @CsvBindByName
    private String style2;

    @CsvBindByName(column = "count.y")
    private String count_y;

    @CsvBindByName
    private String city;

    @CsvBindByName
    private String state;

    @CsvBindByName
    private String label;
}
