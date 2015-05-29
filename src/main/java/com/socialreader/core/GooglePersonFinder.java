package com.socialreader.core;

import com.google.gson.Gson;
import com.socialreader.input.InputReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Brad
 */
public class GooglePersonFinder {
    
    protected String firstName = null;
    protected String lastName = null;
    protected ArrayList<String> titles = null;
    protected ArrayList<String> companies = null;
    protected ArrayList<String> schools = null;
    protected ArrayList<String> locations = null;
    protected ArrayList<String> industries = null;
    protected ArrayList<String> keyWords = null;
    
    private int maxResults = 48;
    private String searchQuery = "site:linkedin.com/in/ OR site:linkedin.com/pub/ -site:linkedin.com/pub/dir/";
    
    public void resetSearchQuery(){
        searchQuery = "site:linkedin.com/in/ OR site:linkedin.com/pub/ -site:linkedin.com/pub/dir/";
    }
    
    public void addTitles(){
        String titleString = "";
        String title = null;
        for(int i = 0; i< titles.size(); i++){
            title = titles.get(i);
            if(i > 0){
                titleString += "OR ";
            }
            titleString += "\""+title+" * * Present\" ";
        }
        searchQuery = titleString + searchQuery;
    }
    
    public void addCompanies(){
        addOrDelimitedList(companies);
    }
    
    public void addSchools(){
        addOrDelimitedList(schools);
    }
    
    public void addLocations(){
        addOrDelimitedList(locations);
    }
    
    public void addIndustries(){
        addOrDelimitedList(industries);
    }
    
    public void addKeyWords(){
        String logicString = "";
        String currentValue = null;
        for(int i = 0; i< keyWords.size(); i++){
            currentValue = keyWords.get(i);
            logicString += currentValue+" ";
        }
        searchQuery = logicString + searchQuery;
    }
    
    private void addOrDelimitedList(ArrayList<String> valuesList){
        String logicString = "";
        String currentValue = null;
        for(int i = 0; i< valuesList.size(); i++){
            currentValue = valuesList.get(i);
            if(i > 0){
                logicString += "OR ";
            }
            logicString += "\""+currentValue+"\" ";
        }
        searchQuery = logicString + searchQuery;
    }
    
    public String getSearchQuery(){
        //System.out.println(searchQuery);
        //System.exit(0);
        return searchQuery;
    }
    
    public void configureSearch(InputReader inputReader){
        this.firstName = inputReader.getFirstName();
        this.lastName = inputReader.getLastName();
        this.titles = inputReader.getTitles();
        this.companies = inputReader.getCompanies();
        this.schools = inputReader.getSchools();
        this.locations = inputReader.getLocations();
        this.industries = inputReader.getIndustries();
        this.keyWords = inputReader.getkeyWords();
        configureSearch();
    }
    
    private void configureSearch() {
        addLocations();
        addIndustries();
        addTitles();
    }
    
    public Set<ProfileBuilder> generateProfileBuilders(){
        return doGoogleSearch();
    }
    
    private Set<ProfileBuilder> doGoogleSearch() {
        Set<ProfileBuilder> profileBuilders = new HashSet<ProfileBuilder>();
        try {
            for (int resultIndex = 0; resultIndex < maxResults; resultIndex += 8) {
                String searchAddress = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=8&start=" + resultIndex + "&q=";
                String searchQuery = getSearchQuery();
                String charset = "UTF-8";
                URL url = new URL(searchAddress + URLEncoder.encode(searchQuery, charset));
                Reader reader = new InputStreamReader(url.openStream(), charset);
                GoogleResults googleResults = new Gson().fromJson(reader, GoogleResults.class);
                GoogleResults.ResponseData responseData = googleResults.getResponseData();
                if (responseData != null) {
                    List<GoogleResults.Result> resultsList = responseData.getResults();
                    for (int currentResult = 0; currentResult < resultsList.size(); currentResult++) {
                        String linkedInUrl = resultsList.get(currentResult).getUrl();
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

class GoogleResults{
 
    private ResponseData responseData;
    public ResponseData getResponseData() { return responseData; }
    public void setResponseData(ResponseData responseData) { this.responseData = responseData; }
    public String toString() { return "ResponseData[" + responseData + "]"; }
 
    static class ResponseData {
        private List<Result> results;
        public List<Result> getResults() { return results; }
        public void setResults(List<Result> results) { this.results = results; }
        public String toString() { return "Results[" + results + "]"; }
    }
 
    static class Result {
        private String url;
        private String title;
        public String getUrl() { return url; }
        public String getTitle() { return title; }
        public void setUrl(String url) { this.url = url; }
        public void setTitle(String title) { this.title = title; }
        public String toString() { return "Result[url:" + url +",title:" + title + "]"; }
    }
}
