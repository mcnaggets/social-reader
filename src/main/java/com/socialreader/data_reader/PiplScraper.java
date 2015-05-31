package com.socialreader.data_reader;

import com.socialreader.core.Profile;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Brad
 */
public class PiplScraper extends WebsiteScraper {

    private final static String PIPL_SEARCH_URL = "https://pipl.com/search/?q=%s+%s&l=%s&sloc=&in=6";
    private String piplUrl = "";

    public PiplScraper(Profile profile) {
        this.profile = profile;
    }

    @Override
    public void parseHtml() {
        if (isProfileCorrectPerson(piplUrl)) {
            Element piplProfileElement = document.getElementById("profile_container_middle");
            if (piplProfileElement != null) {
                scrapeUsernames(piplProfileElement);
                scrapePhones(piplProfileElement);
            }
        }
    }

    private void scrapeUsernames(Element piplProfileElement) {
        Elements usernameElements = piplProfileElement.getElementsByClass("usernames");
        if (!usernameElements.isEmpty()) {
            String[] names = usernameElements.get(0).text().split(",");
            profile.setUsernames(Arrays.stream(names).collect(Collectors.toList()));
        }
    }

    private void scrapePhones(Element piplProfileElement) {
        Elements phoneElements = piplProfileElement.getElementsByClass("phones");
        if (!phoneElements.isEmpty()) {
            Element phoneElement = phoneElements.get(0);
            String phones = phoneElement.text();
            String[] allPhones = phones.split(",");
            if (allPhones.length > 0) {
                profile.setPhone1(allPhones[0]);
            }
            if (allPhones.length > 1) {
                profile.setPhone2(allPhones[1]);
            }
            if (allPhones.length > 2) {
                profile.setPhone3(allPhones[2]);
            }
        }
    }

    public String getPiplProfileUrl() {
        return doPiplSearchForPerson();
    }

    private String doPiplSearchForPerson() {
        String searchUrl = String.format(PIPL_SEARCH_URL, profile.getFirstName(), profile.getLastName(), profile.getLocation());
        getHtml(searchUrl);
        return getFirstProfileFromSearchResults();
    }

    private String getFirstProfileFromSearchResults() {
        String url = "https://pipl.com/search/";
        //System.out.println(document.html());
        Elements piplSearchResultsElements = document.getElementsByClass("content");
        if (!piplSearchResultsElements.isEmpty()) {
            Element piplSearchResultsElement = piplSearchResultsElements.get(0);
            Elements links = piplSearchResultsElement.select("a[href]");
            if (links.size() > 0) {
                url += links.get(0).attr("href");
                return url;
            }
        }
        return "";
    }

    public boolean isProfileCorrectPerson(String piplProfileUrl) {
        if (piplProfileUrl.isEmpty()) return false;
        Elements piplSearchResultsElements = document.getElementsByClass("person_content");
        if (!piplSearchResultsElements.isEmpty()) {
            for (Element currentProfileElement : piplSearchResultsElements) {
                Elements currentProfileElements = currentProfileElement.select("div.line1.truncate");
                if (currentProfileElements.size() > 0) {
                    String currentProfileUrl = currentProfileElements.get(0).text();
                    if (currentProfileUrl.contains("linkedin.com")) {
                        String linkedInProfileUrl = profile.getLinkedInUrl();
                        int beginIndex = linkedInProfileUrl.indexOf("linked");
                        linkedInProfileUrl = linkedInProfileUrl.substring(beginIndex);
                        if (currentProfileUrl.equals(linkedInProfileUrl)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void getHtml(String websiteUrl) {
        this.piplUrl = websiteUrl;
        super.getHtml(websiteUrl);
    }

    @Override
    public Profile generateProfile() {
        return profile;
    }

    public static void main(String[] args) throws Exception {
        PiplScraper p = new PiplScraper(Profile.getDummy());
        String piplProfileUrl = p.getPiplProfileUrl();
        if (piplProfileUrl != null) {
            if (p.isProfileCorrectPerson(piplProfileUrl)) {
                System.out.println("match for person");
                p.getHtml(piplProfileUrl);
                p.parseHtml();
                p.generateProfile();
            } else {
                System.out.println("cannot find match for person");
            }
        }
    }

}
