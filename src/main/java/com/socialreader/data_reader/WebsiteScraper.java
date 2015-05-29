package com.socialreader.data_reader;

import com.socialreader.core.Profile;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Brad
 */
public abstract class WebsiteScraper {
    
    protected Profile profile = null;
    protected Document document = null;
    
    public abstract void parseHtml();
    public abstract Profile generateProfile();
    
    public void getHtml(String websiteUrl){
	try{
            document = Jsoup.connect(websiteUrl).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").referrer("http://www.google.com").timeout(0).get();
	}catch(Exception e) {
            e.printStackTrace();
	}
    }
}