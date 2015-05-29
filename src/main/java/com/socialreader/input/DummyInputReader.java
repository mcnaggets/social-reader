package com.socialreader.input;
import java.util.ArrayList;

/**
 *
 * @author Brad
 */
public class DummyInputReader extends InputReader{
    
    public DummyInputReader(){
        locations = new ArrayList<String>();
        locations.add("Greater Los Angeles Area");
        industries = new ArrayList<String>();
        //industries.add("Computer Software");
        //industries.add("Internet");
        industries.add("Investment Management");
        titles = new ArrayList<String>();
        titles.add("Technical Recruiter");
        titles.add("HR Manager");
        titles.add("Human Resources Manager");
        //keyWords = new ArrayList<String>();
        //keyWords.add("HR");
    }
}
