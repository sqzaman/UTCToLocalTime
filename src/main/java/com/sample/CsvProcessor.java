package com.sample;

import net.sf.jsefa.Deserializer;
import net.sf.jsefa.csv.CsvIOFactory;
import net.sf.jsefa.csv.CsvSerializer;
import net.sf.jsefa.csv.config.CsvConfiguration;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class is for processing the csv file
 */
public class CsvProcessor {

    /**
     * Member variables
     */
    private Helper helper;
    private Properties prop;

    /**
     * Default constructor, Inject singletone instnace of Helper class
     * @param helper
     */
    public CsvProcessor(Helper helper){
      this.helper = helper;
      this.prop = helper.getConfig();
    }

    /**
     * Read locations from csv file and make them iterate able object list
     *
     * @return List of locations object
     */
    public List<Location> deserialize() {
        List<Location> locations = new ArrayList<Location>();
        Deserializer deserializer = CsvIOFactory.createFactory(getConfig(), Location.class).createDeserializer();
        deserializer.open(createFileReader());
        while (deserializer.hasNext()) {
            locations.add((Location) deserializer.next());
        }
        deserializer.close(true);

        return locations;
    }

    /**
     * Generate csv configuration object
     *
     * @return CsvConfiguration object
     */
    private CsvConfiguration getConfig(){
        char fieldDelimiter = this.prop.getProperty("csvDelimiter").charAt(0);
        CsvConfiguration config = new CsvConfiguration();
        config.setFieldDelimiter(fieldDelimiter);
        return config;
    }

    /**
     * Create csv file reader object
     *
     * @return Reader object
     */
    private Reader createFileReader() {
        String csvFilePath = this.prop.getProperty("csvFilePath");
        return new InputStreamReader(this.getClass().getResourceAsStream(csvFilePath));
    }

    /**
     * Convert object to printable/writable output in a string buffer
     *
     * @param locationDetailsList
     * @return StringWriter object
     */
    public StringWriter serialize(List<LocationDetails> locationDetailsList){
        CsvSerializer serializer =  CsvIOFactory.createFactory(getConfig(), LocationDetails.class).createSerializer();
        StringWriter writer = new StringWriter();
        serializer.open(writer);
        for (Location LocationDetails : locationDetailsList) {
            serializer.write(LocationDetails);
        }
        serializer.close(true);

        return writer;
    }


}
