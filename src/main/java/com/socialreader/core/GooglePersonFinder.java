package com.socialreader.core;

import com.socialreader.input.InputReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Brad
 */
public class GooglePersonFinder {

    public static final String SEARCH_QUERY = "site:linkedin.com/in/ OR site:linkedin.com/pub/ -site:linkedin.com/pub/dir/";
    private static final Logger LOGGER = LoggerFactory.getLogger(GooglePersonFinder.class);
    public static final int PEOPLE_TO_FIND = 50;

    private StringBuilder searchQuery = new StringBuilder();
    private InputReader inputReader;

    public void addTitles() {
        searchQuery.insert(0, " ");
        searchQuery.insert(0, inputReader.getTitles().stream().map(this::mapTitle).collect(orJoining()));
    }

    private Collector<CharSequence, ?, String> orJoining() {
        return Collectors.joining(" OR ");
    }

    private String mapTitle(String title) {
        return String.format("\"%s * * Present\"", title);
    }

    public String getSearchQuery() {
        return searchQuery.toString();
    }

    public void addCompanies() {
        addOrDelimitedList(inputReader.getCompanies());
    }

    public void addSchools() {
        addOrDelimitedList(inputReader.getSchools());
    }

    public void addLocations() {
        searchQuery.insert(0, " ");
        searchQuery.insert(0, inputReader.getLocations().stream().map(this::mapLocation).collect(orJoining()));
    }

    private String mapLocation(String location) {
        return String.format("\"Location * %s\"", location);
    }

    public void addIndustries() {
        searchQuery.insert(0, " ");
        searchQuery.insert(0, inputReader.getIndustries().stream().map(this::mapIndustry).collect(orJoining()));
    }

    private String mapIndustry(String i) {
        return String.format("\"Industry * %s\"", i);
    }

    public void addKeyWords() {
        searchQuery.insert(0, " ");
        searchQuery.insert(0, inputReader.getKeyWords().stream().collect(Collectors.joining(" ")));
    }

    private void addOrDelimitedList(List<String> valuesList) {
        searchQuery.insert(0, " ");
        searchQuery.insert(0, valuesList.stream().map(v -> "\"" + v + "\"").collect(orJoining()));
    }

    public void configureSearch(InputReader inputReader) {
        this.searchQuery.append(SEARCH_QUERY);
        this.inputReader = inputReader;
        configureSearch();
        if (!inputReader.getFirstName().isEmpty()) {
            searchQuery.insert(0, " ");
            searchQuery.insert(0, inputReader.getFirstName());
        }
        if (!inputReader.getLastName().isEmpty()) {
            searchQuery.insert(0, " ");
            searchQuery.insert(0, inputReader.getLastName());
        }
        LOGGER.debug("Google search query: {}", searchQuery);
    }

    private void configureSearch() {
        addLocations();
        addIndustries();
        addTitles();
    }

    public Set<ProfileBuilder> generateProfileBuilders() {
        return getDataFromGoogle(getSearchQuery()).stream().map(ProfileBuilder::new).collect(Collectors.toSet());
    }

    private Set<String> getDataFromGoogle(String query) {
        Set<String> result = new HashSet<>();
        try {
            String request = String.format("https://www.google.com/search?q=%s&num=%s", URLEncoder.encode(query, "UTF-8"), PEOPLE_TO_FIND);
            LOGGER.debug("Sending request..." + request);
            // need http protocol, set this as a Google bot agent :)
            return Jsoup.connect(request)
                    .userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                    .timeout(10000).get().select("cite").stream().map(Element::html).collect(Collectors.toSet());
        } catch (IOException e) {
            LOGGER.error("Error while google", e);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        new GooglePersonFinder().getDataFromGoogle("\"CEO * * Present\" OR \"President * * Present\" OR \"owner * * Present\" OR \"CFO * * Present\" \"Restaurants\" OR \"Food & Beverages\" \"Greater Los Angeles Area\" site:linkedin.com/in/ OR site:linkedin.com/pub/ -site:linkedin.com/pub/dir/").forEach(System.out::println);
    }

}