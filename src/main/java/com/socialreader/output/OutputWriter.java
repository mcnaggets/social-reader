package com.socialreader.output;

import com.socialreader.core.Profile;
import java.io.OutputStream;
import java.util.Set;

/**
 *
 * @author Brad
 */
public interface OutputWriter {
    
    public void writeProfileInformation(Set<Profile> profiles);
    
}
