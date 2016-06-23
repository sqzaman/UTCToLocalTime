package com.sample;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Provide all helper method of this project
 */
public class Helper {
    private static Helper instance = null;
    final String UTCPattern = "yyyy-MM-dd HH:mm:ss";

    /**
     * Default constructor is private, to make sure it an be instantiated from inside of this class
     */
    private Helper() {
    }

    /**
     * Global access point of this class
     *
     * @return instance of Helper class
     */
    public static Helper getInstance() {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (Helper.class) {
                if (instance == null) {
                    instance = new Helper();
                }
            }
        }
        return instance;
    }

    /**
     * Get TimeZone via Google API call
     *
     * @param latitude
     * @param longitude
     * @param timestamp
     * @return Json string
     */
    public String getTimeZoneJson(String latitude, String longitude, long timestamp){
        StringBuilder jsonOutput = null;

        try {

            Properties prop = this.getConfig();
            String reqUrl = String.format("%s?location=%s,%s&timestamp=%d", prop.getProperty("timeZoneAPIUrl"), latitude, longitude, timestamp);

            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            jsonOutput = new StringBuilder();

            while ((output = br.readLine()) != null) {
                jsonOutput.append(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonOutput.toString();
    }

    /**
     * Convert json string to object
     *
     * @param jsonStr
     * @return object of TimeZoneMapper class
     */
    public TimeZoneMapper getTimeZone(String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();
        TimeZoneMapper timeZoneMapper = null;
        try{
            timeZoneMapper = mapper.readValue(jsonStr, TimeZoneMapper.class);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return timeZoneMapper;
    }

    /**
     * Load configuration file
     * @return input stream
     */
    public Properties getConfig(){
        Properties prop = new Properties();
        InputStream inputStream;
        String propFileName = "config/config.properties";
        try{
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    /**
     * Create a LocationDetails class object for given value
     * @param date
     * @param latitude
     * @param longitude
     * @param timeZoneMapper
     * @return a newly cretaed object of LocationDetails class
     */
    public LocationDetails newLocationDetails(Date date, String latitude, String longitude, TimeZoneMapper timeZoneMapper){
        LocationDetails locationDetails = new LocationDetails();
        String localPattern = "yyyy-MM-dd'T'HH:mm:ss";

        Long timestamp = date.getTime();
        timestamp += timeZoneMapper.getDstOffset().longValue()*1000 + timeZoneMapper.getRawOffset().longValue()*1000;
        locationDetails.date = new SimpleDateFormat(UTCPattern).format(date);
        locationDetails.latitude = latitude;
        locationDetails.longitude = longitude;
        locationDetails.location = timeZoneMapper.getTimeZoneId();
        locationDetails.localDateTime = DateFormatUtils.formatUTC(timestamp, localPattern);

        return locationDetails;
    }

}
