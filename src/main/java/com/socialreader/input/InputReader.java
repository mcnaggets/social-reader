package com.socialreader.input;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Brad
 */
public abstract class InputReader {
    
    protected String firstName = null;
    protected String lastName = null;
    protected ArrayList<String> titles = null;
    protected ArrayList<String> companies = null;
    protected ArrayList<String> schools = null;
    protected ArrayList<String> locations = null;
    protected ArrayList<String> industries = null;
    protected ArrayList<String> keyWords = null;
    
    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public ArrayList<String> getTitles(){return titles;}
    public ArrayList<String> getCompanies(){return companies;}
    public ArrayList<String> getSchools(){return schools;}
    public ArrayList<String> getLocations(){return locations;}
    public ArrayList<String> getIndustries(){return industries;}
    public ArrayList<String> getkeyWords(){return keyWords;}
    public void setFirstName(String firstName){this.firstName = firstName;}
    public void setLastName(String lastName){this.lastName = lastName;}
    public void setTitles(ArrayList<String> titles){this.titles = titles;}
    public void setCompanies(ArrayList<String> companies){this.companies = companies;}
    public void setSchools(ArrayList<String> schools){this.schools = schools;}
    public void setLocations(ArrayList<String> locations){this.locations = locations;}
    public void setIndustries(ArrayList<String> industries){this.industries = industries;}
    public void setKeyWords(ArrayList<String> keyWords){this.keyWords = keyWords;}
    
}
