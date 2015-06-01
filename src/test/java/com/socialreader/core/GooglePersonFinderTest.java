package com.socialreader.core;

import com.socialreader.input.DummyInputReader;
import com.socialreader.input.InputReader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GooglePersonFinderTest {

    private GooglePersonFinder googlePersonFinder = null;

    @Before
    public void init() {
        googlePersonFinder = new GooglePersonFinder(new DummyInputReader());
    }

    @Test
    public void configureSearch() {
        final String searchQuery = googlePersonFinder.getSearchQuery();
        assertNotNull(searchQuery);
    }


}