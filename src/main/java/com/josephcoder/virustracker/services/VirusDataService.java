package com.josephcoder.virustracker.services;

import com.josephcoder.virustracker.models.Location;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service //We add it in order that our prog would know that
public class VirusDataService {

    private static String VIRUS_DATASOURCE_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";

    private List<Location> allStates = new ArrayList<>();

    public List<Location> getAllStates() {
        return allStates;
    }

    /**
     * The fetchVirusData class fetches the data about the virus.
     */
    @PostConstruct //First initialize this method
    @Scheduled(cron = "* * 1 * * *") // runs every day    ( <minute> <hour> <day-of-month> <month> <day-of-week> <command>)
    public void fetchVirusData() throws IOException, InterruptedException { //InterruptedException - if we want to send something but connection was lost
        List<Location> newStats = new ArrayList<>(); //in order to give user access to the list no matter if our program is fetching the data now or not
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATASOURCE_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString()); //.ofString() - we expect that answer would be String

        StringReader csvReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader()
                .parse(csvReader);
        for(CSVRecord record : records) {
            Location location = new Location();
            location.setState(record.get("Province/State"));
            location.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get("11/4/20")); //record.get(record.size()-1)
            int prevDayCases = Integer.parseInt(record.get("10/4/20")); //record.get(record.size()-2)
            location.setLatestTotalCases(latestCases);
            location.setDelta(prevDayCases);
            System.out.println(location);
            newStats.add(location);

        }
        this.allStates = newStats;


        //System.out.println(httpResponse.body());
    }
}
