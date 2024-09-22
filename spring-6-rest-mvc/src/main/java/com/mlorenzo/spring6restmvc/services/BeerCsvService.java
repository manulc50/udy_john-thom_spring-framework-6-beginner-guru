package com.mlorenzo.spring6restmvc.services;

import com.mlorenzo.spring6restmvc.models.BeerCSVRecord;

import java.io.File;
import java.util.List;

public interface BeerCsvService {
    List<BeerCSVRecord> convertCSV(File csvFile);
}
