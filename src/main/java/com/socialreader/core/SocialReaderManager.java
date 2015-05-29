package com.socialreader.core;

import com.socialreader.input.DummyInputReader;
import com.socialreader.input.InputReader;
import com.socialreader.output.CsvOutputWriter;
import com.socialreader.output.OutputWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
 
public class SocialReaderManager {
 
    private String outputFile = "/users/brad/desktop/socialReaderResults.csv";
    private InputReader inputReader = null;
    private GooglePersonFinder googlePersonFinder = null;
    private Set<ProfileBuilder> profileBuilders = null;
    private Set<Profile> profiles = null;
    private OutputWriter outputWriter = null;
    
    public SocialReaderManager(){}
    
    public void init(){
        inputReader = new DummyInputReader();
        googlePersonFinder = new GooglePersonFinder();
        profileBuilders = new HashSet<ProfileBuilder>();
        outputWriter = new CsvOutputWriter(outputFile);
        profiles = new HashSet<Profile>();
    }
    
    public void run(){
        init();
        googlePersonFinder.configureSearch(inputReader);
        //System.out.println(googlePersonFinder.getSearchQuery());
        profileBuilders = googlePersonFinder.generateProfileBuilders();
        for(ProfileBuilder profileBuilder: profileBuilders){
            profileBuilder.initWebsiteScrapers();
            profileBuilder.generateProfileFromWebsiteScrapers();
            //profileBuilder.initEmailResolver();
            //profileBuilder.enrichProfileWithEmailResolver();
            Profile profile = profileBuilder.getProfile();
            profiles.add(profile);
            
            //System.out.println("finished building profile for: " + profile.getFirstName() + " " + profile.getLastName());
        }
        outputWriter.writeProfileInformation(profiles);
    }
    
    public static void main(String[] args){
        SocialReaderManager s = new SocialReaderManager();
        s.run();
    }
}
 
 
