package com.socialreader.core;

import com.socialreader.input.DummyInputReader;
import com.socialreader.input.InputReader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GooglePersonFinderTest {

    private InputReader inputReader = null;
    private GooglePersonFinder googlePersonFinder = null;

    @Before
    public void init() {
        inputReader = new DummyInputReader();
        googlePersonFinder = new GooglePersonFinder();
    }

    @Test
    public void configureSearch() {
        googlePersonFinder.configureSearch(inputReader);
        final String searchQuery = googlePersonFinder.getSearchQuery();
        assertNotNull(searchQuery);
    }


}