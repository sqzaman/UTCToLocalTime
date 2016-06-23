package com.sample;


import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main {

    public static void main(String[] args) {

        try {

            // get instance of Helper, which is a singletone class
            Helper helper = Helper.getInstance();

            List<LocationDetails> locationDetailsList = new ArrayList<LocationDetails>();
            CsvProcessor csvProcessor = new CsvProcessor(helper);

            // read source csv file and put it in a list
            List<Location> locations = csvProcessor.deserialize();


            DateFormat utcFormat = new SimpleDateFormat(helper.UTCPattern);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            long timestamp;
            String  latitude, longitude;

            // call google api to get timezone information for each item in the location list
            for (Location location : locations) {
                Date date = utcFormat.parse(location.date);
                timestamp = (date.getTime())/1000;
                latitude = location.latitude;
                longitude = location.longitude;
                TimeZoneMapper timeZoneMapper = helper.getTimeZone(helper.getTimeZoneJson(latitude, longitude, timestamp));

                locationDetailsList.add(helper.newLocationDetails(date, latitude, longitude, timeZoneMapper));
            }

            StringWriter writer = csvProcessor.serialize(locationDetailsList);

            System.out.println(writer.toString());

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
