package com.socialreader.input;
import java.util.ArrayList;

/**
 *
 * @author Brad
 */
public class DummyInputReader extends InputReader{
    
    public DummyInputReader(){
        super(10);
        locations = new ArrayList<>();
        locations.add("Greater Los Angeles Area");
        industries = new ArrayList<>();
        //industries.add("Computer Software");
        //industries.add("Internet");
        industries.add("Investment Management");
        titles = new ArrayList<>();
        titles.add("Technical Recruiter");
        titles.add("HR Manager");
        titles.add("Human Resources Manager");
        //keyWords = new ArrayList<String>();
        //keyWords.add("HR");
    }
}
