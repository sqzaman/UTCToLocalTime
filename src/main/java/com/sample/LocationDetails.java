package com.sample;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;


@CsvDataType()
public class LocationDetails extends Location {

    @CsvField(pos = 4)
    String location;

    @CsvField(pos = 5)
    String localDateTime;


}
