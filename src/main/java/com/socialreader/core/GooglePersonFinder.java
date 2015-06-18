package com.socialreader.core;

import com.socialreader.input.InputReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.net.URLEncoder.encode;

/**
 * @author Brad
 */
public class GooglePersonFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GooglePersonFinder.class);

    public static final String SEARCH_QUERY = "site:linkedin.com/in/ OR site:linkedin.com/pub/ -site:linkedin.com/pub/dir/";
    public static final String GOOGLE_SEARCH_TEMPLATE = "https://www.google.com/search?gws_rd=cr&as_qdr=all&q=%s&start=%s&num=%s";
    public static final int PAGE_SIZE = 20;

    private final StringBuilder searchQuery = new StringBuilder();
    private final InputReader inputReader;

    public GooglePersonFinder(InputReader inputReader) {
        this.inputReader = inputReader;
        configureSearch(inputReader);
    }

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

    private void configureSearch(InputReader inputReader) {
        this.searchQuery.append(SEARCH_QUERY);
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
        addKeyWords();
    }

    public Set<ProfileBuilder> generateProfileBuilders() {
        return getDataFromGoogle().stream().map(ProfileBuilder::new).collect(Collectors.toSet());
    }

    private Set<String> getDataFromGoogle() {
        Set<String> result = new HashSet<>();
        try {
            int maxResults = inputReader.getMaxResults();
            int toSearch = maxResults < PAGE_SIZE ? maxResults : PAGE_SIZE;
            int start = inputReader.getStart();
            while (result.size() < maxResults) {
                String request = searchQuery(start, toSearch);
                LOGGER.debug("Sending request... {}", request);
                // need http protocol, set this as a Google bot agent :)
                final Document document = Jsoup.connect(request)
                        .userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                        .timeout(10000).get();
                Set<String> collect = document.select("cite").stream().map(Element::html).filter(c -> c.contains("linkedin")).collect(Collectors.toSet());
                if (collect.isEmpty()) {
                    return result;
                }
                result.addAll(collect);
                start += PAGE_SIZE;
            }
        } catch (Exception e) {
            LOGGER.error("Error while google", e);
        }
        return result;
    }

    private String searchQuery(int start, int maxResults) {
        try {
            return String.format(GOOGLE_SEARCH_TEMPLATE, encode(getSearchQuery(), "UTF-8"), start, maxResults);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void openWebPage() {
        openWebPage(searchQuery(inputReader.getStart(), inputReader.getMaxResults()));
    }

    public static void openWebPage(String query) {
        try {
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI(query));
            }
        } catch (Exception e) {
            LOGGER.error("Error while opening browser", e);
        }
    }

}