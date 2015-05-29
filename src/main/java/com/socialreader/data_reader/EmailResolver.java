package com.socialreader.data_reader;

import com.dominicsayers.isemail.IsEMail;
import com.dominicsayers.isemail.dns.DNSLookupException;
import com.socialreader.core.Profile;
import java.util.ArrayList;

/**
 *
 * @author Brad
 */
public class EmailResolver {
    
    private Profile profile = null;
    private ArrayList<String> personalEmailDomains = null;
    
    public EmailResolver(Profile profile){
        this.profile = profile;
        personalEmailDomains = new ArrayList<String>();
        personalEmailDomains.add("@gmail.com");
        personalEmailDomains.add("@yahoo.com");
        personalEmailDomains.add("@hotmail.com");
        personalEmailDomains.add("@outlook.com");
    }
    
    public Profile generateProfile(){
        return profile;
    }
    
    public ArrayList<String> getPersonalEmails(){
        String firstName = profile.getFirstName();
        String middleName = profile.getMiddleName();
        String lastName = profile.getLastName();
        ArrayList<String> possibleEmails = new ArrayList<String>();
        for(String emailDomain: personalEmailDomains){
            possibleEmails.addAll(getPossibleEmailsFromName(firstName, middleName, lastName, emailDomain));
            if(profile.getUsernames() != null){
                for(String username: profile.getUsernames()){
                    possibleEmails.addAll(getPossibleEmailsFromUsername(username, emailDomain));
                }
            }
        }
        return possibleEmails;
    }
    
    public ArrayList<String> getPossibleEmailsFromName(String firstName, String middleName,
            String lastName, String emailDomain){
        ArrayList<String> possibleEmails = new ArrayList<String>();
        if(firstName != null && lastName != null){
            String firstInitial = ""+firstName.charAt(0);
            String lastInitial = ""+lastName.charAt(0);
            possibleEmails.add(firstName + lastName + emailDomain);
            possibleEmails.add(firstName +"."+ lastName + emailDomain);
            possibleEmails.add(firstName +"_"+ lastName + emailDomain);
            possibleEmails.add(firstName +"-"+ lastName + emailDomain);
            possibleEmails.add(lastName + firstName + emailDomain);
            possibleEmails.add(lastName +"."+ firstName + emailDomain);
            possibleEmails.add(lastName +"_"+ firstName + emailDomain);
            possibleEmails.add(lastName +"-"+ firstName + emailDomain);
            possibleEmails.add(firstInitial + lastName + emailDomain);
            possibleEmails.add(firstInitial +"."+ lastName + emailDomain);
            possibleEmails.add(firstInitial +"_"+ lastName + emailDomain);
            possibleEmails.add(firstInitial +"-"+ lastName + emailDomain);
            return possibleEmails;
        }
        else{
            return null;
        }
    }
    
    public ArrayList<String> getPossibleEmailsFromUsername(String username, String emailDomain){
        ArrayList<String> possibleEmails = new ArrayList<String>();
        if(username != null){
            possibleEmails.add(username + emailDomain);
            return possibleEmails;
        }
        else{
            return null;
        }
        
    }
    
    public void getBusinessEmails(){
        
    }
    
    public static void main(String[] args){
        try{
            Profile profile = Profile.getDummy();
            ArrayList<String> personalEmails = new ArrayList<String>();
            EmailResolver er = new EmailResolver(profile);
            personalEmails = er.getPersonalEmails();
            for(String email: personalEmails){
                if (IsEMail.is_email(email)) {
                    System.out.println(email + " is a valid email address");
                } else {
                    System.out.println(email + " is NOT a valid email address");
                }
            }
            ArrayList<String> businessEmails = new ArrayList<String>();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
