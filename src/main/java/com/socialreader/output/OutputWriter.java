package com.socialreader.output;

import com.socialreader.core.Profile;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Brad
 */
public interface OutputWriter {
    
    void writeProfileInformation(Collection<Profile> profiles);
    
}
