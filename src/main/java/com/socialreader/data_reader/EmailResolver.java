package com.socialreader.data_reader;

import com.socialreader.core.Profile;
import com.socialreader.mail.Envelope;
import com.socialreader.mail.SMTPConnection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.socialreader.core.GooglePersonFinder.GOOGLE_SEARCH_TEMPLATE;
import static java.net.URLEncoder.encode;

/**
 * @author Brad
 */
public class EmailResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailResolver.class);

    private Profile profile = null;
    private ArrayList<String> personalEmailDomains = null;
    private static final Set<String> NOT_SUPPORTED_DOMAINS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public EmailResolver(Profile profile) {
        this.profile = profile;
        personalEmailDomains = new ArrayList<>();
        personalEmailDomains.add("@gmail.com");
        personalEmailDomains.add("@yahoo.com");
        personalEmailDomains.add("@hotmail.com");
        personalEmailDomains.add("@outlook.com");
    }

    public Profile generateProfile() {
        return profile;
    }

    public ArrayList<String> getPersonalEmails() {
        String firstName = profile.getFirstName();
        String middleName = profile.getMiddleName();
        String lastName = profile.getLastName();
        ArrayList<String> possibleEmails = new ArrayList<String>();
        for (String emailDomain : personalEmailDomains) {
            possibleEmails.addAll(getPossibleEmailsFromName(firstName, lastName, emailDomain));
            if (profile.getUsernames() != null) {
                for (String username : profile.getUsernames()) {
                    possibleEmails.addAll(getPossibleEmailsFromUsername(username, emailDomain));
                }
            }
        }
        return possibleEmails;
    }

    public List<String> getPossibleEmailsFromName(String firstName,
                                                  String lastName, String emailDomain) {
        List<String> possibleEmails = new LinkedList<>();
        if (firstName != null && lastName != null) {
            String firstInitial = "" + firstName.charAt(0);
            possibleEmails.add(firstName + "_" + lastName + emailDomain);
            possibleEmails.add(lastName + "_" + firstName + emailDomain);
            possibleEmails.add(firstName + "." + lastName + emailDomain);
            possibleEmails.add(lastName + "." + firstName + emailDomain);
            possibleEmails.add(firstName + "-" + lastName + emailDomain);
            possibleEmails.add(lastName + "-" + firstName + emailDomain);
            possibleEmails.add(firstName + lastName + emailDomain);
            possibleEmails.add(lastName + firstName + emailDomain);
            possibleEmails.add(firstInitial + lastName + emailDomain);
            possibleEmails.add(firstInitial + "." + lastName + emailDomain);
            possibleEmails.add(firstInitial + "_" + lastName + emailDomain);
            possibleEmails.add(firstInitial + "-" + lastName + emailDomain);
            return possibleEmails;
        } else {
            return new LinkedList<>();
        }
    }

    public List<String> getPossibleEmailsFromUsername(String username, String emailDomain) {
        List<String> possibleEmails = new ArrayList<String>();
        if (username != null) {
            possibleEmails.add(username + emailDomain);
            return possibleEmails;
        } else {
            return null;
        }

    }

    public String getBusinessEmail() {
        if (profile.getCurrentEmployer() == null) {
            return "";
        }
        final String domain = getCompanyDomain(profile.getCurrentEmployer());
        if (NOT_SUPPORTED_DOMAINS.contains(domain)) {
            return "";
        }
        try {
            final List<String> emails = getPossibleEmailsFromName(profile.getFirstName(), profile.getLastName(), "@" + domain);
            for (String email : emails) {
                if (pingDomain(domain, email)) {
                    return email;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Domain {} is not supported for mx handshake", domain);
            NOT_SUPPORTED_DOMAINS.add(domain);
        }
        return "";
    }

    private boolean pingDomain(String domain, String email) throws IOException {
        if (!domain.isEmpty()) {
            final Envelope envelope = new Envelope(domain, "test@gmail.com", true);
            try (SMTPConnection connection = new SMTPConnection(envelope)) {
                try {
                    return connection.ping(email);
                } catch (IOException x) {
                    return false;
                }
            }
        }
        return false;
    }

    private String getCompanyDomain(String company) {
        try {
            final String request = String.format(GOOGLE_SEARCH_TEMPLATE, encode(company, "UTF-8"), 1);
            final Document document = Jsoup.connect(request)
                    .userAgent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
                    .timeout(1000).get();

            final Optional<String> cites = document.select("cite").stream().map(Element::text).findFirst();
            if (cites.isPresent()) {
                return getDomainName(cites.get());
            }
        } catch (Exception x) {
            LOGGER.error("Error parsing email", x);
        }
        return "";
    }

    public String getDomainName(String url) {
        url = url.replaceAll("/", "");
        return url.startsWith("www.") ? url.substring(4) : url;
    }

    public static void main(String[] args) {
        try {
            new EmailResolver(Profile.getDummy()).getBusinessEmail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
