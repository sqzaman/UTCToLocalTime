package com.sample;


import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

@CsvDataType()
public class Location {
    @CsvField(pos = 1)
    String date;

    @CsvField(pos = 2)
    String latitude;

    @CsvField(pos = 3)
    String longitude;
}
