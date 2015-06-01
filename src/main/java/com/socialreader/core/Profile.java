package com.socialreader.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Brad
 */
public class Profile {

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
    private List<String> usernames = null;
    private String phone1 = null;
    private String phone2 = null;
    private String phone3 = null;
    private String email1 = null;
    private String email2 = null;
    private String email3 = null;

    public Profile() {
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getIndustry() {
        return industry;
    }

    public String getCurrentEmployer() {
        return currentEmployer;
    }

    public String getPreviousEmployment() {
        return previousEmployment;
    }

    public String getEducation() {
        return education;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public String getEmail1() {
        return email1;
    }

    public String getEmail2() {
        return email2;
    }

    public String getEmail3() {
        return email3;
    }

    public String getLinkedInUrl() {
        return linkedInUrl;
    }

    //public void setLinkedInUrl(String linkedInUrl) {this.linkedInUrl = linkedInUrl;}
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setCurrentEmployer(String currentEmployer) {
        this.currentEmployer = currentEmployer;
    }

    public void setPreviousEmployment(String previousEmployment) {
        this.previousEmployment = previousEmployment;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public void setEmail1(String email1) {
        this.email1 = email1;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
    }

    public void setEmail3(String email3) {
        this.email3 = email3;
    }

    public void setLinkedInUrl(String linkedInUrl) {
        this.linkedInUrl = linkedInUrl;
    }

    public static String getHeader() {
        return "LinkedIn;First Name;Middle Name;Last Name;Title;Company;Education;Location;"
                + "Industry;E-Mail 1;E-Mail 2;E-Mail 3;Phone 1;Phone 2;Phone 3";
    }

    public String toString() {
        return linkedInUrl + ";" + firstName + ";" + middleName + ";" + lastName + ";" + title + ";" + currentEmployer + ";" + education + ";"
                + location + ";" + industry + ";" + email1 + ";" + email2 + ";" + email3 + ";" + phone1 + ";" + phone2 + ";" + phone3;
    }

    public static Profile getDummy() {
        ArrayList<String> usernames = new ArrayList<String>();
        usernames.add("bpask1337");
        Profile profile = new Profile();
        //profile.linkedInUrl = "https://www.linkedin.com/pub/brad-paskewitz/45/46a/bb7";
        profile.linkedInUrl = "https://www.linkedin.com/pub/bradford-paskewitz/0/684/b59";
        profile.firstName = "Brad";
        profile.lastName = "Paskewitz";
        profile.title = "Director of Technology";
        profile.currentEmployer = "Paskewitz Asset Management";
        profile.education = "Lehigh University";
        profile.location = "Greater Los Angeles Area";
        profile.industry = "Investment Management";
        profile.setUsernames(usernames);
        profile.email1 = "bpask1337@gmail.com";
        profile.email2 = "bhp212@lehigh.edu";
        profile.email3 = null;
        profile.phone1 = "609-731-8820";
        profile.phone2 = "609-918-1907";
        profile.phone3 = null;
        return profile;
    }

    public static Profile merge(Profile first, Profile second) {
        Profile merged = new Profile();
        merged.setLinkedInUrl(first.getLinkedInUrl());
        merged = mergeName(merged, first, second);
        merged = mergeTitle(merged, first, second);
        merged = mergeLocation(merged, first, second);
        merged = mergeIndustry(merged, first, second);
        merged = mergeEmployment(merged, first, second);
        merged = mergeEducation(merged, first, second);
        merged = mergePhones(merged, first, second);
        merged = mergeEmails(merged, first, second);
        return merged;
    }

    private static Profile mergeName(Profile merged, Profile first, Profile second) {
        merged.firstName = first.firstName;
        merged.middleName = first.middleName;
        merged.lastName = first.lastName;
        if (first.firstName == null) {
            merged.firstName = second.firstName;
        }
        if (first.middleName == null) {
            merged.middleName = second.middleName;
        }
        if (first.lastName == null) {
            merged.lastName = second.lastName;
        }
        return merged;
    }

    private static Profile mergeTitle(Profile merged, Profile first, Profile second) {
        merged.title = first.title;
        if (first.title == null) {
            merged.title = second.title;
        }
        return merged;
    }

    private static Profile mergeLocation(Profile merged, Profile first, Profile second) {
        merged.location = first.location;
        if (first.location == null) {
            merged.location = second.location;
        }
        return merged;
    }

    private static Profile mergeIndustry(Profile merged, Profile first, Profile second) {
        merged.industry = first.industry;
        if (first.industry == null) {
            merged.industry = second.industry;
        }
        return merged;
    }

    private static Profile mergeEmployment(Profile merged, Profile first, Profile second) {
        merged.currentEmployer = first.currentEmployer;
        merged.previousEmployment = first.previousEmployment;
        if (first.currentEmployer == null) {
            merged.currentEmployer = second.currentEmployer;
        }
        if (first.previousEmployment == null) {
            merged.previousEmployment = second.previousEmployment;
        }
        return merged;
    }

    private static Profile mergeEducation(Profile merged, Profile first, Profile second) {
        merged.education = first.education;
        if (first.education == null) {
            merged.education = second.education;
        }
        return merged;
    }

    private static Profile mergePhones(Profile merged, Profile first, Profile second) {
        merged.phone1 = first.phone1;
        merged.phone2 = first.phone2;
        merged.phone3 = first.phone3;
        if (first.phone1 == null) {
            merged.phone1 = second.phone1;
        }
        if (first.phone2 == null) {
            merged.phone2 = second.phone2;
        }
        if (first.phone3 == null) {
            merged.phone3 = second.phone3;
        }
        return merged;
    }

    private static Profile mergeEmails(Profile merged, Profile first, Profile second) {
        merged.email1 = first.email1;
        merged.email2 = first.email2;
        merged.email3 = first.email3;
        if (first.email1 == null) {
            merged.email1 = second.email1;
        }
        if (first.email2 == null) {
            merged.email2 = second.email2;
        }
        if (first.email3 == null) {
            merged.email3 = second.email3;
        }
        return merged;
    }

}
