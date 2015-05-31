package com.socialreader.core;

import com.google.gson.Gson;
import com.socialreader.input.InputReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
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

    public static final int SEARCH_PAGE_SIZE = 3; // 1..8
    public static final int SEARCH_PAGES = 6;
    public static final int MAX_RESULTS = SEARCH_PAGES * SEARCH_PAGE_SIZE;

    public static final String SEARCH_QUERY = "site:linkedin.com/in/ OR site:linkedin.com/pub/ -site:linkedin.com/pub/dir/";
    private static final Logger LOGGER = LoggerFactory.getLogger(GooglePersonFinder.class);
    public static final String GOOGLE_SEARCH_QUERY = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=%s&start=%s&q=";

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
        return doGoogleSearch();
    }

    private Set<ProfileBuilder> doGoogleSearch() {
        Set<ProfileBuilder> profileBuilders = new HashSet<ProfileBuilder>();
        try {
            for (int resultIndex = 0; resultIndex < MAX_RESULTS; resultIndex += SEARCH_PAGE_SIZE) {
                String searchAddress = String.format(GOOGLE_SEARCH_QUERY, SEARCH_PAGE_SIZE, resultIndex);
                URL url = new URL(searchAddress + URLEncoder.encode(getSearchQuery(), "UTF-8"));
                Reader reader = new InputStreamReader(url.openStream(), "UTF-8");
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
        } catch (Exception x) {
            LOGGER.error("Error while searching in google", x);
        }
        return profileBuilders;
    }

    private Set<String> getDataFromGoogle(String query) {

        Set<String> result = new HashSet<String>();
        String request = "https://www.google.com/search?q=" + query + "&num=20";
        System.out.println("Sending request..." + request);

        try {

            // need http protocol, set this as a Google bot agent :)
            Document doc = Jsoup
                    .connect(request)
                    .userAgent(
                            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                    .timeout(5000).get();

            // get all links
            Elements links = doc.select("a[href]");
            for (Element link : links) {

                String temp = link.attr("href");
                if(temp.startsWith("/url?q=")){
                    //use regex to get domain name
                    result.add(getDomainName(temp));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) throws Exception {

        URL url = new URL("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=8&start=0" +
                "&q=%22Director+*+*+Present%22+%22Industry+*+Design%22+%22Location+*+Hong+Kong%22+site%3Alinkedin.com%2Fin%2F+OR+site%3Alinkedin.com%2Fpub%2F+-site%3Alinkedin.com%2Fpub%2Fdir%2F" +
                "&userip=" + InetAddress.getLocalHost());
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("Referer", "http://linkedin.com");

        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while((line = reader.readLine()) != null) {
            builder.append(line);
        }
        System.out.println(builder);
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
