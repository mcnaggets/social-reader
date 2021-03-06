package com.socialreader.core;

import com.socialreader.data_reader.EmailResolver;
import com.socialreader.data_reader.LinkedInScraper;
import com.socialreader.data_reader.PiplScraper;

import java.io.UnsupportedEncodingException;

/**
 * @author Brad
 */
public class ProfileBuilder {

    private String linkedInUrl = null;
    private Profile profile = null;
    //private ArrayList<WebsiteScraper> websiteScrapers = null;
    LinkedInScraper linkedInScraper = null;
    PiplScraper piplScraper = null;
    EmailResolver emailResolver = null;


    public ProfileBuilder(String linkedInUrl) {
        this.linkedInUrl = linkedInUrl;
    }

    public void initWebsiteScrapers() throws UnsupportedEncodingException {
        initLinkedInScraper();
//        initPiplScraper();
    }

    private void initPiplScraper() throws UnsupportedEncodingException {
        Profile linkedInProfile = linkedInScraper.generateProfile();
        piplScraper = new PiplScraper(linkedInProfile);
        String profileUrl = piplScraper.getPiplProfileUrl();
        if (!profileUrl.isEmpty()) {
            piplScraper.initHtml(profileUrl);
            piplScraper.parseHtml();
        }
    }

    private void initLinkedInScraper() {
        linkedInScraper = new LinkedInScraper(linkedInUrl);
        linkedInScraper.initHtml(linkedInUrl);
        linkedInScraper.parseHtml();
    }

    public void generateProfileFromWebsiteScrapers() {
        profile = linkedInScraper.generateProfile();
//        profile = Profile.merge(linkedInScraper.generateProfile(), piplScraper.generateProfile());
    }

    public void initEmailResolver() {
        emailResolver = new EmailResolver(profile);
        profile.setBusinessEmail(emailResolver.getBusinessEmail());
    }

    public void enrichProfileWithEmailResolver() {
        profile = Profile.merge(profile, emailResolver.generateProfile());
    }

    public Profile getProfile() {
        return profile;
    }

}
