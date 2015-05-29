package com.socialreader.data_reader;

import com.socialreader.core.Profile;
import java.util.ArrayList;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Brad
 */
public class PiplScraper extends WebsiteScraper{
    
    private String piplSearchUrl = "https://pipl.com/search/?q=<first_name>+<last_name>&l=&sloc=&in=6";
    private String piplUrl = null;
    private ArrayList<String> usernames = null;
    private String phone1 = null;
    private String phone2 = null;
    private String phone3 = null;
    
    public PiplScraper(Profile profile){
        this.profile = profile;
    }
    
    @Override
    public void parseHtml(){
        
        Element piplProfileElement = document.getElementById("profile_container_middle");
        if(piplProfileElement != null){
            scrapeUsernames(piplProfileElement);
            scrapePhones(piplProfileElement);
        }
    }
    
    private void scrapeUsernames(Element piplProfileElement){
        Elements usernameElements = piplProfileElement.getElementsByClass("usernames");
        if(!usernameElements.isEmpty()){
            Element usernameElement = usernameElements.get(0);
            String usernames = usernameElement.text();
            String[] allUsernames = usernames.split(",");
            ArrayList<String> u = new ArrayList<String>();
            for(String s: allUsernames){
                u.add(s);
            }
            profile.setUsernames(u);
        }
    }
    
    private void scrapePhones(Element piplProfileElement){
        Elements phoneElements = piplProfileElement.getElementsByClass("phones");
        if(!phoneElements.isEmpty()){
            Element phoneElement = phoneElements.get(0);
            String phones = phoneElement.text();
            String[] allPhones = phones.split(",");
            if(allPhones.length > 0){
               profile.setPhone1(allPhones[0]);
            }
            if(allPhones.length > 1){
                profile.setPhone2(allPhones[1]);
            }
            if(allPhones.length > 2){
                profile.setPhone3(allPhones[2]);
            }
        }
    }
    
    public String getPiplProfileUrl(){
        String profileUrl = doPiplSearchForPerson();
        return profileUrl;
    }
    
    private String doPiplSearchForPerson(){
        String searchUrl = piplSearchUrl;
        String firstName = profile.getFirstName();
        String lastName = profile.getLastName();
        searchUrl = searchUrl.replace("<first_name>", firstName);
        searchUrl = searchUrl.replace("<last_name>", lastName);
        getHtml(searchUrl);
        String url = getFirstProfileFromSearchResults();
        return url;
    }
    
    private String getFirstProfileFromSearchResults(){
        String url = "https://pipl.com/search/";
        //System.out.println(document.html());
        Elements piplSearchResultsElements = document.getElementsByClass("content");
        if(!piplSearchResultsElements.isEmpty()){
            Element piplSearchResultsElement = piplSearchResultsElements.get(0);
            Elements links = piplSearchResultsElement.select("a[href]");
            if(links.size() > 0){
                url += links.get(0).attr("href");
                return url;
            }
        }
        return null;
    }
    
    public boolean isProfileCorrectPerson(String piplProfileUrl){
        getHtml(piplProfileUrl);
        Elements piplSearchResultsElements = document.getElementsByClass("person_content");
        if(!piplSearchResultsElements.isEmpty()){
            for(Element currentProfileElement: piplSearchResultsElements){
                Elements currentProfileElements = currentProfileElement.select("div.line1.truncate");
                if(currentProfileElements.size() > 0){
                    String currentProfileUrl = currentProfileElements.get(0).text();
                    if(currentProfileUrl.contains("linkedin.com")){
                        String linkedInProfileUrl = profile.getLinkedInUrl();
                        int beginIndex = linkedInProfileUrl.indexOf("linked");
                        linkedInProfileUrl = linkedInProfileUrl.substring(beginIndex);
                        currentProfileUrl.trim();
                        linkedInProfileUrl.trim();
                        if(currentProfileUrl.equals(linkedInProfileUrl)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public Profile generateProfile(){
        return profile;
    }
    
    public static void main(String[] args) throws Exception{
        PiplScraper p = new PiplScraper(Profile.getDummy());
        String piplProfileUrl = p.getPiplProfileUrl();
        if(piplProfileUrl != null){
            if(p.isProfileCorrectPerson(piplProfileUrl)){
                System.out.println("match for person");
                p.getHtml(piplProfileUrl);
                p.parseHtml();
                p.generateProfile();
            }
            else{
                System.out.println("cannot find match for person");
            }
        }
    }
    
}
