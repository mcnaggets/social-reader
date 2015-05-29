package com.socialreader.core;

import com.google.gson.Gson;
import com.socialreader.input.InputReader;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Brad
 */
public class GooglePersonFinder {

    private static final int MAX_RESULTS = 48;
    private static final String SEARCH_QUERY = "site:linkedin.com/in/ OR site:linkedin.com/pub/ -site:linkedin.com/pub/dir/";

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
        return "\"" + title + " * * Present\"";
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
        searchQuery.insert(0, inputReader.getLocations().stream().map(mapLocation()).collect(orJoining()));
    }

    private Function<String, String> mapLocation() {
        return v -> "\"location * " + v + "\"";
    }

    public void addIndustries() {
        searchQuery.insert(0, " ");
        searchQuery.insert(0, inputReader.getIndustries().stream().map(mapIndustry()).collect(orJoining()));
    }

    private Function<String, String> mapIndustry() {
        return i -> "\"industry * " + i + "\"";
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
    }

    private void configureSearch() {
        addLocations();
        addIndustries();
        addTitles();
    }

    public Set<ProfileBuilder> generateProfileBuilders() {
        return doGoogleSearch();
    }

    private Set<ProfileBuilder> doGoogleSearch() {
        Set<ProfileBuilder> profileBuilders = new HashSet<ProfileBuilder>();
        try {
            for (int resultIndex = 0; resultIndex < MAX_RESULTS; resultIndex += 8) {
                String searchAddress = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=8&start=" + resultIndex + "&q=";
                String charset = "UTF-8";
                URL url = new URL(searchAddress + URLEncoder.encode(getSearchQuery(), charset));
                Reader reader = new InputStreamReader(url.openStream(), charset);
                GoogleResults googleResults = new Gson().fromJson(reader, GoogleResults.class);
                GoogleResults.ResponseData responseData = googleResults.getResponseData();
                if (responseData != null) {
                    List<GoogleResults.Result> resultsList = responseData.getResults();
                    if (resultsList.isEmpty()) {
                        return profileBuilders;
                    }
                    for (GoogleResults.Result aResultsList : resultsList) {
                        String linkedInUrl = aResultsList.getUrl();
                        ProfileBuilder profileBuilder = new ProfileBuilder(linkedInUrl);
                        profileBuilders.add(profileBuilder);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return profileBuilders;
    }
}

class GoogleResults {

    private ResponseData responseData;

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }

    public String toString() {
        return "ResponseData[" + responseData + "]";
    }

    static class ResponseData {
        private List<Result> results;

        public List<Result> getResults() {
            return results;
        }

        public void setResults(List<Result> results) {
            this.results = results;
        }

        public String toString() {
            return "Results[" + results + "]";
        }
    }

    static class Result {
        private String url;
        private String title;

        public String getUrl() {
            return url;
        }

        public String getTitle() {
            return title;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String toString() {
            return "Result[url:" + url + ",title:" + title + "]";
        }
    }
}
