package com.socialreader.data_reader;

import com.socialreader.core.Profile;
import java.util.List;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Brad
 */
public class LinkedInScraper extends WebsiteScraper{
    
    private String linkedInUrl = null;
    private String firstName = null;
    private String middleName = null;
    private String lastName = null;
    private String title = null;
    private String location = null;
    private String industry = null;
    private String currentEmployer = null;
    private String previousEmployment = null;
    private String education = null;
    
    public LinkedInScraper(String linkedInUrl){
        this.linkedInUrl = linkedInUrl;
    }
    
    @Override
    public void parseHtml(){
        Elements linkedInOverviewElements = document.getElementsByClass("profile-overview-content");
        if(!linkedInOverviewElements.isEmpty()){
            Element linkedInOverviewElement = linkedInOverviewElements.get(0);
            scrapeNameData(linkedInOverviewElement);
            scrapeTitleData();
            scrapeLocationData(linkedInOverviewElement);
            scrapeIndustryData(linkedInOverviewElement);
            scrapeCurrentEmploymentData();
            scrapePreviousEmploymentData();
            scrapeEducationData();
            
        }
    }
   
    private void scrapeNameData(Element linkedInOverviewElement){
        Elements nameElements = linkedInOverviewElement.getElementsByClass("full-name");
        if(!nameElements.isEmpty()){
            Element nameElement = nameElements.get(0);
            String fullName = nameElement.text();
            String[] names = fullName.split("\\s+");
            if(names.length == 1){
                firstName = names[0];
            }
            else if(names.length == 2){
                firstName = names[0];
                lastName = names[1];
            }
            else{
                firstName = names[0];
                middleName = "";
                for(int i = 1; i< names.length-1; i++){
                    middleName += names[i] + " ";
                }
                middleName.trim();
                lastName = names[names.length-1];
            }
            //System.out.println(firstName + " " + lastName);
        }
    }
    
    private void scrapeTitleData(){
        Element headlineElement = document.getElementById("headline");
        if(headlineElement != null){
            title = headlineElement.text();
            //System.out.println(previousEmployment);
        }
    }
    
    private void scrapeLocationData(Element linkedInOverviewElement){
        Elements locationElements = linkedInOverviewElement.getElementsByClass("locality");
        if(!locationElements.isEmpty()){
            Element locationElement = locationElements.get(0);
            location = locationElement.text();
            //System.out.println(location);
        }
    }
    
    private void scrapeIndustryData(Element linkedInOverviewElement){
        Elements industryElements = linkedInOverviewElement.getElementsByClass("industry");
        if(!industryElements.isEmpty()){
            Element industryElement = industryElements.get(0);
            industry = industryElement.text();
            //System.out.println(industry);
        }
    }
    
    private void scrapeCurrentEmploymentData(){
        Element currentEmploymentHeaderElement = document.getElementById("overview-summary-current");
        if(currentEmploymentHeaderElement != null){
            Elements employmentElements = currentEmploymentHeaderElement.getElementsByTag("LI");
            if(!employmentElements.isEmpty()){
                Element currentEmploymentElement = employmentElements.get(0);
                currentEmployer = currentEmploymentElement.text();
                //System.out.println(previousEmployment);
            }
        }
    }
    
    private void scrapePreviousEmploymentData(){
        Element previousEmploymentHeaderElement = document.getElementById("overview-summary-past");
        if(previousEmploymentHeaderElement != null){
            Elements employmentElements = previousEmploymentHeaderElement.getElementsByTag("LI");
            if(!employmentElements.isEmpty()){
                previousEmployment = "";
                for(int i = 0; i< employmentElements.size(); i++){
                    Element previousEmploymentElement = employmentElements.get(i);
                    previousEmployment += previousEmploymentElement.text();
                }
                //System.out.println(previousEmployment);
            }
        }
    }
    
    private void scrapeEducationData(){
        Element educationHeaderElement = document.getElementById("overview-summary-education");
        if(educationHeaderElement != null){
            Elements educationElements = educationHeaderElement.getElementsByTag("LI");
            if(!educationElements.isEmpty()){
                education = "";
                for(int i = 0; i< educationElements.size(); i++){
                    Element educationElement = educationElements.get(i);
                    education += educationElement.text();
                }
                //System.out.println(education);
            }
        }
    }
    
    @Override
    public Profile generateProfile(){
        Profile profile = new Profile();
        profile.setLinkedInUrl(linkedInUrl);
        profile.setFirstName(firstName);
        profile.setMiddleName(middleName);
        profile.setLastName(lastName);
        profile.setTitle(title);
        profile.setLocation(location);
        profile.setIndustry(industry);
        profile.setCurrentEmployer(currentEmployer);
        profile.setPreviousEmployment(previousEmployment);
        profile.setEducation(education);
        return profile;
    }
    
    public static void main(String[] args) throws Exception{
        LinkedInScraper l = new LinkedInScraper("https://www.linkedin.com/pub/eric-conner/40/901/348");
        l.getHtml(l.linkedInUrl);
        l.parseHtml();
        l.generateProfile();
        //System.out.println(l.document.html());
    }
}
