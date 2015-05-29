package com.socialreader.output;

import com.socialreader.core.Profile;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Brad
 */
public class CsvOutputWriter implements OutputWriter{
    
    PrintWriter printWriter = null;
    
    public CsvOutputWriter(String path){
        try{
            printWriter = new PrintWriter(path);
        }catch(Exception e){
            System.out.println("invalid output file");
        }
    }
    
    public void writeProfileInformation(Set<Profile> profiles){
        printWriter.println(Profile.getHeader());
        for(Profile profile: profiles){
            printWriter.println(profile.toString());
        }
        printWriter.close();
    }
    
    public static void main(String[] args){
        String path = "/users/brad/desktop/testCSV.csv";
        Profile profile = Profile.getDummy();
        Set<Profile> profiles = new HashSet<Profile>();
        profiles.add(profile);
        try{
            CsvOutputWriter csvOutputWriter = new CsvOutputWriter(path);
            csvOutputWriter.writeProfileInformation(profiles);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
