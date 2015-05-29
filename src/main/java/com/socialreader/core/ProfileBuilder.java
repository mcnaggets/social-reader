package com.socialreader.core;

import com.socialreader.data_reader.EmailResolver;
import com.socialreader.data_reader.PiplScraper;
import com.socialreader.data_reader.LinkedInScraper;
import com.socialreader.data_reader.WebsiteScraper;
import java.util.ArrayList;

/**
 *
 * @author Brad
 */
public class ProfileBuilder {
    
    private String linkedInUrl = null;
    private Profile profile = null;
    //private ArrayList<WebsiteScraper> websiteScrapers = null;
    LinkedInScraper linkedInScraper = null;
    PiplScraper piplScraper = null;
    EmailResolver emailResolver = null;
    
    
    public ProfileBuilder(String linkedInUrl){
        this.linkedInUrl = linkedInUrl;
    }
    
    public void initWebsiteScrapers(){
        linkedInScraper = new LinkedInScraper(linkedInUrl);
        linkedInScraper.getHtml(linkedInUrl);
        linkedInScraper.parseHtml();
        Profile linkedInProfile = linkedInScraper.generateProfile();
        piplScraper = new PiplScraper(linkedInProfile);
        piplScraper.getHtml(piplScraper.getPiplProfileUrl());
        piplScraper.parseHtml();
    }
    
    public void generateProfileFromWebsiteScrapers(){
        linkedInScraper.generateProfile();
        profile = Profile.merge(linkedInScraper.generateProfile(), piplScraper.generateProfile());
    }
    
    public void initEmailResolver(){
        emailResolver = new EmailResolver(profile);
        emailResolver.getPersonalEmails();
        emailResolver.getBusinessEmails();
    }
    
    public void enrichProfileWithEmailResolver(){
        profile = Profile.merge(profile, emailResolver.generateProfile());
    }
    
    public Profile getProfile(){
        return profile;
    }

}
