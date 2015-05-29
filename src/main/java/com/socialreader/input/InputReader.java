package com.socialreader.input;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Brad
 */
public abstract class InputReader {

    protected String firstName = "";
    protected String lastName = "";
    protected List<String> titles = new LinkedList<>();
    protected List<String> companies = new LinkedList<>();
    protected List<String> schools = new LinkedList<>();
    protected List<String> locations = new LinkedList<>();
    protected List<String> industries = new LinkedList<>();
    protected List<String> keyWords = new LinkedList<>();

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<String> getTitles() {
        return titles;
    }

    public List<String> getCompanies() {
        return companies;
    }

    public List<String> getSchools() {
        return schools;
    }

    public List<String> getLocations() {
        return locations;
    }

    public List<String> getIndustries() {
        return industries;
    }

    public List<String> getKeyWords() {
        return keyWords;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public void setCompanies(List<String> companies) {
        this.companies = companies;
    }

    public void setSchools(List<String> schools) {
        this.schools = schools;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public void setIndustries(List<String> industries) {
        this.industries = industries;
    }

    public void setKeyWords(List<String> keyWords) {
        this.keyWords = keyWords;
    }

}
